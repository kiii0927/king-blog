package com.king.sys.bean.dto;

import com.king.common.validator.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * userInfo dto
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {

    @NotBlank(message = "uid不能为空", groups = {UpdateGroup.class})
    private String id;

    private String token;

    //@NotBlank(message = "用户名不能为空")
    private String username;


    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "年龄不能为空")
    private Integer age;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "用户手机号不合法")
    private String phone;

    private String home;

    private String occupation;

    private String remark;

    private String location;

    private String tencent_qq;

    private boolean authLogin;
}
