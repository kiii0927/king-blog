package com.king.sys.bean.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 *    区分用户是注册还是登录(忘记密码)
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Getter
public enum LoginSignEnum {

    REGISTER(1, "注册"), // 注册
    FORGET_PASS(2, "忘记密码"); // 忘记密码

    @EnumValue
    private Integer type;

    @JsonValue  // 标记响应json值
    private String des;

    LoginSignEnum(){}

    LoginSignEnum(Integer type, String des){
        this.type = type;
        this.des = des;
    }

    /**
     * 获取用户的实际性别，常规网站
     *
     * @param type 类型
     * @return 登录类型
     */
    public static LoginSignEnum getRealType(Integer type) {
        switch (type){
            case 1:
                return REGISTER;
            case 2:
                return FORGET_PASS;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "LoginSignEnum{" +
                "type=" + type +
                ", des='" + des + '\'' +
                '}';
    }
}
