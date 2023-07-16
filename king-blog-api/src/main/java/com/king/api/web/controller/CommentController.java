package com.king.api.web.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.common.module.domain.ResponseResult;
import com.king.common.validator.SaveGroup;
import com.king.common.validator.ValidatorGroup;
import com.king.core.aspectj.preven.Prevent;
import com.king.core.aspectj.redisLimit.RedisLimit;
import com.king.sys.bean.dto.CommentDto;
import com.king.sys.bean.entity.system.Comment;
import com.king.sys.service.system.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *    评论表 前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@RestController
@RequestMapping("/v1/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;


    /**
     * 通过文章 id 获取评论数据, 并分页
     * @param bId 文章 id
     * @param currentPage 当前页
     * @param size 页面大小
     */
    @GetMapping("/{bId}")
    public ResponseResult<Map<String, Object>> select(@PathVariable String bId, @RequestParam(name = "currentPage") Integer currentPage,
                                                      @RequestParam(name = "pageSize") Integer size){
        return ResponseResult.success(commentService.selectPageByIdCache(bId, currentPage, size));
    }

    /**
     * 保存用户评论
     */
    @Prevent(message = "操作成功", value = "10")
    @RedisLimit(key = "comment:save", permitsPerSecond = 500, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    @PostMapping("/save")
    public Comment save(@RequestBody @Validated(SaveGroup.class) CommentDto commentDto){
        return commentService.save(commentDto, true);
    }

    /**
     * 保存用户回复评论
     */
    @RedisLimit(key = "comment:reply", permitsPerSecond = 500, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    @Prevent(message = "操作成功", value = "10")
    @PostMapping("/reply")
    public Comment reply(@RequestBody @Validated(ValidatorGroup.class) CommentDto commentDto){
        return commentService.save(commentDto, false);
    }

    /**
     * 通过评论模块 ID 获取更多评论回复数据
     */
    @GetMapping("/getMoreReplyByCommentId")
    public ResponseResult<Object> selectMoreReplyByCommentId(@RequestParam Integer commentId,
                                                     @RequestParam Integer pageSize,
                                                     @RequestParam String dateTime){
        Page<Comment> page = commentService.selectMoreReplyByCommentId(commentId, pageSize, dateTime);
        return ResponseResult.success(MapUtil.builder()
                .put("total",page.getTotal())
                .put("pages",page.getPages())
                .put("next",page.hasNext())
                .put("previous",page.hasPrevious())
                .put("records",page.getRecords())
                .map());
    }
}
