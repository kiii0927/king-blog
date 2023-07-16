package com.king.sys.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 *    用户注册dto
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @NotBlank(message = "用户名不能为空")
    //@Length(min = 2, max = 10, groups = {Save.class, Update.class})
    private String username;

    @NotBlank(message = "用户密码不能为空")
    private String password;

    @NotBlank(message = "注册邮箱不能为空")
    @Email
    private String email;

}
