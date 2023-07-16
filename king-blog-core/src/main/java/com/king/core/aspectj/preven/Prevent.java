package com.king.core.aspectj.preven;

import java.lang.annotation.*;

/**
 * <p>
 * 接口防刷注解
 * </p>
 *
 * 使用：在需要防刷的方法上加上该注解即刻
 *
 * @author k
 * @since 2023-02-01
 **/
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prevent {

    /**
     * 限制的时间值（秒）
     *
     */
    String value() default "60";

    /**
     * 提示
     */
    String message() default "秒内不允许重复请求";

    /**
     * 策略
     * @return {@link PreventStrategy}
     */
    PreventStrategy strategy() default PreventStrategy.DEFAULT;
}
