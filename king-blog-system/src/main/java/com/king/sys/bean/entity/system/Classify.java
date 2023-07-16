package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *    分类表
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_classify")
public class Classify implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 作者
     */
    private String author;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
