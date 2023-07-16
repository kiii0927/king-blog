package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *    用户表
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * token, 用户唯一标识
     */
    @TableField(fill = FieldFill.INSERT)
    private String token;

    /**
     * 登录状态 0:false 1:true
     */
    private Integer status;

    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Integer deleted;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 原文密码
     */
    private String originalPassword;

    /**
     * 用户权限
     */
    private String permission;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最近登录时间
     */
    @TableField(value = "last_login_time", fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
}
