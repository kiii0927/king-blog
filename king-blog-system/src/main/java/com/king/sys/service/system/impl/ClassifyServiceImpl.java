package com.king.sys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.module.constant.CacheConstant;
import com.king.common.module.domain.ResponseCode;
import com.king.sys.bean.dto.SearchDto;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.entity.system.Classify;
import com.king.sys.mapper.BlogMapper;
import com.king.sys.mapper.ClassifyMapper;
import com.king.sys.service.system.IClassifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 *    分类表服务实现类
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Slf4j
@Service
public class ClassifyServiceImpl extends ServiceImpl<ClassifyMapper, Classify> implements IClassifyService {

    @Autowired
    private ClassifyMapper classifyMapper;

    @Autowired
    private BlogMapper blogMapper;

    /**
     * 查询所有类别
     * @return list
     */
    @Override
    public List<Classify> selectAll() {
        LambdaQueryWrapper<Classify> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Classify::getStatus, 1);

        List<Classify> list = classifyMapper.selectList(wrapper);
        Assert.notNull(list, ResponseCode.ERROR.getMessage());

        return list;
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_30SECS, key = "#type + '[' + #currentPage + ']'")
    public Page<Blog> searchSelect(Integer type, Integer currentPage, Integer size, SearchDto searchDto) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getIsDraft,0);
        switch (type){
            case 0:
                log.info("查询所有");
                break;
            case 1:
                log.info("查询前端");
                wrapper.in(Blog::getCId,12,13,14,15);
                break;
            case 2:
                log.info("查询后端");
                wrapper.in(Blog::getCId,16,17,23);
                break;
            case 3:
                log.info("查询日常");
                wrapper.in(Blog::getCId, -1); // 暂且不存在
                break;
            case 4:
                log.info("查询游戏");
                wrapper.in(Blog::getCId,-1); // 暂且不存在
                break;
            case 5:
                log.info("查询其它");
                wrapper.in(Blog::getCId,18);
                break;
            default:
                log.info("默认====>查询所有");
        }
        if(!ObjectUtils.isEmpty(searchDto.getKeyword())){
            // 模糊查询
            wrapper.and(c -> c.like(Blog::getTitle, searchDto.getKeyword())
                    .or()
                    .like(Blog::getDescription, searchDto.getKeyword()));
        }
        switch(searchDto.getType()){
            case 1:
                log.info("发布时间");
                wrapper.orderByDesc(Blog::getCreateTime);
                break;
            case 2:
                log.info("浏览量");
                wrapper.orderByDesc(Blog::getViews);
                break;
            default:
                wrapper.orderByDesc(Blog::getCreateTime);
                log.info("默认===>发布时间查询");
        }

        return blogMapper.selectPage(new Page<>(currentPage, size), wrapper);
    }
}
