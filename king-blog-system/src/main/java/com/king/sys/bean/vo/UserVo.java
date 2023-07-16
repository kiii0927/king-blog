package com.king.sys.bean.vo;

import com.king.sys.bean.entity.auth.AuthUser;
import com.king.sys.bean.entity.system.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-02
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * token,用户唯一标识
     */
    private String token;

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
     * 用户权限
     */
    private String permission;

    /**
     * 第三方登录
     */
    private boolean isAuthLogin;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 将 AuthUser实体类 转换成 UserVo
     * @param authUser {@link AuthUser}
     */
    public UserVo(AuthUser authUser){
        Objects.requireNonNull(authUser);
        this.id = authUser.getId();
        this.token = authUser.getToken();
        this.avatar = authUser.getAvatar();
        this.email= authUser.getEmail();
        this.username = authUser.getUsername();
        this.permission = "test";
        this.isAuthLogin = true;
        this.createTime = authUser.getCreateTime();
        this.lastLoginTime = authUser.getLoginTime();
    }

    /**
     * 将 User实体类 转换成 UserVo
     */
    public UserVo(User user) {
        Objects.requireNonNull(user);
        this.id = user.getId();
        this.token = user.getToken();
        this.avatar = user.getAvatar();
        this.email= user.getEmail();
        this.username = user.getUsername();
        this.permission = "test";
        this.isAuthLogin = false;
        this.createTime = user.getCreateTime();
        this.lastLoginTime = user.getLoginTime();
    }

}
