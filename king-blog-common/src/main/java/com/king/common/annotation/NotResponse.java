package com.king.common.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *    标注该注解的方法将不做数据转换处理,直接返回.
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotResponse {
}
