package com.king.core.config;

import com.king.common.module.constant.Constant;
import com.king.core.web.interceptor.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

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
     * 所有请求都允许跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("http://localhost:9027", "http://39.101.75.221:9027")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .maxAge(3600);
    }

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
        return new RequestInterceptor();
    }

}
