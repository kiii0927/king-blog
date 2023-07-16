package com.king.sys.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-02
 **/
@Getter
@Setter
@ToString
public class LoginDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @NotBlank(message = "用户名不能空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

}
