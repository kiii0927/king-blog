package com.king.sys.service.auth.impl;

import com.king.sys.bean.properties.OAuthProperties;
import com.king.sys.service.auth.IRestAuthService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *    qq 登录 服务实现类
 * </p>
 *
 * @author: k
 * @create: 2023-01-02 14:10
 **/
@Service
public class QQAuthImpl implements IRestAuthService {


    /**
     * 判断是否为当前登录类型进行登录的
     * @param oauthType 登录类型
     * @return ture/false
     */
    @Override
    public boolean isCurrentAuthLogin(String oauthType) {
        return Objects.equals(oauthType, "qq");
    }

    /**
     * qq登陆
     * @return AuthRequest
     * @param properties 第三方登录的配置
     */
    @Override
    public AuthRequest authLogin(OAuthProperties properties) {
        // 创建Request
        AuthConfig qq = properties.getQq();
        return new AuthQqRequest(qq);
    }
}
