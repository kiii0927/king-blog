package com.king.sys.service.auth.impl;

import com.king.sys.bean.properties.OAuthProperties;
import com.king.sys.service.auth.IRestAuthService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *     gitee 登录实现类
 * </p>
 * @author: k
 * @create: 2023-01-02 19:25
 **/
@Service
public class GiteeAuthImpl implements IRestAuthService {
    @Override
    public boolean isCurrentAuthLogin(String oauthType) {
        return Objects.equals(oauthType, "gitee");
    }

    @Override
    public AuthRequest authLogin(OAuthProperties properties) {
        // 创建Request
        AuthConfig gitee = properties.getGitee();
        return new AuthGiteeRequest(gitee);
    }
}
