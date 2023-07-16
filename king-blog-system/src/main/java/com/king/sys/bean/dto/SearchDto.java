package com.king.sys.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Getter
@Setter
@NoArgsConstructor
public class SearchDto implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 查询类别
     * 1: 发布时间  2: 浏览量
     */
    @NotNull(message = "type is null")
    private Integer type;

    /**
     * 关键字
     */
    private String keyword;

    public SearchDto(Integer type) {
        this(type, "");
    }

    public SearchDto(Integer type, String keyword) {
        this.type = type;
        this.keyword = keyword;
    }

}
