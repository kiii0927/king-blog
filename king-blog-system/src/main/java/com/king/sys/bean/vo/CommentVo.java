package com.king.sys.bean.vo;

import com.king.sys.bean.entity.system.Comment;
import com.king.sys.bean.po.CommentPo;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Getter
@Setter
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentVo extends Comment {

    /**
     * 回复列表
     */
    //private List<Comment> replyList = new ArrayList<>();
    private List<CommentPo> replyList = new ArrayList<>();

    /**
     * 是否有更多回复
     */
    //private Boolean isHasMoreReply;
    private Boolean hasMoreReply;

    /**
     * 加载更多回复的标识
     *   前端做处理需要
     */
    private Boolean loadingMoreReply;

    /**
     * 构造函数
     *  将 User Do 转换成 User Vo
     * @param comment entity
     */
    public CommentVo(Comment comment) {
        super(comment);
        // 默认为false; 则没有更多回复
        this.setHasMoreReply(false);
        // 默认为false; 则没有加载中
        this.setLoadingMoreReply(false);
    }
}
