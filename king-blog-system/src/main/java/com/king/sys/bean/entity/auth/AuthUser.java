package com.king.sys.bean.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import com.king.sys.bean.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.zhyd.oauth.enums.AuthUserGender;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *    第三方授权表
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_auth_user")
public class AuthUser implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "auth_id", type = IdType.AUTO)
    private Integer id;

    /**
     * 第三方平台用户唯一ID
     */
    private String uuid;

    /**
     * 登录账号
     */
    @TableField(value = "user_name")
    private String username;

    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    private String nickname;

    /**
     * token,用户唯一标识
     */
    @TableField(fill = FieldFill.INSERT)
    private String token;

    /**
     * 登录状态 0:false 1:true
     */
    private Integer status;

    /**
     * 性别
     *  IEnum接口的枚举处理
     * {@link AuthUserGender}
     */
    private GenderEnum gender;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户来源
     */
    private String source;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 登录时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime loginTime;
}
