package com.king.sys.bean.factory.message;

import com.king.sys.bean.dto.message.Message;
import com.king.sys.bean.entity.message.UserMessage;
import com.king.sys.bean.vo.message.UMessageVo;

import java.time.ZoneOffset;
import java.util.Objects;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-14
 **/
public final class UserMessageFactory {

    private UserMessageFactory(){}


    public static UserMessage producerUserMessage(UserMessage userMessage){
        Objects.requireNonNull(userMessage);
        return UserMessage.builder()
                .topic(userMessage.getTopic()).content(userMessage.getContent())
                .formId(userMessage.getFormId()).toId(userMessage.getToId())
                //.source(Long.toString(now.toInstant(ZoneOffset.ofHours(8)).toEpochMilli()))
                .build();
    }

}
