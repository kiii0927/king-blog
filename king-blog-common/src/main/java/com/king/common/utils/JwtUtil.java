package com.king.common.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTUtil;
import com.king.common.module.constant.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *    jwt工具类
 * </p>
 *
 * @author king
 * @version 1.0
 * @see <a href="https://hutool.cn/docs/#/jwt/JWT%E5%B7%A5%E5%85%B7-JWTUtil">hutool JWT工具-JWTUtil</a>
 * @since 2023-06-21
 **/
public final class JwtUtil {

    private JwtUtil(){
        throw new AssertionError("No com.king.common.utils.JwtUtil instances for you!");
    }

    /**
     * 创建JWT
     * @param target 用户唯一标识
     * @return jwt
     */
    public static String createToken(String target){
        Objects.requireNonNull(target, "target be null");
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("uid", Long.parseLong(target));
                // 失效时间为：当前时间-15天后
                put("expire_time", System.currentTimeMillis() + Constant.DAYS_15TIME);
            }
        };

        return JWTUtil.createToken(map, Constant.SALT.getBytes());
    }

    /**
     * 验证JWT
     * @param target jwt
     * @return true/false
     */
    public static boolean verifyToken(String target){
        return JWTUtil.verify(target, Constant.SALT.getBytes());
    }

    /**
     * 解析JWT
     * @param target JWT
     * @return 用户唯一标识
     */
    public static String parseToken(String target){
        Objects.requireNonNull(target, "target be null");

        final JWT jwt = JWTUtil.parseToken(target);

        jwt.getHeader(JWTHeader.TYPE);
        // 获取载荷信息
        return jwt.getPayload("uid").toString();
    }
}
