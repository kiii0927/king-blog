package com.king.sys.assets;

import lombok.Getter;

/**
 * <p>
 *    message type enum
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Getter
public enum MessageTypeEnum {

    SYSTEM("SYSTEM", 0),
    USER("USER", 1);
    //reply

    private String type;

    private Integer number;

    MessageTypeEnum() {}

    MessageTypeEnum(String type, Integer number) {
        this.type = type;
        this.number = number;
    }

}
