package com.king.sys.service.message;

import com.king.common.module.domain.ScrollResult;
import com.king.sys.bean.dto.message.MessageConsumerDto;
import com.king.sys.bean.dto.message.Message;

import javax.validation.Valid;

/**
 * <p>
 *    message 服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
public interface IMessageService {

    /**
     * message 类型[用户消息,系统消息]
     * @param type 类型
     * @return ture/false
     */
    boolean isMessageType(String type);

    /**
     * producer message
     * @param message message
     */
    void producerMessage(@Valid Message message);

    /**
     * consumer message
     * @param consumerDto {@link MessageConsumerDto}
     * @return {@link ScrollResult}
     */
    ScrollResult queryMessage(MessageConsumerDto consumerDto);

}
