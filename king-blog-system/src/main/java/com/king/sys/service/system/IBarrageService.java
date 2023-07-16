package com.king.sys.service.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.common.module.domain.ResponseResult;
import com.king.common.module.enums.BarrageSayEnum;
import com.king.sys.bean.dto.BarrageRequest;
import com.king.sys.bean.entity.system.Barrage;
import com.king.sys.bean.vo.BarrageResponse;
import com.king.sys.bean.vo.BarrageVo;
import com.king.sys.bean.vo.MemorialVo;
import io.vavr.Tuple2;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * <p>
 *    barrage表服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 * @description 针对表【t_barrage(弹幕表)】的数据库操作Service
 **/
public interface IBarrageService extends IService<Barrage> {

    /**
     * save barrage
     * @param request 请求参数
     * @param type 类型 {@link BarrageSayEnum}
     * @return result
     */
    ResponseResult<Barrage> saveBarrage(BarrageRequest request, String type);

    /**
     * 评论
     * @param request 请求参数
     * @return result
     */
    ResponseResult<Barrage> comment(BarrageRequest request);

    /**
     * 回复
     * @param request 请求参数
     * @return result
     */
    ResponseResult<Barrage> reply(BarrageRequest request);

    /**
     * 分页查询
     * @param currentPage 当前页
     * @param size 页面大小
     * @return map
     */
    BarrageResponse<BarrageVo> pagingQuery(Integer currentPage, Integer size);

    /**
     * 获取 page info<br/>
     *  - total<br/>
     *  - pages
     * @param size
     * @return tuple
     */
    Tuple2<Integer, Integer> getPage(int size);

    /**
     * 查询一级评论
     * @param currentPage 当前页
     * @param size 页面大小
     * @param wrapper 查询条件
     * @return list
     */
    @Nullable
    Tuple2<Page<Barrage>, List<BarrageVo>> queryParent(int currentPage, int size, LambdaQueryWrapper<Barrage> wrapper);

    /**
     * 查询回复评论 (子级)
     * @param parentList 父级评论
     * @param wrapper 查询条件
     */
    void queryChildren(List<BarrageVo> parentList, LambdaQueryWrapper<Barrage> wrapper);

    /**
     * 获取弹幕(滑动模块)
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return response
     */
    @Nullable
    BarrageResponse<MemorialVo> getBarrage(Integer currentPage, Integer pageSize);

}
