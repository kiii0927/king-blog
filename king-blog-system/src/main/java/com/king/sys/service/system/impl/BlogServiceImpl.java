package com.king.sys.service.system.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.king.common.exception.ServiceException;
import com.king.common.module.constant.CacheConstant;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ResponseCode;
import com.king.common.utils.EntityUtil;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.vo.BlogVo;
import com.king.sys.bean.vo.BlogVo2;
import com.king.sys.mapper.BlogMapper;
import com.king.sys.service.system.IBlogService;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 *    博客表 服务实现类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-04
 **/
@Slf4j
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    private final BlogMapper blogMapper;

    private final TransactionTemplate transactionTemplate;

    private final StringRedisTemplate stringRedisTemplate;


    public BlogServiceImpl(BlogMapper blogMapper, TransactionTemplate transactionTemplate, StringRedisTemplate stringRedisTemplate){
        this.blogMapper = blogMapper;
        this.transactionTemplate = transactionTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    ///**
    // * 存储时间轴数据
    // */
    //private static final Map<String, Map<String, Object>> timelineMap = new ConcurrentHashMap<>();

    /**
     * caffeine缓存
     */
    public final Cache<String, Object> blogCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    @Override
    public Page<Blog> selectPages(Integer currentPage, Integer size) {
        // 创建 wrapper
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Blog::getId)
                .eq(Blog::getIsDraft, 0);

        // page select
        Page<Blog> blogPage = blogMapper.selectPage(new Page<>(currentPage, size), wrapper);
        Assert.notNull(blogPage, ResponseCode.ERROR.getMessage());

        return blogPage;
    }

    @Override
    @SuppressWarnings("unchecked")
    //@Cacheable(value = CacheConstant.CACHE_30SECS, key = "#currentPage")
    public Map<String, Object> selectListCache(Integer currentPage, Integer size) {
        String cacheKey = "selectListCache" + currentPage;
        return (Map<String, Object>) blogCache.get(cacheKey, key -> {
            Page<Blog> page = this.selectPages(currentPage, size);
            List<BlogVo> blogVo = this.selectList(currentPage, size);
            return MapUtil.builder(new HashMap<String, Object>())
                    .put("total", page.getTotal())
                    .put("pages", page.getPages())
                    .put("records", blogVo)
                    .map();
        });
    }

    @Override
    public List<BlogVo> selectList(Integer currentPage, Integer size) {
        if (currentPage > 0) --currentPage;
        return Optional.ofNullable(blogMapper.selectListPage(currentPage, size))
                .orElseThrow(() -> new ServiceException(ResponseCode.ERROR.getMessage()));
    }

    @Override
    //@Cacheable(value = CacheConstant.CACHE_60SECS, key = "#id")
    public BlogVo selectBlogDetailByIdCache(String id) {

        // query blog detail
        BlogVo blogVo = (BlogVo) blogCache.get(id, this::selectBlogDetailById);
        //BlogVo blogVo = this.selectBlogDetailById(id);

        // update views execute async
        Optional.ofNullable(blogVo)
                .ifPresent(b -> {
                    CompletableFuture.runAsync(() -> transactionTemplate.execute(status -> {
                        b.setViews(b.getViews() + 1);
                        BlogVo blogVo2 = new BlogVo();
                        BeanUtils.copyProperties(b, blogVo2);

                        // save to redis
                        stringRedisTemplate.opsForValue()
                                .set(id, JSON.toJSONString(blogVo2, SerializerFeature.WriteDateUseDateFormat));

                        // update views
                        this.updateViews(blogVo2.getId(), blogVo2.getViews());

                        return Boolean.TRUE;
                    }));
                });
        return blogVo;
    }

    @Override
    public BlogVo selectBlogDetailById(String id) {
        //System.out.println("-------------->>>> selectBlogDetailById.");
        AtomicReference<BlogVo> blogVo = new AtomicReference<>(new BlogVo());

        // 先从redis查, 不存在再从数据库中查
        String strTemp = stringRedisTemplate.opsForValue().get(id);

        blogVo.set(Objects.nonNull(strTemp) ? JSON.parseObject(strTemp, BlogVo.class) : Optional.ofNullable(blogMapper.selectBlogById(id))
                .orElseThrow(() -> new IllegalArgumentException(ResponseCode.ERROR.getMessage())));

        return blogVo.get();
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_60SECS, key = "'blog-data'")
    public Map<String, Integer> selectBlogData() {
        Map<String, Integer> information = blogMapper.findBlogData();
        return Optional.ofNullable(information)
                .orElseThrow(ServiceException::new);
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_60SECS, key = "'information'")
    public Tuple3<List<Blog>, List<Blog>, Map<String, Integer>> getInformation() {
        List<Blog> recommend = this.selectRecommendPost(); // 推荐文章
        List<Blog> recentNews = this.selectRecentNews(); // 最新文章
        Map<String, Integer> blogData = this.selectBlogData(); // 数量\访问\评论  数据

        return Tuple.of(recommend, recentNews, blogData);
    }

    @Override
    public List<Blog> list(Page<Blog> page, Wrapper<Blog> wrapper) {
        return Optional.ofNullable(this.blogMapper.selectPage(page, wrapper).getRecords())
                .orElseThrow(ServiceException::new);
    }

    @Override
    public Map<String, Object> getTimelineMap(Integer currentPage, Integer size) {
        //String cacheKey = "archives" + currentPage;
        //Map<String, Object> v = timelineMap.get(cacheKey);
        //if (ObjectUtil.isNotEmpty(v)) {
        //    return v;
        //}
        //timelineMap.put(cacheKey, map);
        return this.selectAllGroupingByDate(currentPage, size);
    }

    @Override
    public Map<String, Object> selectAllGroupingByDate(Integer currentPage, Integer size) {
        // 创建查询条件(wrapper)
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Blog::getCreateTime, Blog::getDescription, Blog::getTitle, Blog::getFirstPicture, Blog::getId)
                .eq(Blog::getIsDraft, 0)
                .orderByDesc(Blog::getCreateTime);

        // select
        Page<Blog> page = this.blogMapper.selectPage(new Page<>(currentPage, size), wrapper);

        List<BlogVo2> blogVo2List = EntityUtil.toList(page.getRecords(), BlogVo2::new);
        this.setStrCreateTime(blogVo2List);

        // execute
        return MapUtil.builder(new HashMap<String, Object>())
                .put("total", page.getTotal())
                .put("pages", page.getPages())
                .put("size", page.getSize())
                .put("records", blogVo2List)
                .build();
    }

    /**
     * 设置 创建时间  yyyy年MM月dd日
     * @param list list
     */
    protected void setStrCreateTime(@NonNull List<BlogVo2> list) {
        list.forEach(item -> {
            String format = DateUtil.format(item.getCreateTime(), DatePattern.CHINESE_DATE_PATTERN);
            item.setStrCreateTime(format);
        });
    }

    /**
     * 按年分组
     * @param list {@link List}
     * @return list
     */
    protected List<Map<String, Object>> selectAllGroupingByYear(@NonNull List<Blog> list) {
        // 分组操作(按年分组)
        Map<String, List<Blog>> collect = list.stream()
                .collect(Collectors.groupingBy(o -> DateUtil.format(o.getCreateTime(), DatePattern.NORM_YEAR_PATTERN)));

        // 转 list
        return collect.entrySet().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("year", item.getKey());
                    map.put("list", item.getValue());
                    return map;
                }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_30SECS, key = "'recommend-post'")
    public List<Blog> selectRecommendPost() {
        // 创建 wrapper
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getIsDraft, 0)
                .orderByDesc(Blog::getViews)
                .last("limit 1," + Constant.DEFAULT_PAGE_SIZE);
        List<Blog> blogList = blogMapper.selectList(wrapper);
        return Optional.ofNullable(blogList)
                .orElseThrow(() -> new IllegalArgumentException(ResponseCode.ERROR.getMessage()));
    }

    @Override
    @Cacheable(value = CacheConstant.CACHE_30SECS, key = "'recent-news'")
    public List<Blog> selectRecentNews() {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getIsDraft, 0)
                .orderByDesc(Blog::getUpdateTime)
                .last("limit 1,3");
        List<Blog> blogList = blogMapper.selectList(wrapper);
        return Optional.ofNullable(blogList)
                .orElseThrow(() -> new ServiceException(ResponseCode.ERROR.getMessage()));
    }
}
