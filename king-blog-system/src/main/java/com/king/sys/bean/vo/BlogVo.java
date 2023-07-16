package com.king.sys.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-04
 **/
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlogVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class) // https://blog.csdn.net/czx2018/article/details/109024482
    private Long id;

    private String title;

    private String content;

    private String description;

    private String name;

    private String type;

    private String firstPicture;

    private Integer views;

    private Integer comments;

    private Integer isComment;

    private LocalDateTime createTime;
}
