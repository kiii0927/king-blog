package com.king.sys.bean.properties;

import lombok.Data;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * <p>
 *    第三方登录配置
 * </p>
 * @author: k
 * @create: 2023-01-02 13:48
 **/
@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {
    /**
     * QQ 配置
     */
    private AuthConfig qq;

    /**
     * github 配置
     */
    private AuthConfig github;

    /**
     * gitee 配置
     */
    private AuthConfig gitee;

    /**
     * 微博 配置
     */
    private AuthConfig wb;

    /**
     * 百度 配置
     */
    private AuthConfig baidu;

}
