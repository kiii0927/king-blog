package com.king.sys.service.message.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ScrollResult;
import com.king.common.module.enums.TopicEnum;
import com.king.common.validator.SystemGroup;
import com.king.sys.assets.FailureAction;
import com.king.sys.assets.MessageTypeEnum;
import com.king.sys.bean.dto.message.MessageConsumerDto;
import com.king.sys.bean.dto.message.USMessageProducerDto;
import com.king.sys.bean.dto.message.Message;
import com.king.sys.bean.entity.message.SysMessage;
import com.king.sys.event.UserSysMessageEvent;
import com.king.sys.handle.consumer.SysMessageHandlerConsumer;
import com.king.sys.mapper.SysMessageMapper;
import com.king.sys.service.message.IMessageService;
import com.king.sys.service.message.ISysMessageService;
import io.vavr.Tuple;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author king
 * @description 针对表【sys_message】的数据库操作Service实现
 * @createDate 2023-07-09 09:54:15
 */
@Slf4j
@Service
@DS("slave")
@Validated(SystemGroup.class)
@RequiredArgsConstructor
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage>
    implements ISysMessageService, IMessageService {

    //@Autowired
    private final SysMessageMapper sysMessageMapper;

    //@Autowired
    private final SysMessageHandlerConsumer messageConsumer;

    //@Autowired
    private final ApplicationEventPublisher publisher;


    @Override
    public boolean isMessageType(String type) {
        return Objects.equals(type.toUpperCase(), MessageTypeEnum.SYSTEM.getType());
    }

    @Override
    public ScrollResult queryMessage(MessageConsumerDto consumerDto) {
        return this.selectMessage(consumerDto.getIdentify(), consumerDto.getLastId(), consumerDto.getOffset());
    }

    @Override
    @Nullable
    public ScrollResult selectMessage(String identify, Long lastId, Integer offset) {
        // 先从 redis 中查询, 没有再去 mysql 中查询
        ScrollResult scrollResult = this.messageConsumer.getMessageReverseSort(identify, lastId, offset);
        if (scrollResult == null) { // redis中不存在, 获取key
            //ScrollResult scrollResult;
            List<String> keyList = this.messageConsumer.getSysMessageKey(identify, lastId, offset);
            if (ObjectUtil.isNotEmpty(keyList)) {
                // 拿到key,获取message
                scrollResult = this.handleHasKey(identify, keyList);
            }else { // 没有拿到 key, redis中也没有
                scrollResult = this.handleNoneKey(identify, lastId, offset);
            }
        }

        if (scrollResult == null || scrollResult.getList() == null){
            scrollResult = ScrollResult.builder().offset(offset)
                    .minTime(lastId).build();
            //scrollResult.setOffset(offset);
            //scrollResult.setMinTime(lastId);
        } else {
            scrollResult.setOffset((int) (offset + Constant.SOURCE_COUNT));
        }

        return scrollResult;
    }

    @Override
    public void producerMessage(@Valid Message message) {
        this.producerOneMessage(message.getStartTime(), message.getEndTime(),
                message.getContent());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateIsRead(String uid, List<String> keyList){
        this.messageConsumer.updateIsRead(uid, keyList);
    }

    @Override
    //@DSTransactional
    @Transactional(rollbackFor = Exception.class)
    public void producerOneMessage(Date startTime, Date endTime, String content) {
        String key = IdUtil.simpleUUID();
        LocalDateTime now = LocalDateTime.now();
        SysMessage sysMessage = SysMessage.builder()
                .key(key)
                .topic(TopicEnum.SYSTEM.getTopic()).content(content)
                .createTime(now)
                .msgType(0).startTime(startTime)
                .endTime(endTime)
                .build();
        int insert = this.sysMessageMapper.insert(sysMessage);
        if (insert > 0) {
            Try.of(() -> {
                publisher.publishEvent(new UserSysMessageEvent(USMessageProducerDto.builder()
                        .key(key).createTime(DateUtil.date(now))
                        .content(content)
                        .build()));
                return "ok";
            }).onFailure(FailureAction::accept);
        }
    }

    @Override
    public SysMessage selectMessage(String key) {
        // 创建 wrapper
        LambdaQueryWrapper<SysMessage> wrapper = Wrappers.lambdaQuery(SysMessage.class)
                .eq(SysMessage::getKey, key);

        // select
        SysMessage sysMessage = this.getOne(wrapper);

        // return
        return Optional.ofNullable(sysMessage)
                .orElseThrow(ServiceException::new);
    }

    @Override
    public LocalDateTime getMessageCreateTime(String uid) {
        // select
        LocalDateTime createTime = this.sysMessageMapper.findCreate_time(uid);

        // return
        return Optional.ofNullable(createTime)
                .orElse(null);
    }

    @DSTransactional
    public ScrollResult handleNoneKey(String identify, long lastId, Integer offset){
        // 查询 `identify` user_user_message 在表中最后一条数据的创建时间
        LocalDateTime createTime = this.getMessageCreateTime(identify);

        // 创建 wrapper
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>(SysMessage.class);
        if (createTime != null){
            //wrapper.gt(SysMessage::getCreate_time, createTime); // 大于
            wrapper.lt(SysMessage::getCreateTime, createTime); // 小于
        }

        wrapper.ge(SysMessage::getEndTime, DateUtil.now()) // 查询没有失效的 大于等于
                .orderByDesc(SysMessage::getCreateTime)
                .last("limit " + Constant.SOURCE_COUNT);

        // query db
        List<SysMessage> messages = super.list(wrapper);

        if (messages.size() <= 0){
            log.info("`handleNoneKey`,没有查询到数据");
            return new ScrollResult();
        }

        // 发布事件 user_sys 表中新增message
        Try.of(() -> {
            this.publisher.publishEvent(new UserSysMessageEvent(Tuple.of(messages, identify)));
            return "ok";
        }).onFailure(FailureAction::accept);

        // setter
        return ScrollResult.builder()
                .list(messages)
                //.minTime(messages.get(0).getCreate_time().toInstant(ZoneOffset.of("Z")).toEpochMilli())
                .minTime(messages.get(messages.size() - 1).getCreateTime().toInstant(ZoneOffset.of("+0")).toEpochMilli())
                .build();
    }

    public ScrollResult handleHasKey(String identify, List<String> keyList){
        // 创建 wrapper
        LambdaQueryWrapper<SysMessage> wrapper = Wrappers.lambdaQuery(SysMessage.class)
                .orderByDesc(SysMessage::getCreateTime)
                .in(SysMessage::getKey, keyList);

        // 合并后的数组
        List<SysMessage> sysMessageList = new ArrayList<>((int) Constant.SOURCE_COUNT);

        List<SysMessage> sysMessageList1 = super.list(wrapper);

        if (sysMessageList1.size() <= 0) {
            log.info("`handleHasKey`,没有查询到数据");
            return new ScrollResult();
        }

        if (sysMessageList1.size() < Constant.SOURCE_COUNT){
            // 查询 `identify` user_user_message 在表中最后一条数据的创建时间
            LocalDateTime createTime = this.getMessageCreateTime(identify);

            // select; 创建 wrapper
            List<SysMessage> sysMessageList2 = super.list(Wrappers.lambdaQuery(SysMessage.class)
                    .gt(SysMessage::getCreateTime, createTime) // 大于
                    .ge(SysMessage::getEndTime, DateUtil.now()) // 查询没有失效的
                    .orderByDesc(SysMessage::getCreateTime)
                    .last("limit " + (int) (Constant.SOURCE_COUNT - sysMessageList1.size())));

            if (sysMessageList2.size() > 0){
                // 合并数组
                sysMessageList = Stream.concat(sysMessageList1.stream(), sysMessageList2.stream())
                        .collect(Collectors.toList());

                // batch insert
                Try.of(() -> {
                    this.publisher.publishEvent(new UserSysMessageEvent(Tuple.of(sysMessageList2, identify)));
                    return "ok";
                }).onFailure(FailureAction::accept);

                this.publishEvent(sysMessageList2, identify);
            }
        } else {
            sysMessageList.addAll(sysMessageList1);
        }

        // update
        this.updateIsRead(identify, keyList);

        // setter
        return ScrollResult.builder()
                //.minTime(LocalDateTimeUtil.toEpochMilli(sysMessageList.get(0)
                //        .getCreate_time()))
                .minTime(sysMessageList.get(0).getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli())
                //.offset(0)
                .list(sysMessageList)
                .build();
    }

    /**
     * 发布事件
     * @param messages message list
     * @param identify 用户身份
     */
    //@DSTransactional
    @Transactional(rollbackFor = Exception.class)
    public void publishEvent(List<SysMessage> messages, String identify){
        Try.of(() -> {
            this.publisher.publishEvent(new UserSysMessageEvent(Tuple.of(messages, identify)));
            return "ok";
        }).onFailure(FailureAction::accept);
    }

}




