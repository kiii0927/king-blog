package com.king.sys.bean.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.king.common.validator.SystemGroup;
import com.king.common.validator.UserGroup;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *    UserGroup: 用户消息
 *    SystemGroup: 系统消息
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    //private String key;
    @NotBlank(message = "topic不能为空", groups = {UserGroup.class})
    private String topic;

    @NotBlank(message = "originalContent不能为空", groups = {UserGroup.class})
    private String originalContent;

    @NotBlank(message = "content不能为空")
    //private String replyContent;
    private String content;

    @NotBlank(message = "formId不能为空", groups = {UserGroup.class})
    private String formId; // 发送者/生产者

    @NotBlank(message = "toId不能为空", groups = {UserGroup.class})
    private String toId; // 接收者/接收者

    @NotBlank(message = "username不能为空", groups = {UserGroup.class})
    private String username;

    @NotBlank(message = "replyUsername不能为空", groups = {UserGroup.class})
    private String replyUsername;

    @NotBlank(message = "blogId不能为空", groups = {UserGroup.class})
    private String blogId;

    @NotBlank(message = "avatar不能为空", groups = {UserGroup.class})
    private String avatar;

    //@NotBlank(message = "source不能为空", groups = {SaveGroup.class})
    private String source;

    //@NotNull(message = "msgType不能为空")
    private Integer msgType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8") // 当前端传递一个string字符串时间可以解析成date类型
    @NotNull(message = "startTime不能为空", groups = {SystemGroup.class})
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "endTime不能为空", groups = {SystemGroup.class})
    private Date endTime;
}
