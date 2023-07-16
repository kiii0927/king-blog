package com.king.common.utils;

import com.king.common.exception.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * cookie 工具类
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class CookieUtil {

    private static final Logger log = LoggerFactory.getLogger(CookieUtil.class);

    private CookieUtil(){
        throw new AssertionError("No com.king.common.utils.CookieUtil instances for you!");
    }

    /**
     * 获取指定的cookie值
     *
     * @param request request
     * @param name cookie的name
     * @return value
     */
    public static String getValue(HttpServletRequest request, String name) {
        Objects.requireNonNull(name, "name be null.");
        if (request == null) {
            throw new IllegalArgumentException("request be null");
        }
        AtomicReference<String> s = new AtomicReference<>(null);

        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> {
                    log.error("cookies be null. request jwt undefined.");
                    return new RequestException("请求失败");
                });
        Objects.requireNonNull(cookies, "cookies be null");
        Arrays.stream(cookies).forEach(cookie -> {
            if (name.equals(cookie.getName())) {
                s.set(cookie.getValue());
            }
        });
        return s.get();
    }
}
