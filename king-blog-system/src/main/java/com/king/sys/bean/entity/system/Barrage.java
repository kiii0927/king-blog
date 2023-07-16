package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.king.common.utils.EmojiUtil;
import com.king.sys.bean.dto.BarrageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 *    弹幕表
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="t_barrage")
@JsonIgnoreProperties(allowSetters = true, value = {"deleted"})
public class Barrage implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID/编号
     */
    @TableId(value = "barrage_no", type = IdType.AUTO)
    private Integer barrageNo;

    /**
     * admin 评论 0 not 1 yeah
     */
    private Integer adminComment;

    /**
     * 发送人id
     */
    private String formId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 回复的用户名
     */
    private String replyUsername;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 消息
     */
    private String message;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 用户网站
     */
    private String site;

    /**
     * 评论主键ID
     */
    private Integer commentId;

    /**
     * 父级评论ID
     */
    private Integer parentCommentId;

    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public Barrage(BarrageRequest request) {
        this.adminComment = request.getAdminComment();
        this.formId = request.getFormId();
        this.username = request.getUsername();
        Optional.ofNullable(request.getReplyUsername())
                .ifPresent(v -> this.replyUsername = v);
        this.email = request.getEmail();
        this.avatar = request.getAvatar();
        this.message = EmojiUtil.emojiConverterToHtml(request.getMessage());
        Optional.ofNullable(request.getProvince())
                .ifPresent(v -> this.province = v);
        Optional.ofNullable(request.getCity())
                .ifPresent(v -> this.city = v);
        Optional.ofNullable(request.getSite())
                .ifPresent(v -> this.site = v);
        Optional.ofNullable(request.getCommentId())
                .ifPresent(id -> this.commentId = id);
        Optional.ofNullable(request.getParentCommentId())
                .ifPresent(id -> this.parentCommentId = id);
    }

    @SuppressWarnings("all")
    public Barrage(Barrage barrage) {
        Objects.requireNonNull(barrage, "barrage entity undefined.");
        Optional.ofNullable(barrage.getBarrageNo())
                .ifPresent(v -> this.barrageNo = v);
        this.adminComment = barrage.getAdminComment();
        this.formId = barrage.getFormId();
        this.username = barrage.getUsername();
        Optional.ofNullable(barrage.getReplyUsername())
                .ifPresent(v -> this.replyUsername = v);
        this.email = barrage.getEmail();
        this.avatar = barrage.getAvatar();
        this.message = barrage.getMessage();
        this.province = barrage.getProvince();
        this.city = barrage.getCity();
        Optional.ofNullable(barrage.getCommentId())
                .ifPresent(v -> this.commentId = v);
        Optional.ofNullable(barrage.getParentCommentId())
                .ifPresent(v -> this.parentCommentId = v);
        Optional.ofNullable(barrage.getCreateTime())
                .ifPresent(v -> this.createTime = v);
    }
}
