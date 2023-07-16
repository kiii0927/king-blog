package com.king.sys.event;

import com.king.sys.bean.entity.message.UserMessage;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 *    用户消息通知事件
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
public class UserMessageProducerEvent extends ApplicationEvent {

    public UserMessageProducerEvent(Object source) {
        super(source);
    }

}
