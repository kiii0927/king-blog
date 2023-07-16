package com.king.sys.bean.dto;

import com.king.common.validator.SaveGroup;
import com.king.common.validator.UpdateGroup;
import com.king.common.validator.ValidatorGroup;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * comment dto<br/>
 *  - ValidatorGroup.class: 回复功能<br/>
 *  - SaveGroup.class: 评论功能
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @NotNull(message = "ID不能为空", groups = {UpdateGroup.class})
    private Integer id;

    @NotBlank(message = "文章ID不能为空")
    private String blogId;

    /**
     * admin 评论  0: not 1: has
     */
    @NotNull(message = "parameter cannot be null")
    private Integer adminComment;

    /**
     * 发送者标识(生产者)
     */
    @NotBlank(message = "用户ID不能为空")
    //private String userId;
    private String formId;

    /**
     * 接收者标识(消费者)
     */
    @NotBlank(message = "接收者ID不能空", groups = {ValidatorGroup.class})
    //private String receiverId;
    private String toId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 回复者的 username
     */
    @NotBlank(message = "回复人姓名不能为空", groups = {ValidatorGroup.class})
    private String replyUsername;

    /**
     * 原文内容
     */
    @NotBlank(message = "原文内容不能为空", groups = {ValidatorGroup.class})
    private String originalContent;

    @Email
    @NotBlank(message = "用户邮箱不能为空", groups = {ValidatorGroup.class, SaveGroup.class})
    private String email;

    @NotBlank(message = "用户头像不能为空", groups = {ValidatorGroup.class, SaveGroup.class})
    private String avatar;

    @NotBlank(message = "评论内容不能为空", groups = {ValidatorGroup.class, SaveGroup.class})
    private String content;

    //@NotNull(message = "评论模块ID不能为空", groups = {UpdateGroup.class, SaveGroup.class})
    @NotNull(message = "parameter cannot be null", groups = {ValidatorGroup.class})
    private Integer commentId;

    //@NotNull(message = "父级评论模块ID不能为空", groups = {UpdateGroup.class})
    @NotNull(message = "parameter cannot be null", groups = {ValidatorGroup.class})
    private Integer parentCommentId;

}
