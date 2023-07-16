package com.king.sys.service.message;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.common.module.domain.ScrollResult;
import com.king.sys.bean.entity.message.UserMessage;
import com.king.sys.bean.vo.message.UMessageVo;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 * @description 针对表【user_message】的数据库操作Service
 **/
public interface IUserMessageService extends IService<UserMessage> {

    /**
     * 生成一条消息
     * @param message
     */
    void producerOneMessage(UserMessage message);

    /**
     * 生成消息到redis
     * @param list data
     */
    void producerMessageToRedis(List<UserMessage> list);


    /**
     * 查询消息
     * @param identify 用户唯一标识
     * @param topic 消息主题
     * @param lastId 上一次查询的最小值(本次查询的最大值)
     * @param offset 偏移量
     * @return {@link ScrollResult}
     */
    @Nullable
    ScrollResult selectMessage(String identify, String topic, long lastId, Integer offset);

}
