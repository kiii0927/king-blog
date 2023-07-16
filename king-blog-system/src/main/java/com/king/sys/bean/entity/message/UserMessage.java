package com.king.sys.bean.entity.message;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.king.common.module.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="user_message")
public class UserMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * key
     */
    @TableField(value = "`key`", fill = FieldFill.INSERT)
    private String identify;

    /**
     * 主题
     */
    @TableField(value = "topic")
    private String topic;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 发送者id
     */
    @TableField(value = "form_id")
    private String formId;

    /**
     * 接受者id
     */
    @TableField(value = "to_id")
    private String toId;

    /**
     * source
     */
    @TableField(value = "source")
    private String source;

    /**
     * 消息类型 用户消息为1, 系统消息为 0
     */
    @TableField(value = "msg_type")
    private Integer msgType;

    /**
     * 是否已读 0未读1已读
     */
    @TableField(value = "is_read")
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = Constant.CHINESE_DATE_TIME_PATTERN_MINUTE)
    private LocalDateTime createTime;

    /**
     * 操作更新时间【不由程序控制，由mysql系统控制】
     */
    @TableField(value = "ope_time")
    private LocalDateTime opeTime;

}
