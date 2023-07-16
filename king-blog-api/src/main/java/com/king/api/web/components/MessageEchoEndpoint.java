package com.king.api.web.components;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.king.sys.assets.SessionManager;
import com.king.sys.service.message.IWebSocketService;
import com.king.sys.service.message.impl.WebSocketServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * <p>
 *    message websocket server point
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-11
 **/
@Slf4j
@Component
@ServerEndpoint("/v1/echo/{uid}")
public class MessageEchoEndpoint {

    private final IWebSocketService webSocketService = SpringUtil.getBean(WebSocketServiceImpl.class);

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("uid") String uid) {
        SessionManager.add(uid, session);
        log.info("用户{}建立ws连接", uid);
    }

    /**
     * 监听 client side 发送的 message<br/>
     *  如果请求参数`uid`为 all，则是发送给所有用户，如果是用户id则是发给单独的用户
     * @param uid 用户id
     * @param message 消息
    //* @return 返回该消息，Client side 可以接收到该方法的返回回调
     */
    @OnMessage
    public void onMessage(@PathParam("uid") String uid, Session session,
                          String message) {
        log.info("`{}`用户发送一条消息：{}", uid, message);
        if (ObjectUtil.isEmpty(uid) || "all".equals(uid)){
            // send all user
            webSocketService.broadcastMessage(message);
        } else {
            // send individual user
            webSocketService.sendMessage(message);
        }
    }

    /**
     * error
     */
    @OnError
    public void onError(Session session, @PathParam("uid") String uid, Throwable error) {
        log.error("error: {}",error.getMessage());
        SessionManager.removeAndClose(uid);
        error.printStackTrace();
    }

    /**
     * 关闭连接
     */
    @OnClose
    // Session session, CloseReason reason
    public void onClose(@PathParam("uid") String uid) {
        log.info("用户{}关闭ws连接", uid);
        SessionManager.removeAndClose(uid);
    }
}
