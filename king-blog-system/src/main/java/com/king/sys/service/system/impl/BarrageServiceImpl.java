package com.king.sys.service.system.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.module.constant.CacheConstant;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ResponseResult;
import com.king.common.module.enums.BarrageSayEnum;
import com.king.common.utils.EntityUtil;
import com.king.sys.bean.dto.BarrageRequest;
import com.king.sys.bean.entity.system.Barrage;
import com.king.sys.bean.vo.BarrageResponse;
import com.king.sys.bean.vo.BarrageVo;
import com.king.sys.bean.vo.MemorialVo;
import com.king.sys.mapper.BarrageMapper;
import com.king.sys.service.system.IBarrageService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 *    针对表【t_barrage(弹幕表)】的数据库操作Service实现
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Service
public class BarrageServiceImpl extends ServiceImpl<BarrageMapper, Barrage>
        implements IBarrageService {

    @Autowired
    private BarrageMapper barrageMapper;

    @Override
    public ResponseResult<Barrage> saveBarrage(BarrageRequest request, String type) {
        // 创建 barrage 实体类
        Barrage barrage = new Barrage(request);
        return this.barrageMapper.insert(barrage) > 0  ? ResponseResult.success(barrage) : ResponseResult.fail();
        //return super.save(barrage) ? Result.success(barrage) : Result.fail();
    }

    @Override
    public ResponseResult<Barrage> comment(BarrageRequest request) {
        Integer lastInsertId = this.barrageMapper.findLastInsertId();
        request.setCommentId(lastInsertId == null ? 1 : lastInsertId + 1);
        return this.saveBarrage(request, BarrageSayEnum.COMMENT.getType());
    }

    @Override
    public ResponseResult<Barrage> reply(BarrageRequest request) {
        return this.saveBarrage(request, BarrageSayEnum.REPLY.getType());
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_30SECS, condition = "#currentPage > 0", key = "#currentPage + '[' + #size + ']'", unless = "#result == null")
    public BarrageResponse<BarrageVo> pagingQuery(Integer currentPage, Integer size) {
        // create a wrapper
        LambdaQueryWrapper<Barrage> wrapper = new LambdaQueryWrapper<>();

        /* execute query */
        Tuple2<Integer, Integer> page1 = this.getPage(size);
        Tuple2<Page<Barrage>, List<BarrageVo>> tuple2 = Optional.ofNullable(this.queryParent(currentPage, size, wrapper))
                .orElseThrow(() -> new IllegalArgumentException("paging query error. tuple2 undefined."));

        /* processing data */
        List<BarrageVo> parentList = tuple2._2;
        Page<Barrage> page2 = tuple2._1;
        this.queryChildren(parentList, wrapper);

        return new BarrageResponse<BarrageVo>()
                .setTotal((int) page2.getTotal())
                .setPages((int) page2.getPages())
                .setRecords(parentList)
                .setExtras(MapUtil.builder(new HashMap<String, Object>())
                        .put("pages", page1._1)
                        .put("total", page1._2)
                        .map());
    }

    @Override
    public Tuple2<Integer, Integer> getPage(int size){
        // get total
        Integer total = this.barrageMapper.findTotal();

        // get pages
        Integer pages = total / size;
        if (total % size != 0) {
            pages++;
        }

        return Tuple.of(pages, total);
    }

    @Override
    public Tuple2<Page<Barrage>, List<BarrageVo>> queryParent(int currentPage, int size, LambdaQueryWrapper<Barrage> wrapper) {
        Objects.requireNonNull(wrapper, "wrapper undefined.");

        // clear previous query conditions.
        wrapper.clear();

        // builder query conditions.
        currentPage = currentPage == 1 ? 0 : currentPage;
        wrapper.isNull(Barrage::getParentCommentId)
                .isNull(Barrage::getReplyUsername)
                .orderByDesc(Barrage::getCreateTime);
        //.last("limit " + currentPage + "," + size);

        // paging query
        Page<Barrage> page = super.page(new Page<>(currentPage, size), wrapper);

        AtomicReference<List<BarrageVo>> barrageVoList = new AtomicReference<>(new ArrayList<>(Constant.COMMENT_PAGE_SIZE));
        Optional.ofNullable(page.getRecords())
                .ifPresent(list -> barrageVoList.set(EntityUtil.toList(list, BarrageVo::new)));

        return Tuple.of(page, barrageVoList.get());
    }

    @Override
    public void queryChildren(List<BarrageVo> parentList, LambdaQueryWrapper<Barrage> wrapper) {
        Objects.requireNonNull(wrapper, "wrapper undefined.");

        // clear previous query conditions.
        wrapper.clear();

        // get comment module id list
        List<Integer> commentIdList = parentList.stream()
                .map(BarrageVo::getCommentId).collect(Collectors.toList());

        // builder query conditions.
        wrapper.isNotNull(Barrage::getParentCommentId).isNotNull(Barrage::getReplyUsername)
                .in(Barrage::getCommentId, commentIdList).orderByDesc(Barrage::getCreateTime)
                .last("limit " + Constant.TOTAL_REPLY_PAGE_SIZE);

        // processing children list
        Optional.ofNullable(super.list(wrapper))
                .ifPresent(list -> {
                    // grouping
                    Map<Integer, List<Barrage>> map = list.stream().collect(Collectors.groupingBy(Barrage::getCommentId));

                    // processing parent list
                    parentList.forEach(barrageVo -> Optional.ofNullable(map.get(barrageVo.getCommentId()))
                            .ifPresent(replyList -> {
                                // add reply list
                                barrageVo.setReplyList(replyList);
                                // change the value of more replies to true
                                this.changeMoreReply(map, barrageVo);
                            }));
                });
    }

    /**
     * 更改是否有更多回复的值为`true`
     * @param map 分组数据
     * @param barrageVo 需要更改的{@link BarrageVo}
     */
    private void changeMoreReply(Map<Integer, List<Barrage>> map, BarrageVo barrageVo){
        // get iterator
        Iterator<Integer> iterator = map.keySet().iterator();
        // has next
        if (iterator.hasNext()) {
            List<Barrage> barrageVoList = map.get(iterator.next());
            // 回复列表的数据大于规定展示的页面大小时, 则有更多回复; 否则没有更多回复, 默认值在构造函数中已做处理为`false`
            if (barrageVoList.size() > Constant.COMMENT_PAGE_SIZE) {
                barrageVo.setHasMoreReply(true);
            }
        }
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_30SECS, condition = "#currentPage > 0",  key = "#currentPage", unless = "#result == null")
    public BarrageResponse<MemorialVo> getBarrage(Integer currentPage, Integer pageSize) {
        // page query
        Page<Barrage> page = super.page(new Page<>(currentPage, pageSize));
        Assert.notNull(page, "page undefined.");

        // List<Barrage> convert in to List<MemorialVo>
        AtomicReference<List<MemorialVo>> collect = new AtomicReference<>();
        Optional.ofNullable(page.getRecords())
                .ifPresent(v -> collect.set(v.stream()
                        .map(item -> MemorialVo.builder()
                                .barrageNo(item.getBarrageNo()).formId(item.getFormId())
                                .message(item.getMessage()).avatar(item.getAvatar())
                                .email(item.getEmail()).line(0)
                                .build())
                        .collect(Collectors.toList())));

        return new BarrageResponse<MemorialVo>()
                .setTotal((int) page.getTotal())
                .setPages((int) page.getPages())
                .setRecords(collect.get())
                .setExtras(MapUtil.builder(new HashMap<String, Object>())
                        .put("hasNext", page.hasNext())
                        .put("hasPrevious", page.hasPrevious())
                        .build());

    }
}
