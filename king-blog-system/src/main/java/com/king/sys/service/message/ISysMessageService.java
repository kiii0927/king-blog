package com.king.sys.service.message;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.common.module.domain.ScrollResult;
import com.king.sys.bean.entity.message.SysMessage;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
public interface ISysMessageService extends IService<SysMessage> {

    /**
     * producer One message
     * @param startTime 失效时间
     * @param endTime 失效时间
     * @param content message 内容
     */
    void producerOneMessage(Date startTime, Date endTime, String content);

    /**
     * 通过 message 的 key 查询
     * @param key key
     * @return {@link SysMessage}
     */
    SysMessage selectMessage(String key);

    /**
     * 获取 message 创建时间
     * @param uid 用户id
     * @return create time
     */
    LocalDateTime getMessageCreateTime(String uid);

    /**
     * query message
     * @param identify 用户唯一标识
     * @param lastId 上一次查询的最小值(本次查询的最大值)
     * @param offset 偏移量
     * @return {@link ScrollResult}
     */
    @Nullable
    ScrollResult selectMessage(String identify, Long lastId, Integer offset);
}
