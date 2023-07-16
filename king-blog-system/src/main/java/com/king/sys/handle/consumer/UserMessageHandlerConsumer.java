package com.king.sys.handle.consumer;

import com.alibaba.fastjson.JSON;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ScrollResult;
import com.king.common.utils.EmojiUtil;
import com.king.sys.bean.vo.message.UMessageVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>
 *    userMessage consumer class
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Slf4j
@Component
public class UserMessageHandlerConsumer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取消息; 倒序模式
     * @param key 被回复的人[接收者,消费者]
     * @param max 最大值(上一次查询的最小值)
     * @param offset 偏移量
     * @return {@link ScrollResult}
     */
    public ScrollResult getMessageReverseSort(String key, long max, long offset){
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max,
                        offset, Constant.SOURCE_COUNT);

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
            // receiverId: 消费者ID
            return null;
        }

        List<UMessageVo> valueList = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 1;
        // 解析数据,填充数据
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            long time = Objects.requireNonNull(typedTuple.getScore()).longValue();
            UMessageVo uMessageVo = JSON.parseObject(typedTuple.getValue(), UMessageVo.class);

            Objects.requireNonNull(uMessageVo, "uMessageVo can't be null.");
            // setter
            uMessageVo.setOriginalContent(EmojiUtil.emojiConverterUnicodeStr(uMessageVo.getOriginalContent()));
            uMessageVo.setReplyContent(EmojiUtil.emojiConverterUnicodeStr(uMessageVo.getReplyContent()));
            uMessageVo.setSource(Long.toString(time));

            valueList.add(uMessageVo);

            if (time == minTime){
                os++;
            }else {
                minTime = time;
                os = 1;
            }
        }

        // 封装数据
        return ScrollResult.builder()
                .list(valueList).minTime(minTime).offset(os)
                .build();
    }

    /**
     * delete a message
     * @param key key must not be null.
     * @param source source must not be null.
     * @return 返回删除元素的个数
     */
    public Long delMessage(String key, Object ...source){
        // 返回删除的个数
        return stringRedisTemplate.opsForZSet()
                .remove(key, source);
    }

    ///**
    // * 解析map中emoji内容
    // *  replyContent
    // *  originalContent
    // */
    //private void parseContent(Map<String, Object> map, String ...target){
    //    map.forEach((key, value) -> {
    //        for (String s : target) {
    //            if (key.equals(s)){
    //                value = EmojiUtil.emojiConverterUnicodeStr((String) value);
    //                map.put(key, value);
    //            }
    //        }
    //    });
    //}
    //
    ///**
    // * 解析 map中 emoji 内容 <br />
    // *  - replyContent <br />
    // *  - originalContent
    // * @param map map
    // */
    //private void parseContent(@NonNull Map<String, Object> map){
    //    map.forEach((key, value) -> {
    //        if (key.equals("replyContent") || key.equals("originalContent")){
    //            value = EmojiUtil.emojiConverterUnicodeStr((String) value);
    //            map.put(key, value);
    //        }
    //    });
    //}
}
