package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *    用户收藏文章表
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user_favor")
public class UserFavor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户唯一标识
     */
    private String identify;

    /**
     * 文章ID
     */
    private Long bId;

    /**
     * 逻辑删除 0 未删除 1 已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
