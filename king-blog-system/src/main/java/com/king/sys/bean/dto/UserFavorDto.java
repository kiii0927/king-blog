package com.king.sys.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Getter
@Setter
@ToString
public class UserFavorDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @NotBlank(message = "`identify`不能为空")
    private String identify;

    @NotBlank(message = "文章ID不能为空")
    private String blogId;
}
