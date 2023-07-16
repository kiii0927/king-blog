package com.king.sys.bean.po;

import com.king.sys.bean.entity.system.Comment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@EqualsAndHashCode(callSuper = true)
public class CommentPo extends Comment {
    /**
     * 将 User Do 转换成 User Po
     * @param comment entity
     */
    public CommentPo(Comment comment) {
        super(comment);
    }
}
