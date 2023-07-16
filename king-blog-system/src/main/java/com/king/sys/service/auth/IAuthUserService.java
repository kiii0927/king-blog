package com.king.sys.service.auth;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.entity.auth.AuthUser;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthResponse;
import org.springframework.lang.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * <p>
 *     第三方授权表 服务类
 * </p>
 *
 * @author k
 * @version 1.0
 * @since 2023-06-20
 **/
public interface IAuthUserService extends IService<AuthUser> {


    JSONObject login(AuthResponse<Object> login) throws UnsupportedEncodingException;

    /**
     * 添加用户
     *  1. 校验第三方登录是否成功
     *  2. 拿到第三方授权登录平台的用户数据
     *  3. 校验用户是否已经存在, 存在则直接登录, 否则注册该用户
     * @param login JustAuth统一授权响应类
     * @author k
     */
    AuthUser addUser(AuthResponse<Object> login);

    /**
     * 根据token获取用户
     * @param token
     * @return authUser
     */
    @Nullable
    AuthUser getUser(String token);

    /**
     * 通过 user_id 判断用户是否存在
     * @param identify  用户唯一标识
     * @return true or false
     */
    default boolean isUserExists(String identify) {
        AuthUser authUser = this.getOne(new LambdaQueryWrapper<AuthUser>().eq(AuthUser::getToken, identify));
        // 查询到该记录则返回 true, 否则返回 false
        return Objects.nonNull(authUser);
    }

    /**
     * 通过uuid + source判断用户是否存在
     * @param uuid 用户第三方系统的唯一id
     * @param source 用户来源
     * @return true or false
     */
    boolean isAuthUserExists(String uuid, String source);

    /**
     * 通过uuid + source获取用户
     * @param uuid 用户第三方系统的唯一id
     * @param source 用户来源
     * @return {@link AuthUser}
     */
    AuthUser getAuthUser(String uuid, String source);

    /**
     * 获取授权用户性别
     * @param gender {@link AuthUserGender}
     * @return {@link String}
     */
    default int getAuthUserGender(String gender) {
        int sex = -1;
        switch (gender.toUpperCase()){
            case "MALE":
                sex = Integer.parseInt(AuthUserGender.MALE.getCode());
                break;
            case "FEMALE":
                sex = Integer.parseInt(AuthUserGender.FEMALE.getCode());
                break;
            case "UNKNOWN":
                sex = Integer.parseInt(AuthUserGender.UNKNOWN.getCode());
                break;
        }
        return sex;
    }

    /**
     * 获取响应结果
     * @param authUser authUser entity
     * @return {@link JSONObject}
     * @throws UnsupportedEncodingException
     */
    JSONObject getResponseResult(AuthUser authUser) throws UnsupportedEncodingException;

    void remove(String uuid, String source);
}
