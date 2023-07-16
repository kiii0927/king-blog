package com.king.sys.bean.dto.message;

import com.king.common.module.enums.TopicEnum;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author king
 * @since 2023-04-10
 **/
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageConsumerDto {

    private String identify;

    private String topic = TopicEnum.SYSTEM.getTopic();

    /**
     * 上一次查询的最小值 | 如果是第一次请求, 则是当前是时间的时间戳
     */
    @NotNull(message = "lastId不能为空")
    private Long lastId;

    /**
     * 偏移量 0: 包含上次查询的最大值  1: 不包含上次查询的最大值, 从它后一位查
     */
    @NotNull(message = "offset不能为空")
    private Integer offset = 0;

}
