package com.king.sys.bean.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import me.zhyd.oauth.enums.AuthUserGender;

import java.util.Arrays;

/**
 * 用户性别
 *
 * @author king
 * @since 2023-06-20
 * @version 1.0
 **/
@Getter
// implements IEnum<Integer>
public enum GenderEnum {

    /**
     * MALE/FAMALE为正常值，通过{@link AuthUserGender#getRealGender(String)}方法获取真实的性别
     * UNKNOWN为容错值，部分平台不会返回用户性别，为了方便统一，使用UNKNOWN标记所有未知或不可测的用户性别信息
     */
    MALE(1, "男"),
    FEMALE(0, "女"),
    UNKNOWN(-1, "未知");

    @EnumValue
    private final Integer value;

    @JsonValue  // 标记响应json值
    private final String desc;


    /**
     * 获取用户的实际性别，常规网站
     *
     * @param originalGender 用户第三方标注的原始性别
     * @return 用户性别
     */
    public static GenderEnum getRealGender(String originalGender) {
        if (null == originalGender || originalGender.equals("UNKNOWN")) {
            return UNKNOWN;
        }
        String[] males = {"m", "男",  "male"};
        if (Arrays.asList(males).contains(originalGender.toLowerCase())) {
            return MALE;
        }
        return FEMALE;
    }


    GenderEnum(final Integer value, final String desc){
        this.value = value;
        this.desc = desc;
    }

    //@Override
    //public Integer getValue() {
    //    return value;
    //}

    @Override
    public String toString() {
        return "GenderEnum{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}
