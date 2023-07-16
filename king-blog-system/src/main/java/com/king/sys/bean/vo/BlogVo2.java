package com.king.sys.bean.vo;

import com.king.sys.bean.entity.system.Blog;
import lombok.*;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class BlogVo2 extends Blog {

    private String strCreateTime;

    /**
     * 有参构造
     *   实现 do 转 vo
     * @param blog
     */
    public BlogVo2(Blog blog){
        super(blog);
    }

}
