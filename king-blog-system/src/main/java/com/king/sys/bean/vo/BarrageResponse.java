package com.king.sys.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BarrageResponse<T> {

    private Integer total;

    private Integer pages;

    private List<T> records;

    private Map<String, Object> extras;

}
