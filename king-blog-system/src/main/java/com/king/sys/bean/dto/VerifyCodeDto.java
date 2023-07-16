package com.king.sys.bean.dto;

import com.king.common.validator.UpdateGroup;
import com.king.common.validator.ValidatorGroup;
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
public class VerifyCodeDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @NotBlank(message = "邮箱不能为空", groups = {UpdateGroup.class, ValidatorGroup.class})
    private String email;

    @NotBlank(message = "验证不能为空", groups = ValidatorGroup.class)
    private String code;

    @NotBlank(message = "用户密码不能为空", groups = UpdateGroup.class)
    private String password;
}
