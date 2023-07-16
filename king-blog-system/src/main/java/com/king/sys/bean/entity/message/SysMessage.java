package com.king.sys.bean.entity.message;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.king.common.module.constant.Constant;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 * @TableName sys_message
 **/
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="sys_message")
@JsonIgnoreProperties(allowSetters = true, value = {"deleted"})
public class SysMessage implements Serializable {

    //@TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * key
     */
    //@TableId(value = "key")
    //@TableField(value = "`key`", fill = FieldFill.INSERT)
    @TableField(value = "`key`")
    private String key;

    /**
     * 主题
     */
    @TableField(value = "topic")
    private String topic;

    /**
     * 消息标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 系统消息 默认为0
     */
    @TableField(value = "msg_type")
    private Integer msgType;

    /**
     * 消息有效时间（开始时间）
     */
    @TableField(value = "start_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date startTime;

    /**
     * 消息失效时间（结束时间）
     */
    @TableField(value = "end_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date endTime;

    /**
     * 是否被删除 0 未删除 1 已删除
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = Constant.CHINESE_DATE_TIME_PATTERN_MINUTE)
    private LocalDateTime createTime;

    /**
     * 操作更新时间
     */
    @TableField(value = "update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
