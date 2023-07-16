package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.king.common.utils.EmojiUtil;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 *    评论表
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * admin 评论
     */
    private Boolean adminComment;

    /**
     * 用户标识
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 回复用户名
     */
    private String replyUsername;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long blogId;

    /**
     * Comment ID
     */
    private Integer commentId;

    /**
     * 父级评论ID
     */
    private Integer parentCommentId;

    /**
     * 逻辑删除
     */
    @JsonIgnore
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 有参构造
     */
    public Comment(Comment comment) {
        if (Objects.nonNull(comment)) {
            this.id = comment.id;
            this.adminComment = comment.adminComment;
            this.userId = comment.userId;
            this.username = comment.username;
            this.replyUsername = comment.replyUsername;
            this.email = comment.email;
            this.avatar = comment.avatar;
            this.content = EmojiUtil.emojiConverterUnicodeStr(comment.content);
            this.blogId = comment.blogId;
            this.commentId = comment.commentId;
            this.parentCommentId = comment.parentCommentId;
            this.deleted = comment.deleted;
            this.createTime = comment.createTime;
        }
    }
}
