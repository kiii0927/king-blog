package com.king.sys.service.message.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.module.domain.ScrollResult;
import com.king.common.module.enums.TopicEnum;
import com.king.common.utils.EmojiUtil;
import com.king.common.validator.UserGroup;
import com.king.sys.assets.FailureAction;
import com.king.sys.assets.MessageTypeEnum;
import com.king.sys.bean.dto.message.MessageConsumerDto;
import com.king.sys.bean.dto.message.Message;
import com.king.sys.bean.entity.message.UserMessage;
import com.king.sys.bean.vo.message.UMessageVo;
import com.king.sys.event.UserMessageProducerEvent;
import com.king.sys.handle.consumer.UserMessageHandlerConsumer;
import com.king.sys.mapper.UserMessageMapper;
import com.king.sys.service.message.IMessageService;
import com.king.sys.service.message.IUserMessageService;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author k
 * @description 针对表【user_message】的数据库操作Service实现
 * @createDate 2023-04-03 09:54:15
*/
@Slf4j
@Service
@DS("slave")
@Validated(UserGroup.class)
@RequiredArgsConstructor
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage>
    implements IUserMessageService, IMessageService {

    //@Autowired
    private final ApplicationEventPublisher publisher;

    //@Autowired
    private final UserMessageMapper usersMessageMapper;

    //@Autowired
    private final UserMessageHandlerConsumer messageConsumer;

    @Override
    public ScrollResult queryMessage(MessageConsumerDto consumerDto) {
        return this.selectMessage(consumerDto.getIdentify(), consumerDto.getTopic(),
                consumerDto.getLastId() <= 0 ? System.currentTimeMillis() : consumerDto.getLastId(), consumerDto.getOffset());
    }

    @Nullable
    @Override
    public ScrollResult selectMessage(String identify, String topic, long lastId, Integer offset) {
        // 先从 redis 中查询, 没有再去 mysql 中查询
        ScrollResult scrollResult = this.messageConsumer.getMessageReverseSort(identify + topic, lastId, offset);
        if (scrollResult == null) {
            // db query
            LambdaQueryWrapper<UserMessage> wrapper = Wrappers.lambdaQuery(UserMessage.class)
                    .eq(UserMessage::getToId, identify);
            switch (topic) {
                case "reply":
                    wrapper.eq(UserMessage::getTopic, TopicEnum.REPLY.getTopic());
                    break;
                case "love":
                    wrapper.eq(UserMessage::getTopic, TopicEnum.LOVE.getTopic());
                    break;
                case "attention":
                    wrapper.eq(UserMessage::getTopic, TopicEnum.ATTENTION.getTopic());
                    break;
                default:
                    log.error("wrong topic");
                    throw new ServiceException();
            }

            wrapper.lt(UserMessage::getSource, lastId) // 小于
                    .orderByDesc(UserMessage::getCreateTime);

            // query
            List<UserMessage> usersMessageList = this.usersMessageMapper.selectMyList(wrapper);

            if (usersMessageList.isEmpty()){
                log.info("`queryUserMessage`,没有查询到数据");
                return new ScrollResult();
            }

            // execute async. save to redis.
            CompletableFuture.runAsync(() -> this.producerMessageToRedis(usersMessageList));

            // convert list
            List<UMessageVo> collect = usersMessageList.stream()
                    .map(item -> {
                        UMessageVo uMessageVo = JSON.parseObject(item.getContent(), UMessageVo.class);
                        uMessageVo.setOriginalContent(EmojiUtil.emojiConverterUnicodeStr(uMessageVo.getOriginalContent()));
                        uMessageVo.setReplyContent(EmojiUtil.emojiConverterUnicodeStr(uMessageVo.getReplyContent()));
                        uMessageVo.setId(item.getId())
                                .setKey(item.getIdentify())
                                .setSource(item.getSource());
                        return uMessageVo;
                    })
                    .collect(Collectors.toList());

            // setter
            scrollResult = ScrollResult.builder()
                    .list(collect)
                    .minTime(Long.parseLong(usersMessageList.get(0).getSource()))
                    .build();
        }

        return scrollResult;
    }

    @Override
    //@DSTransactional
    @Transactional(rollbackFor = Exception.class)
    public void producerOneMessage(UserMessage message) {
        int insert = this.usersMessageMapper.insert(message);
        if (insert > 0) {
            // 发布事件
            Try.of(() -> {
                publisher.publishEvent(new UserMessageProducerEvent(message));
                return "ok";
            }).onFailure(FailureAction::accept);
        }
    }

    @Override
    public void producerMessageToRedis(List<UserMessage> list) {
        // 发布事件
        Try.of(() -> {
            publisher.publishEvent(new UserMessageProducerEvent(list));
            return "ok";
        }).onFailure(FailureAction::accept);
    }


    @Override
    public boolean isMessageType(String type) {
        return Objects.equals(type.toUpperCase(), MessageTypeEnum.USER.getType());
    }

    @Override
    public void producerMessage(@Valid Message message) {
        String content = "";
        LocalDateTime now = LocalDateTime.now();
        switch (message.getTopic()) {
            case "reply":
                log.info("reply topic handle...");
                content = this.handleReply(message, now);
                break;
            case "love":
                log.info("love topic handle...");
                content = this.handleLove(message, now);
                break;
            case "attention":
                log.info("attention topic handle...");
                content = this.handleAttention(message);
                break;
            default:
                log.error("wrong topic");
                throw new ServiceException();
        }

        UserMessage usersMessage = UserMessage.builder()
                .topic(message.getTopic()).content(content)
                .formId(message.getFormId()).toId(message.getToId())
                .source(Long.toString(now.toInstant(ZoneOffset.ofHours(8)).toEpochMilli()))
                .build();

        this.producerOneMessage(usersMessage);
    }

    private String handleReply(Message message, LocalDateTime dateTime){
        UMessageVo uMessageVo = UMessageVo.builder()
                .blogId(message.getBlogId()).username(message.getUsername())
                .replyUsername(message.getReplyUsername()).avatar(message.getAvatar())
                .replyContent(EmojiUtil.emojiConverterToAlias(message.getContent()))
                .originalContent(EmojiUtil.emojiConverterToAlias(message.getOriginalContent()))
                .createTime(DateUtil.date(dateTime))
                .build();

        return JSON.toJSONString(uMessageVo, SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteDateUseDateFormat);
    }

    private String handleLove(Message message, LocalDateTime dateTime) {
        UMessageVo uMessageVo = UMessageVo.builder()
                .blogId(message.getBlogId()).username(message.getUsername())
                .avatar(message.getAvatar())
                .originalContent(EmojiUtil.emojiConverterToAlias(message.getOriginalContent()))
                .createTime(DateUtil.date(dateTime))
                .build();

        return JSON.toJSONString(uMessageVo, SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteDateUseDateFormat);
    }

    private String handleAttention(Message message) {
        // developing...
        return null;
    }

}




