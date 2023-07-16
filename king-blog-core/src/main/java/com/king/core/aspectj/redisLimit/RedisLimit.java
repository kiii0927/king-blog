package com.king.core.aspectj.redisLimit;

import java.lang.annotation.*;

/**
 * rides限流
 * @author king
 * @since 2023-03-12
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RedisLimit {
    /**
     * 资源的key,唯一
     * 作用：不同的接口，不同的流量控制
     */
    String key() default "";

    /**
     * 最多的访问限制次数
     */
    long permitsPerSecond() default 10;

    /**
     * 过期时间也可以理解为单位时间，单位秒，默认60
     */
    long expire() default 60;


    /**
     * 得不到令牌的提示语
     */
    String msg() default "系统繁忙,请稍后再试.";

}
