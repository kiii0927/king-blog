package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.king.sys.bean.entity.system.Comment;
import org.apache.ibatis.annotations.Param;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public interface CommentMapper extends BaseMapper<Comment> {


    Integer selectMax();

    /**
     * 通过评论模块 ID 获取更多评论回复数据
     * @param page 获取到的分页数据
     * @param dateTime 最近的评论时间
     * @param commentId 评论模块
     * @return 查询到的分页数据
     */
    IPage<Comment> findMoreReplyByCommentId(@Param("page") IPage<Comment> page, @Param("dateTime") String dateTime, @Param("commentId") Integer commentId);

}
