package com.king.sys.bean.entity.message;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.king.common.module.constant.Constant;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 * @TableName user_sys_message
 **/
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="user_sys_message")
public class UserSysMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户消息唯一标识
     */
    @TableField(value = "uid")
    private String uid;

    /**
     * 系统消息唯一标识
     */
    @TableField(value = "sys_id")
    private String sysId;

    /**
     * 是否已读 0未读 1已读
     */
    @TableField(value = "is_read")
    private Integer isRead;

    @TableField(value = "source")
    private String source;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = Constant.CHINESE_DATE_TIME_PATTERN_MINUTE)
    private LocalDateTime createTime;

    /**
     * 操作时间
     */
    @TableField(value = "ope_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime opeTime;


    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
