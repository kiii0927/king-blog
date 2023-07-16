package com.king.sys.handle.producer;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.king.common.module.enums.TopicEnum;
import com.king.sys.bean.dto.message.USMessageProducerDto;
import com.king.sys.bean.entity.message.SysMessage;
import com.king.sys.event.UserSysMessageEvent;
import com.king.sys.service.message.IUserSysMessageService;
import com.king.sys.service.system.IUserService;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 *    UserSysMessage 处理生成者
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-11
 **/
@Slf4j
@Component
public class UserSysMessageHandlerProducer implements ApplicationListener<UserSysMessageEvent> {

    private final StringRedisTemplate stringRedisTemplate;
    private final IUserSysMessageService userSysMessageService;
    private final IUserService userService;

    public UserSysMessageHandlerProducer(StringRedisTemplate stringRedisTemplate,
                                         IUserSysMessageService userSysMessageService,
                                         IUserService userService) {
        this.userSysMessageService = userSysMessageService;
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(UserSysMessageEvent event) {
        Object val = event.getSource();
        if (val instanceof USMessageProducerDto){
            log.info("USMessageProducerDto class");
            this.saveToRedisAndDB((USMessageProducerDto) val);
        }else if (val instanceof Tuple2){
            log.info("Tuple2 class");
            this.saveToRedisAndDB((Tuple2<List<SysMessage>, String>) val, event.getTimestamp());
        }
        log.info("[afterUserSysMessageHandlerProducer] execute success...");
    }

    public void saveToRedisAndDB(@NonNull Tuple2<List<SysMessage>, String> tuple, long source){
        //System.out.println("saveToRedisAndDB....");
        boolean exists = this.userSysMessageService.exists(tuple._1.stream()
                .map(SysMessage::getKey)
                .collect(Collectors.toList()), tuple._2);
        // not exists
        if (!exists){
            // execute async
            CompletableFuture.runAsync(() -> {
                this.saveToRedis(tuple);
                this.saveBatchToDB(tuple);
            });
        }
        //this.saveToRedis(tuple, source);
    }

    public void saveToRedisAndDB(@NonNull USMessageProducerDto producerDto){
        // 拿到最近登录用户的唯一标识
        List<String> list = userService.queryLastLoginUser();
        long source = producerDto.getCreateTime().getTime();
        this.saveToRedis(list, producerDto, source);
        this.saveBatchToDB(producerDto.getKey(), list, source);
    }

    /**
     * 批量添加 DB
     * @param key sys message key
     * @param list 最近登录用户的唯一标识
     */
    public void saveBatchToDB(String key, List<String> list, long source){
        // 给最近登录的用户发送系统消息
        boolean insert = userSysMessageService.insertBatch(key, list, source);
        if (insert) {
            log.info("insertBatch success");
        } else {
            log.warn("insertBatch failed");
        }
    }

    /**
     * save to redis
     * @param list 最近登录用户的唯一标识
     * @param producerDto {@link USMessageProducerDto}
     * @param source source
     */
    public void saveToRedis(List<String> list, USMessageProducerDto producerDto, long source){
        list.forEach(item -> stringRedisTemplate.opsForZSet()
                .add(item + TopicEnum.SYSTEM.getTopic(), JSON.toJSONString(producerDto,
                        SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat),
                        source));
    }

    /**
     * 保存到redis<br/>
     *
     *  <a href='https://www.cnblogs.com/candlia/p/11919878.html'>SerializerFeature详解</a>
     *  <br/>
     *  - DisableCircularReferenceDetect: 消除对同一对象循环引用的问题，默认为false  <br/>
     *  - WriteDateUseDateFormat: 全局修改日期格式(默认是时间戳),默认为false。{@linkplain JSON#DEFFAULT_DATE_FORMAT fastjson默认时间格式} <br/>
     *
     * @param tuple tuple
     */
    public void saveToRedis(Tuple2<List<SysMessage>, String> tuple){
        tuple._1.forEach(item -> stringRedisTemplate.opsForZSet()
                .add(tuple._2 + TopicEnum.SYSTEM.getTopic(), JSON.toJSONString(USMessageProducerDto.builder()
                                .key(item.getKey())
                                .createTime(DateUtil.date(item.getCreateTime())) // DateUtil.date(source)
                                .title(item.getTitle()).content(item.getContent())
                                .build(), SerializerFeature.DisableCircularReferenceDetect,
                        SerializerFeature.WriteDateUseDateFormat), item.getCreateTime()
                        .toInstant(ZoneOffset.ofHours(8)).toEpochMilli()));
    }

    public void saveBatchToDB(Tuple2<List<SysMessage>, String> tuple){
        boolean insert = this.userSysMessageService.insertBatch(tuple._1, tuple._2);
        if (insert) {
            log.info("insertBatch success");
        } else {
            log.error("insertBatch failed");
        }
    }
}
