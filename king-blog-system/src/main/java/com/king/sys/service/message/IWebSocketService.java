package com.king.sys.service.message;

import lombok.NonNull;

import javax.websocket.Session;

/**
 * <p>
 *    message websocket 服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
public interface IWebSocketService {

    /**
     * 发送消息
     *
     * @param toSession 消费者[接受者]session
     * @param message 消息
     */
    void sendMessage(Session toSession, @NonNull String message);

    /**
     * 发生消息
     * @param message 消息
     */
    void sendMessage(String message);

    /**
     * 广播消息
     * @param message 消息
     */
    void broadcastMessage(String message);

}
