package com.king.sys.service.system;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.TransactionConsumer;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.vo.BlogVo;
import io.vavr.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *    博客表 服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-04
 **/
public interface IBlogService extends IService<Blog> {

    /**
     * 查询文章分页数据
     * @param currentPage 当前页
     * @param size 页面大小
     * @return {@link Page <Blog>}
     */
    Page<Blog> selectPages(Integer currentPage, Integer size);

    /**
     * 查询文章并分页(缓存版)
     * @param currentPage 当前页
     * @param size 页面大小
     * @return {@link Map <String, Object>}
     */
    Map<String, Object> selectListCache(Integer currentPage, Integer size);

    /**
     * 查询文章并分页
     * @deprecated 推荐使用 {@link #selectListCache(Integer, Integer)}
     * @param currentPage 当前页
     * @param size 页面大小
     * @return {@link List<BlogVo>}
     */
    default List<BlogVo> selectList(Integer currentPage, Integer size){
        return new ArrayList<BlogVo>(Constant.DEFAULT_PAGE_SIZE);
    }

    /**
     * 根据文章id获取文章详情(缓存版)
     * @param id 文章id
     * @return {@link BlogVo}
     */
    BlogVo selectBlogDetailByIdCache(String id);

    /**
     * 根据文章id获取文章详情
     * @deprecated 推荐使用 {@link #selectBlogDetailByIdCache(String)}
     * @param id 文章 id
     * @return {@link BlogVo}
     */
    default BlogVo selectBlogDetailById(String id){
        return new BlogVo();
    }

    /**
     * 获取访问量最高的前三文章（推荐文章）
     * @return {@link List<Blog>}
     */
    List<Blog> selectRecommendPost();

    /**
     * 获取最新文章
     * @return {@link Page<Blog>}
     */
    List<Blog> selectRecentNews();

    /**
     * 更新文章浏览量
     * @param id
     */
    default void updateViews(long id){
        // 创建wrapper
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getId, id)
                .select(Blog::getId, Blog::getViews);// 构建查询条件

        // update
        Blog blog = getOne(wrapper);
        blog.setViews(blog.getViews() + 1);

        // transaction
        TransactionConsumer transactionConsumer = () -> update(blog, wrapper);
        transactionConsumer.doInTransaction();
    }

    /**
     * 更新文章浏览量
     * @param id 文章id
     * @param views 未更新前浏览量
     */
    default void updateViews(long id, int views){
        // builder blog entity
        Blog blog = Blog.builder()
                .id(id).views(views)
                .build();
        // transaction
        TransactionConsumer transactionConsumer = () -> updateById(blog);
        transactionConsumer.doInTransaction();
    }

    /**
     * 查询 blog 数量\访问\评论  数据
     * @return map
     */
    Map<String,Integer> selectBlogData();


    /**
     * 获取 blog information
     *  - 数量\访问\评论  数据
     *  - 获取访问量最高的前三文章（推荐文章）
     *  - 获取最新文章
     * @return
     */
    Tuple3<List<Blog>, List<Blog>, Map<String, Integer>> getInformation();

    /**
     * 查询所有并分页
     * @param page {@link Page}
     * @param wrapper {@link Wrapper}
     * @return list
     */
    List<Blog> list(Page<Blog> page, Wrapper<Blog> wrapper);

    /**
     * 获取时间轴数据
     * @param currentPage 当前页
     * @param size 页面大小
     * @return map
     */
    Map<String, Object> getTimelineMap(Integer currentPage, Integer size);

    /**
     * 查询所有并分页, 按时间分组.
     * <br/>先按年分组, 然后按月分组
     * @param currentPage 当前页
     * @param size 页面大小
     * @return list
     */
    Map<String, Object> selectAllGroupingByDate(Integer currentPage, Integer size);

}
