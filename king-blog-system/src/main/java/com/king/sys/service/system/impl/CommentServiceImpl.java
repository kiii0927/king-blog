package com.king.sys.service.system.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.king.common.exception.ServiceException;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ResponseCode;
import com.king.common.utils.EmojiUtil;
import com.king.common.utils.EntityUtil;
import com.king.sys.bean.dto.CommentDto;
import com.king.sys.bean.entity.system.Comment;
import com.king.sys.bean.factory.CommentFactory;
import com.king.sys.bean.po.CommentPo;
import com.king.sys.bean.vo.CommentVo;
import com.king.sys.mapper.BlogMapper;
import com.king.sys.mapper.CommentMapper;
import com.king.sys.service.system.ICommentService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *    评论表服务实现类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private BlogMapper blogMapper;

    //@Autowired
    //private TransactionTemplate transactionTemplate;

    // @Autowired
    // private ApplicationEventPublisher publisher;

    /**
     * caffeine缓存
     */
    private final Cache<String, Object> comment_cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(180, TimeUnit.SECONDS)
            .build();

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> selectPageByIdCache(String bId, Integer currentPage, Integer size) {
        return (Map<String, Object>) comment_cache.get(bId, key -> {
            Tuple2<Page<Comment>, List<CommentVo>> tuple2 = this.selectPageById(bId, currentPage, size);
            Page<Comment> page = tuple2._1;
            List<CommentVo> commentVoList = tuple2._2;
            return MapUtil.builder(new HashMap<String, Object>())
                    .put("total",page.getTotal())
                    .put("pages",page.getPages())
                    .put("next",page.hasNext())
                    .put("previous",page.hasPrevious())
                    .put("records",commentVoList)
                    .map();
        });
    }

    /**
     * 根据文章 id 查询评论数据, 并分页
     *
     * @param bId 文章 id
     * @param currentPage 当前页 init 1
     * @param size  页面大小 init 5
     * @return page
     */
    @Override
    public Tuple2<Page<Comment>, List<CommentVo>> selectPageById(String bId, Integer currentPage, Integer size){
        // 创建 wrapper
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery(Comment.class);

        Tuple2<Page<Comment>, List<CommentVo>> tuple2 = this.findParent(bId, currentPage, size, wrapper);
        Optional.ofNullable(tuple2)
                .orElseThrow(ServiceException::new);
        //List<CommentVo> commentVoList = tuple2._2;
        this.findChildren(bId, tuple2._2, wrapper);

        return tuple2;
    }

    /**
     * 查询一级评论 (父级)
     *
     * @param bId 文章id
     * @param currentPage 当前页
     * @param size 页面大小
     * @return {@link Tuple2}
     */
    protected Tuple2<Page<Comment>, List<CommentVo>> findParent(String bId, Integer currentPage, Integer size,
                                                                LambdaQueryWrapper<Comment> wrapper){
        wrapper.eq(Comment::getBlogId, Long.parseLong(bId))
                .isNull(Comment::getParentCommentId)
                .orderByDesc(Comment::getCreateTime);

        // select
        Page<Comment> page = commentMapper.selectPage(new Page<>(currentPage, size), wrapper);
        List<Comment> commentList = page.getRecords();

        return Tuple.of(page, EntityUtil.toList(commentList, CommentVo::new));
    }

    /**
     * 查询回复评论 (子级) all
     * @param bId 文章id
     * @param parentList 查询的list
     */
    protected void findChildren(String bId, List<CommentVo> parentList, LambdaQueryWrapper<Comment> wrapper){
        wrapper.clear();
        // 查出子评论(回复)
        wrapper.eq(Comment::getBlogId, Long.parseLong(bId)).
                isNotNull(Comment::getParentCommentId).
                orderByDesc(Comment::getCreateTime);

        Optional.ofNullable(commentMapper.selectList(wrapper))
                .ifPresent(list -> {
                    List<CommentPo> commentPoList = EntityUtil.toList(list, CommentPo::new);
                    // 分组
                    // Map<Integer, List<Comment>> map = commentList.stream().collect(Collectors.groupingBy(Comment::getCommentId));
                    // 这里我们使用 po 接收, 因为评论的内容会带有emoji表情包, 在get函数中处理的话, 评论数据保存不成功, 所以在 po 中做处理, 做一层转化
                    Map<Integer, List<CommentPo>> map = commentPoList
                            .stream()
                            .collect(Collectors.groupingBy(CommentPo::getCommentId));
                    // 拿到 iterator
                    Iterator<Integer> iterator = map.keySet().iterator();
                    // 只取前5条
                    Map<Integer, List<CommentPo>> integerListMap = this.filterMap(map);
                    // 添加回复数据
                    parentList.forEach(commentVo -> {
                        if (Objects.nonNull(map.get(commentVo.getId()))) {
                            //commentVo.setReplyList(map.get(commentVo.getId()));
                            commentVo.setReplyList(integerListMap.get(commentVo.getCommentId()));
                            this.changeMoreReply(iterator, map, commentVo);
                        }
                    });
                });
    }

    /**
     * 过滤`map`, 只获取前5条数据
     * @param map 需要过滤的map
     * @return 过滤后的`map`
     */
    protected Map<Integer, List<CommentPo>> filterMap(Map<Integer, List<CommentPo>> map){
        Map<Integer, List<CommentPo>> newMap = new HashMap<>();
        for (Integer next : map.keySet()) {
            List<CommentPo> list = map.get(next);
            if (list.size() <= Constant.COMMENT_PAGE_SIZE) {
                newMap.put(next, list);
            }
            if (list.size() > Constant.COMMENT_PAGE_SIZE) {
                List<CommentPo> iteratorList = list.subList(0, Constant.COMMENT_PAGE_SIZE);
                newMap.put(next, iteratorList);
            }
        }
        return newMap;
    }

    /**
     * `更改`是否有更多回复
     * @param iterator 迭代器
     * @param map map
     * @param commentVo 评论 VO 类
     */
    protected void changeMoreReply(Iterator<Integer> iterator, Map<Integer, List<CommentPo>> map, CommentVo commentVo){
        if(iterator.hasNext()){
            List<CommentPo> commentPos = map.get(iterator.next());
            // 回复列表的数据大于规定展示的页面大小时, 则有更多回复; 否则没有更多回复, 默认值在构造函数中已做处理为`false`
            if (commentPos.size() > Constant.COMMENT_PAGE_SIZE) {
                commentVo.setHasMoreReply(true);
            }
        }
    }

    //@Override
    //public CommentVo saveReplyComment(CommentDto commentDto) {
    //
    //    Comment comment = CommentFactory.getComment(commentDto, false);
    //
    //    // insert 后主键会自动 set 到实体的 ID 字段，所以你只需要 getId() 就好
    //    transactionTemplate.execute(status -> {
    //        // 修改评论数
    //        this.blogMapper.updateCommentsById(comment.getBlogId());
    //
    //        boolean save = super.save(comment);
    //        if(!save) {
    //            throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
    //        }
    //        return Boolean.TRUE;
    //    });
    //    // query the comments created
    //    Comment one = commentMapper.selectOne(Wrappers.lambdaQuery(Comment.class)
    //            //.eq(Comment::getUserId, comment.getUserId())
    //            //.eq(Comment::getCreateTime, comment.getCreateTime()));
    //            .eq(Comment::getId, comment.getId()));
    //    //.orderByDesc(Comment::getCreateTime));
    //
    //    return EntityUtil.toObj(one, CommentVo::new);
    //}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment save(CommentDto commentDto, boolean flag){
        Comment comment = CommentFactory.getComment(commentDto, flag);
        if (flag) {
            comment.setCommentId(commentMapper.selectMax() + 1);
        }
        //transactionTemplate.execute(status -> {
        //    // 修改评论数
        //    int insert = this.commentMapper.insert(comment);
        //    if(insert <= 0) {
        //        throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
        //    }
        //    return Boolean.TRUE;
        //});

        int insert = this.commentMapper.insert(comment);
        if(insert <= 0) {
            throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
        }

        return flag ? EntityUtil.toObj(comment, CommentVo::new) : comment;
    }

    @Override
    public Page<Comment> selectMoreReplyByCommentId(Integer commentId, Integer pageSize, String dateTime) {
        Assert.notNull(commentId, ResponseCode.PARAM_IS_BLANK.getMessage());
        Assert.notNull(pageSize, ResponseCode.PARAM_IS_BLANK.getMessage());
        return (Page<Comment>) commentMapper.findMoreReplyByCommentId(new Page<>(1, pageSize), dateTime, commentId);
    }

    @Override
    public Page<Comment> selectPages(String bId, Integer currentPage, Integer size) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = Wrappers.lambdaQuery(Comment.class)
                .eq(Comment::getBlogId, Long.parseLong(bId))
                .isNull(Comment::getParentCommentId);

        return commentMapper.selectPage(new Page<>(currentPage, size), lambdaQueryWrapper);
    }

    ///**
    // * 创建 Comment
    // * @param commentDto {@link CommentDto}
    // * @param flag true/false 评论/回复
    // * @return {@link Comment}
    // */
    //private Comment createComment(CommentDto commentDto, boolean flag){
    //    Objects.requireNonNull(commentDto,"commentDto 参数是必须的，不能为空");
    //    Comment comment = new Comment();
    //    comment.setAdminComment(commentDto.getAdminComment() != 0)
    //            .setUserId(commentDto.getFormId())
    //            .setUsername(commentDto.getUsername())
    //            .setEmail(commentDto.getEmail())
    //            .setAvatar(commentDto.getAvatar())
    //            .setContent(EmojiUtil.emojiConverterToAlias(commentDto.getContent()))
    //            .setBlogId(Long.parseLong(commentDto.getBlogId()))
    //            .setParentCommentId(commentDto.getParentCommentId());
    //    // 评论
    //    if (flag){
    //        comment.setCommentId(commentMapper.selectMax() + 1);
    //    }else { // 回复
    //        comment.setCommentId(commentDto.getCommentId())
    //                .setReplyUsername(commentDto.getReplyUsername());
    //    }
    //    return comment;
    //}
}
