package com.king.sys.service.auth;

import com.king.sys.bean.properties.OAuthProperties;
import me.zhyd.oauth.request.AuthRequest;

/**
 * <p>
 *     第三方登录服务类
 * </p>
 *
 * @author k
 * @version 1.0
 * @since 2023-06-20
 **/
public interface IRestAuthService {

    /**
     * 判断是否为当前登录类型进行登录的
     * @param oauthType 登录类型
     * @return true/false
     */
    boolean isCurrentAuthLogin(String oauthType);

    /**
     * 认证登录
     * @param properties 第三方登录的配置
     * @return AuthRequest
     */
    AuthRequest authLogin(OAuthProperties properties);

}
