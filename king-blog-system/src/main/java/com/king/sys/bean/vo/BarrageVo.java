package com.king.sys.bean.vo;

import com.king.sys.bean.entity.system.Barrage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BarrageVo extends Barrage {

    /**
     * 回复列表
     */
    private List<Barrage> replyList = new ArrayList<>();

    /*private Integer barrageNo;

    private Integer adminComment;

    private String userId; // formId

    private String username;

    private String replyUsername;

    private String email;

    private String avatar;

    private String message;

    private String province;

    private String city;

    private String site;

    private Integer commentId;

    private Integer parentCommentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;*/

    /**
     * 是否有更多回复
     */
    private Boolean hasMoreReply;

    /**
     * 加载更多回复的标识
     * 前端做处理需要
     */
    private Boolean loadingMoreReply;

    /**
     * 构造函数
     * 将 Barrage Do 转换成 Barrage Vo
     *
     * @param barrage entity
     */
    public BarrageVo(Barrage barrage) {
        super(barrage);
        // 默认为false; 则没有更多回复
        this.setHasMoreReply(false);
        // 默认为false; 则没有加载中
        this.setLoadingMoreReply(false);
    }
}
