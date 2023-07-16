package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.vo.BlogVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *    博客表 Mapper 接口
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-04
 **/
public interface BlogMapper extends BaseMapper<Blog> {

    /**
     * 查询文章并分页
     * @param currentPage 当前页
     * @param size 页面大小
     * @return {@link List <BlogVo>}
     */
    List<BlogVo> selectListPage(@Param("currentPage") Integer currentPage, @Param("size") Integer size);

    /**
     * 根据文章id查询文章
     * @param id 文章id
     * @return {@link BlogVo}
     */
    BlogVo selectBlogById(@Param("id") String id);

    /**
     * 查询浏览量
     * @return {@link Integer}
     */
    Integer selectViewsCount();

    /**
     * 查询评论量
     * @return {@link Integer}
     */
    Integer selectCommentsCount();

    /**
     * 查询文章数量
     * @return {@link Integer}
     */
    Integer selectBlogCount();

    /**
     * 查询总文章数、评论数、浏览量
     * @return map
     */
    //@MapKey("id")
    Map<String, Integer> findBlogData();

    /**
     * 修改文章评论数
     * @param blogId 文章id
     */
    default void updateCommentsById(@Param("blogId") long blogId) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();

        wrapper.select(Blog::getId) // Blog::getComments
                .eq(Blog::getId, blogId);

        Blog blog = this.selectOne(wrapper);
        //blog.setComments(blog.getComments() + 1);
        this.updateById(blog);
    }
}
