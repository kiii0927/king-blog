package com.king.api.web.controller;

import com.king.common.module.domain.ResponseResult;
import com.king.core.aspectj.redisLimit.RedisLimit;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.vo.BlogVo;
import com.king.sys.service.system.IBlogService;
import io.vavr.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *    博客表 前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-04
 **/
@RestController
@RequestMapping("/v1/blog")
public class BlogController {

    @Autowired
    //@Qualifier("blogServiceImpl")
    private IBlogService blogService;

    /**
     * 查询所有并分页
     */
    @GetMapping("/selectAll")
    public ResponseResult<Map<String, Object>> getAllPage(@RequestParam(name = "currentPage") Integer currentPage,
                                     @RequestParam(name = "pageSize") Integer size) {
        return ResponseResult.success(blogService.selectListCache(currentPage,size));
    }

    /**
     * 获取 blog 详情
     */
    @RedisLimit(key = "redis-limit:BlogDetail", permitsPerSecond = 100, expire = 1, msg = "当前请求人数较多，请稍后再试！")
    @GetMapping("/getBlogDetail/{id}")
    public ResponseResult<BlogVo> getBlogDetail(@PathVariable String id){
        return ResponseResult.success(blogService.selectBlogDetailByIdCache(id));
    }

    /**
     * 获取时间轴列表数据
     */
    @RedisLimit(key = "redis-limit:archives", permitsPerSecond = 100, expire = 1, msg = "当前请求人数较多，请稍后再试！")
    @GetMapping("/archives")
    public ResponseResult<Map<String, Object>> archives(@RequestParam(name = "currentPage") Integer currentPage,
                                   @RequestParam(name = "pageSize") Integer size){
        return ResponseResult.success(this.blogService.getTimelineMap(currentPage, size));
    }

    /**
     * blog information
     */
    @GetMapping("/information")
    public ResponseResult<Tuple3<List<Blog>, List<Blog>, Map<String, Integer>>> information(){
        return ResponseResult.success(blogService.getInformation());
    }
}
