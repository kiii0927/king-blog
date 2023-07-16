package com.king.sys.bean.dto;

import com.king.common.validator.ValidatorGroup;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 *    barrage request<br/>
 *     - SaveGroup comment<br/>
 *     - ValidatorGroup reply
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BarrageRequest implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * admin 评论  0: not 1: has
     */
    private Integer adminComment;

    /**
     * 发送者标识(生产者) userId
     */
    @NotBlank(message = "发送者ID不能为空")
    private String formId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 回复者的 username
     */
    @NotBlank(message = "回复人姓名不能为空", groups = {ValidatorGroup.class})
    private String replyUsername;

    /**
     * 用户邮箱
     */
    @Email(message = "请填写正确的邮箱地址")
    private String email;

    /**
     * 用户头像
     */
    @NotBlank(message = "头像不能为空")
    private String avatar;

    /**
     * 评论消息
     */
    @NotBlank(message = "评论消息不能为空")
    private String message;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 用户网站
     */
    private String site;

    @NotNull(message = "parameter cannot be null", groups = {ValidatorGroup.class})
    private Integer commentId;

    //@NotNull(message = "parameter cannot be null", groups = {ValidatorGroup.class})
    private Integer parentCommentId;

}
