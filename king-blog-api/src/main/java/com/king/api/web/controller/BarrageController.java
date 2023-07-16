package com.king.api.web.controller;

import com.king.common.module.domain.ResponseResult;
import com.king.common.validator.SaveGroup;
import com.king.common.validator.ValidatorGroup;
import com.king.core.aspectj.redisLimit.RedisLimit;
import com.king.sys.bean.dto.BarrageRequest;
import com.king.sys.bean.entity.system.Barrage;
import com.king.sys.bean.vo.BarrageResponse;
import com.king.sys.bean.vo.BarrageVo;
import com.king.sys.bean.vo.MemorialVo;
import com.king.sys.service.system.IBarrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *    弹幕表 前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@RestController
@RequestMapping("/v1/barrage")
public class BarrageController {

    @Autowired
    private IBarrageService barrageService;

    /**
     * 评论
     * @param barrageRequest 请求参数
     */
    @RedisLimit(key = "say:comment", permitsPerSecond = 500, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    @PostMapping("/say/comment")
    public ResponseResult<Barrage> sayComment(@RequestBody @Validated(SaveGroup.class) BarrageRequest barrageRequest){
        return barrageService.comment(barrageRequest);
    }

    /**
     * 回复
     * @param barrageRequest 请求参数
     */
    @RedisLimit(key = "say:reply", permitsPerSecond = 500, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    @PostMapping("/say/reply")
    public ResponseResult<Barrage> sayReply(@RequestBody @Validated(ValidatorGroup.class) BarrageRequest barrageRequest){
        return barrageService.reply(barrageRequest);
    }


    /**
     * query barrage data(评论模块)
     * @param currentPage 当前页
     * @param size 页面大小
     * @return res
     */
    @GetMapping("/page-query")
    public ResponseResult<BarrageResponse<BarrageVo>> query(@RequestParam(name = "currentPage") Integer currentPage,
                                                    @RequestParam(name = "pageSize") Integer size){
        return ResponseResult.success(barrageService.pagingQuery(currentPage, size));
    }

    /**
     * 获取弹幕(滑动模块)
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/get-memorial")
    public ResponseResult<BarrageResponse<MemorialVo>> memorial(@RequestParam(name = "page") Integer page,
                                                        @RequestParam(name = "limit") Integer limit){
        return ResponseResult.success(barrageService.getBarrage(page, limit));
    }

}
