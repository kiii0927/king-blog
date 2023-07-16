package com.king.sys.bean.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-04
 **/
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_blog")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // https://blog.csdn.net/czx2018/article/details/109024482
    @TableId(value = "id", type = IdType.ASSIGN_ID) // https://blog.csdn.net/w1014074794/article/details/125604191
    private Long id;

    /**
     * 分类表ID
     */
    private Integer cId;

    /**
     * 作者
     */
    private String author;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客类型
     */
    private String type;

    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Integer deleted;

    /**
     * 博客描述
     */
    private String description;

    /**
     * 博客首图
     */
    private String firstPicture;

    /**
     * 是否开启评论
     */
    private Integer isComment;

    /**
     * 是否发布博客
     */
    private Integer isDraft;

    /**
     * 访问量
     */
    private Integer views;

    /**
     * 评论数
     */
    @TableField(update="%s+1",updateStrategy=FieldStrategy.IGNORED)
    private Integer comments;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;

    /**
     * 原文链接
     */
    private String link;


    /**
     * 有参构造
     */
    public Blog(Blog blog) {
        Objects.requireNonNull(blog);
        this.id = blog.id;
        this.cId = blog.cId;
        this.author = blog.author;
        this.content = blog.content;
        this.title = blog.title;
        this.type = blog.type;
        this.deleted = blog.deleted;
        this.description = blog.description;
        this.firstPicture = blog.firstPicture;
        this.isComment = blog.isComment;
        this.isDraft = blog.isDraft;
        this.views = blog.views;
        this.comments = blog.comments;
        this.createTime = blog.createTime;
        this.updateTime = blog.updateTime;
        this.link = blog.link;
    }


    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", cId=" + cId +
                ", author='" + author + '\'' +
                ", content='" + "文章内容..." + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", deleted=" + deleted +
                ", description='" + description + '\'' +
                ", firstPicture='" + firstPicture + '\'' +
                ", isComment=" + isComment +
                ", isDraft=" + isDraft +
                ", views=" + views +
                ", comments=" + comments +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", link='" + link + '\'' +
                '}';
    }
}
