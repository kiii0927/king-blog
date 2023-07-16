package com.king.sys.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.king.common.exception.WrongInputException;
import com.king.common.module.domain.MessageResult;
import com.king.sys.assets.FailureAction;
import com.king.sys.assets.SessionManager;
import com.king.sys.bean.dto.message.Message;
import com.king.sys.service.message.IMessageService;
import com.king.sys.service.message.IWebSocketService;
import io.vavr.concurrent.Future;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * <p>
 *    message websocket 服务实现类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Service
public class WebSocketServiceImpl implements IWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServiceImpl.class);

    // 注入message服务类
    @Autowired
    private List<IMessageService> messageServices;

    /**
     * 获取 MessageService 实例
     */
    private IMessageService getMessageServiceInstance(String type){
        return messageServices.stream()
                .filter(item -> item.isMessageType(type))
                .findFirst()
                .orElseThrow(WrongInputException::new);
    }

    @Override
    @SneakyThrows
    public void sendMessage(Session toSession, @NonNull String message) {
        //Objects.requireNonNull(toSession, "The session to be sent is empty");
        if (toSession == null) {
            logger.error("The session to be sent is empty");
        } else {
            toSession.getBasicRemote().sendText(message);
            logger.info("message send successfully");
        }
    }

    @Override
    public void sendMessage(String message) {
        if (!StringUtils.hasLength(message)){
            logger.error("The message to be sent is empty");
        } else {
            // send individual user
            Message parseMessage = JSON.parseObject(message, Message.class);
            String toId = parseMessage.getToId();
            Session toSession = SessionManager.get(toId);

            // async handle
            Future.of(Executors.newFixedThreadPool(1), () -> {
                // service handle
                getMessageServiceInstance("user")
                        .producerMessage(parseMessage);
                return "ok";
            }).onFailure(FailureAction::accept);

            MessageResult messageResult = new MessageResult("您有新的消息，请查收！",
                    MessageResult.buildDots(parseMessage.getTopic()));

            String s = JSON.toJSONString(messageResult);

            // send
            this.sendMessage(toSession, s);
        }
    }

    @Override
    public void broadcastMessage(String message){
        if (StringUtils.hasLength(message)){
            for (Session session : SessionManager.SESSION_POOL.values()) {
                this.sendMessage(session, message);
            }
        }
    }
}
