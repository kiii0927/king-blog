package com.king.sys.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Getter
@Setter
@ToString
public class FavorVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 唯一标识
     */
    private String identify;

    private String title;

    private String firstPicture;

    private String name;

    private LocalDateTime createTime;

}
