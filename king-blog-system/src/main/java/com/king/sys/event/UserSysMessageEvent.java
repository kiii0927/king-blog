package com.king.sys.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
public class UserSysMessageEvent extends ApplicationEvent {

    public UserSysMessageEvent(Object source) {
        super(source);
    }
}
