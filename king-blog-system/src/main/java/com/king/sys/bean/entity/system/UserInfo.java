package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.king.sys.bean.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *   用户信息表
 * </p>
 *
 * @author king
 * @since 2022-11-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_info")
public class UserInfo implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * token, 用户唯一标识
     */
    @TableField("token")
    private String identify;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别  0 女  1 男  -1 未知/其他
     */
    private GenderEnum gender;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 个人主页
     */
    private String home;

    /**
     * 个人职业
     */
    private String occupation;

    /**
     * 腾讯QQ
     */
    @TableField("tencent_qq")
    private String tencentQQ;

    /**
     * 用户备注（各平台中的用户个人介绍）
     */
    private String remark;

    /**
     * 位置
     */
    private String location;

    /**
     * 第三方登录
     */
    @TableField(value = "auth_login")
    private boolean authLogin;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
