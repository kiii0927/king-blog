package com.king.common.module.enums;

import lombok.Getter;

/**
 * topic enum
 *
 * @author king
 * @since 2023-03-20
 * @version 1.0
 **/
@Getter
public enum TopicEnum {

    REPLY("reply", "reply:topic", 0),

    LOVE("love", "love:topic", 1),

    ATTENTION("attention", "attention:topic", 2),

    SYSTEM("system", "system:topic", 3);

    /**
     * topic 类型
     */
    private String topic;

    /**
     * topic 描述
     */
    private String depict;

    /**
     * topic 编码
     */
    private Integer code;

    TopicEnum() {}

    TopicEnum(String type, String v, Integer code) {
        this.topic = type;
        this.depict = v;
        this.code = code;
    }
}
