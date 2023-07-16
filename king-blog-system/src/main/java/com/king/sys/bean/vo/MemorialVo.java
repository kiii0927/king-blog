package com.king.sys.bean.vo;

import lombok.*;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemorialVo {

    private Integer barrageNo;

    private String message;

    private String formId;

    private String email;

    private String avatar;

    private Integer line;

}
