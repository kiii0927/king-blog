package com.king.sys.handle.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.king.common.exception.ServiceException;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ScrollResult;
import com.king.common.module.enums.TopicEnum;
import com.king.common.utils.EmojiUtil;
import com.king.sys.assets.FailureAction;
import com.king.sys.bean.entity.message.UserSysMessage;
import com.king.sys.bean.vo.message.USMessageProducerVo;
import com.king.sys.mapper.UserSysMessageMapper;
import io.vavr.Tuple;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *    SystemMessage consumer class
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Slf4j
@Component
public class SysMessageHandlerConsumer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserSysMessageMapper userSysMessageMapper;



    /**
     * 获取 message (倒序)
     * @param key 消费者标识
     * @param max 最大值(上一次查询的最小值)
     * @param offset 偏移量
     * @return {@link ScrollResult}
     */
    @Nullable
    public ScrollResult getMessageReverseSort(String key, long max, long offset){
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key + TopicEnum.SYSTEM.getTopic(), 0, max == 0 ? System.currentTimeMillis() : max,
                        offset, Constant.SOURCE_COUNT);
        ScrollResult scrollResult = this.getMessage(typedTuples);

        return ScrollResult.handleIdentical(this.getMessage(typedTuples), offset, max);
    }

    /**
     * 获取消息
     * @param typedTuples {@link Set<ZSetOperations>} 消息列表
     * @return {@link ScrollResult}
     */
    //String topic, String key, long max
    @Nullable
    private ScrollResult getMessage(Set<ZSetOperations.TypedTuple<String>> typedTuples){
        // 非空判断
        if (typedTuples == null || typedTuples.isEmpty()){
            return null;
        }

        List<USMessageProducerVo> valueList = new ArrayList<>(typedTuples.size());
        //long minTime = 0;
        final long[] minTime = {0}; // minTime
        final AtomicInteger[] os = {new AtomicInteger(1)}; // offset
        // 解析数据,填充数据
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            Try.of(() -> typedTuple)
                    .mapTry(t -> Tuple.of(Objects.requireNonNull(t.getScore()).longValue(),
                            JSON.parseObject(t.getValue(), USMessageProducerVo.class)))
                    .filter(Objects::nonNull)
                    .onSuccess(v -> {
                        // handle message
                        v._2.setSource(String.valueOf(v._1));
                        // set content. parse emoji
                        String content = v._2.getContent();
                        v._2.setContent(EmojiUtil.emojiConverterUnicodeStr(content));

                        valueList.add(v._2());
                        if (v._1 == minTime[0]){
                            os[0].getAndIncrement(); // 当前值自动加1
                        }else {
                            minTime[0] = v._1;
                            os[0].set(1);
                        }
                    })
                    .onFailure(FailureAction::accept);
        }
        // 封装数据
        return ScrollResult.builder()
                .list(valueList)
                .minTime(minTime[0])
                .offset(os[0].get())
                .build();
    }

    /**
     * 通过用户id获取对应的系统消息key
     * @param uid 用户唯一标识
     * @param lastId 查询的时间(时间戳)
     * @return list
     */
    public List<String> getSysMessageKey(String uid, long lastId, int offset){

        // 创建 wrapper
        LambdaQueryWrapper<UserSysMessage> wrapper = Wrappers.lambdaQuery(UserSysMessage.class)
                .select(UserSysMessage::getSysId)
                .le(UserSysMessage::getSource, lastId == 0 ? System.currentTimeMillis() : lastId)
                .eq(UserSysMessage::getUid, uid);

        // query
        return this.userSysMessageMapper.selectMyList(wrapper, offset);
    }

    public void updateIsRead(String uid, String sid) {
        this.userSysMessageMapper.updateIsReadAndSysId(uid, sid);
    }


    public void updateIsRead(String identify, List<String> keyList){
        // 创建 wrapper
        LambdaUpdateWrapper<UserSysMessage> wrapper = Wrappers.lambdaUpdate(UserSysMessage.class)
                // 构造sql条件
                .eq(UserSysMessage::getUid, identify)
                .in(UserSysMessage::getSysId, keyList)
                .eq(UserSysMessage::getIsRead, 0)
                // 修改值
                .set(UserSysMessage::getUpdateTime, LocalDateTime.now()) // update(T t,Wrapper updateWrapper)时t不能为空,否则自动填充失效
                .set(UserSysMessage::getIsRead, 1);
        this.userSysMessageMapper.update(null, wrapper);
    }

    /**
     * 查询 `uid` user_user_message 在表中最后一条数据
     * @param uid 用户id
     * @return {@link UserSysMessage}
     */
    @Nullable
    public UserSysMessage getLastUserSysMessage(String uid){
        // 查询 `uid` user_user_message 在表中最后一条数据
        LambdaQueryWrapper<UserSysMessage> wrapper = Wrappers.lambdaQuery(UserSysMessage.class)
                .select(UserSysMessage::getSysId)
                .eq(UserSysMessage::getUid, uid)
                .orderByDesc(UserSysMessage::getCreateTime)
                .last("limit 1");
        // select
        UserSysMessage userSysMessage = userSysMessageMapper.selectOne(wrapper);

        // return
        return Optional.ofNullable(userSysMessage)
                .orElseThrow(ServiceException::new);
    }

}
