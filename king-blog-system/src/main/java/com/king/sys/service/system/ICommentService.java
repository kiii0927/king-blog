package com.king.sys.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.dto.CommentDto;
import com.king.sys.bean.entity.system.Comment;
import com.king.sys.bean.vo.CommentVo;
import io.vavr.Tuple2;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *    评论表服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public interface ICommentService extends IService<Comment> {
    /**
     * 根据Id查询并分页
     * @param bId id
     * @param currentPage 当前页
     * @param size 页面大小
     * @return list
     */
    Tuple2<Page<Comment>, List<CommentVo>> selectPageById(String bId, Integer currentPage, Integer size);

    /**
     * 根据文章id进行分页查询并缓存
     *
     * @param bId 文章id
     * @param currentPage 当前页
     * @param size 页面大小
     * @return map
     */
    Map<String, Object> selectPageByIdCache(String bId, Integer currentPage, Integer size);

    /**
     * 保存评论
     *
     * @param commentDto {@link CommentDto}
     * @param flag true/false 评论/回复
     * @return {@link Comment}
     */
    Comment save(CommentDto commentDto, boolean flag);

    /**
     * 通过评论模块 ID 获取更多评论回复数据
     *
     * @param commentId 评论模块ID
     * @param pageSize 获取数据大小
     * @param dateTime 最近的评论时间
     * @return page
     */
    Page<Comment> selectMoreReplyByCommentId(Integer commentId, Integer pageSize, String dateTime);

    /**
     * 根据id查询并分页
     *
     * @param bId 文章id
     * @param currentPage 当前页
     * @param size 页面大小
     * @return page
     */
    Page<Comment> selectPages(String bId, Integer currentPage, Integer size);
}
