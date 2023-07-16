package com.king.common.module.enums;

import lombok.Getter;

/**
 * barrage say enum
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Getter
public enum BarrageSayEnum {

    COMMENT("comment", 0),

    REPLY("reply", 1);

    /**
     * say 类型
     */
    String type;

    /**
     * say code
     */
    private Integer code;

    BarrageSayEnum() {}

    BarrageSayEnum(String type, int code) {
        this.type = type;
        this.code = code;
    }
}
