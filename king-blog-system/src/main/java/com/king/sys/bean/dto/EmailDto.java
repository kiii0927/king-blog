package com.king.sys.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 *    邮箱 dto
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-02
 **/
@Getter
@Setter
@ToString
public class EmailDto implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 邮件内容
     */
    @NotBlank(message = "请输入邮件内容")
    private String content;

    /**
     * 昵称
     */
    @Length(min = 1, max = 8, message = "昵称长度在1-8之间")
    @NotBlank(message = "请输入昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @Email
    @NotBlank(message = "请输入邮箱地址")
    private String email;

    /**
     * 邮件标题
     */
    @NotBlank(message = "请输入邮件标题")
    private String title;
}
