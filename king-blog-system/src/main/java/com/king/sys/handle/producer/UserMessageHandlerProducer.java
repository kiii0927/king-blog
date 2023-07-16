package com.king.sys.handle.producer;

import com.king.sys.bean.entity.message.UserMessage;
import com.king.sys.event.UserMessageProducerEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *    user message 处理生成者
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-11
 **/
@Slf4j
@Component
public class UserMessageHandlerProducer implements ApplicationListener<UserMessageProducerEvent> {

    private final StringRedisTemplate stringRedisTemplate;

    public UserMessageHandlerProducer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //@EventListener(value = MessageProducerEvent.class)
    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(@NonNull UserMessageProducerEvent event) {
        Object obj = event.getSource();

        if (obj instanceof UserMessage) {
            this.saveToRedis((UserMessage) obj);
        } else if (obj instanceof List){
            this.saveToRedis((List<UserMessage>) obj);
        }
        log.info("[afterUserMessageHandlerProducer] execute success...");
    }

    /**
     * 往 redis 中添加
     * @param message message
     */
    private void saveToRedis(UserMessage message){
        this.saveToRedis(message, Long.parseLong(message.getSource()));
    }

    /**
     * 往 redis 中添加
     * @param message message
     * @param source source
     */
    private void saveToRedis(UserMessage message, long source){
        Boolean save = stringRedisTemplate.opsForZSet()
                .add(message.getToId() + message.getTopic(), message.getContent(), source);
        assert save != null;
        if (save) {
            log.info("saveToRedis success...");
        }else {
            log.error("saveToRedis failed...");
        }
    }

    /**
     * 往 redis 中添加
     * @param list list
     */
    private void saveToRedis(List<UserMessage> list){
        list.forEach(this::saveToRedis);
    }
}
