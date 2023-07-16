package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.king.sys.bean.enums.LoginSignEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_validation")
public class Validation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证类型 1'注册' 2'忘记密码'
     */
    private LoginSignEnum type;

    /**
     * 过期时间
     */
    // private LocalDateTime time;
    private Date time;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastTime;

}
