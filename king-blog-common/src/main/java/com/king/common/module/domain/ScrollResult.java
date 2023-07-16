package com.king.common.module.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * scroll result
 *
 * @author king
 * @since 2023-03-29
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrollResult {

    private List<?> list;

    /**
     * 查询的最小时间戳(lastId)
     */
    private Long minTime;

    /**
     * 偏移量
     */
    private Integer offset;


    public static ScrollResult handleIdentical(ScrollResult scrollResult, long offset, long max){

        if (scrollResult == null || scrollResult.getList() == null){
            return scrollResult;
        }

        // offset minTime 值没有发生变化表示是最后的值
        // scrollResult.getOffset() == (int) offset
        if (scrollResult.getOffset() == (int) offset && scrollResult.getMinTime() == max){
            scrollResult.setOffset((int) ++offset);
        }

        return scrollResult;
    }

}
