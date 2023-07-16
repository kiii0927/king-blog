package com.king.sys.bean.vo.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.king.common.module.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;


    private String key;

    /**
     * 文章id
     */
    private String blogId;

    /**
     * username
     */
    private String username;

    /**
     * replyUsername
     */
    private String replyUsername;

    /**
     * avatar
     */
    private String avatar;

    /**
     * source
     */
    private String source;

    /**
     * 回复内容
     */
    private String replyContent;

    /**
     * 原文内容
     */
    private String originalContent;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // @DateTimeFormat注解的功能是将一个日期字符串转化为对应的Date类型，主要处理前端时间类型与后端pojo对象中的成员变量进行数据绑定，在注解属性patttern中所约束的时间格式并不会影响后端返回前端的时间类型数据格式
    @JsonFormat(locale="zh", timezone="GMT+8", pattern= Constant.CHINESE_DATE_TIME_PATTERN_MINUTE) // @JsonFormat注解的功能是处理请求中的JSON日期字符串和查询数据库中的日期类型。即可约束时间类型的请求格式和响应格式。
    private Date createTime;
}
