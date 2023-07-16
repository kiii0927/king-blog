package com.king.sys.bean.factory;

import com.king.common.utils.EmojiUtil;
import com.king.sys.bean.dto.CommentDto;
import com.king.sys.bean.entity.system.Comment;

import java.util.Objects;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public class CommentFactory {

    private CommentFactory(){}

    public static Comment getComment(CommentDto commentDto){
        return getComment(commentDto, true);
    }

    /**
     * 创建 Comment
     * @param commentDto {@link CommentDto}
     * @param flag true/false 评论/回复
     * @return {@link Comment}
     */
    public static Comment getComment(CommentDto commentDto, boolean flag){
        Objects.requireNonNull(commentDto,"commentDto 参数是必须的，不能为空");

        Comment comment = Comment.builder()
                .userId(commentDto.getFormId()).username(commentDto.getUsername())
                .email(commentDto.getEmail()).avatar(commentDto.getAvatar())
                .content(EmojiUtil.emojiConverterToAlias(commentDto.getContent()))
                .blogId(Long.parseLong(commentDto.getBlogId()))
                .parentCommentId(commentDto.getParentCommentId())
                .build();
        // 回复
        if (!flag){
            comment.setCommentId(commentDto.getCommentId());
            comment.setReplyUsername(commentDto.getReplyUsername());
        }
        return comment;
    }

}
