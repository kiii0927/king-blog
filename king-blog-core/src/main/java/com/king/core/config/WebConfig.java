package com.king.core.config;

import com.king.common.module.constant.Constant;
import com.king.core.web.interceptor.LoginTicketInterceptor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web config
 *
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constant.BASE_UPLOAD_URl + "**")
                .addResourceLocations("file:////" + Constant.AVATAR_UPLOAD_ABSTRACT_PATH);
    }

    /**
     * 添加拦截器
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginTicketInterceptor()) // 添加拦截器
                .excludePathPatterns("/**/*.css", "/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg"); // 过滤哪些请求
    }

    @Bean
    public HandlerInterceptor getLoginTicketInterceptor(){
        return new LoginTicketInterceptor();
    }

}
