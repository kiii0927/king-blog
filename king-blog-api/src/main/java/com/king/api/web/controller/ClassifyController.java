package com.king.api.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.common.module.domain.ResponseResult;
import com.king.sys.bean.dto.SearchDto;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.entity.system.Classify;
import com.king.sys.service.system.IClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *    分类表 前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@RestController
@RequestMapping("/v1/classify")
public class ClassifyController {

    @Autowired
    private IClassifyService classifyService;

    /**
     * 查询所有类别
     */
    @GetMapping("/selectAll")
    public ResponseResult<List<Classify>> selectAll(){
        return ResponseResult.success(classifyService.selectAll());
    }

    /**
     * 查询单个分类所有数据
     * @param type index
     * @param currentPage 当前页
     * @param size 页面大小
     * @param searchDto dto
     */
    @PostMapping("/selectOneAll/{type}")
    public ResponseResult<Page<Blog>> selectOneAll(@PathVariable Integer type, @RequestParam(name = "currentPage") Integer currentPage,
                                                   @RequestParam(name = "pageSize") Integer size, @RequestBody @Validated SearchDto searchDto){
        return ResponseResult.success(classifyService.searchSelect(type,currentPage,size,searchDto));
    }

    /**
     * 搜索查询
     * @param type 类型
     * @param currentPage 当前页
     * @param size 页面大小
     * @param searchDto dto
     */
    @PostMapping("/search/{type}")
    public ResponseResult<Page<Blog>> searchSelect(@PathVariable Integer type, @RequestParam(name = "currentPage") Integer currentPage,
                                           @RequestParam(name = "pageSize") Integer size, @RequestBody @Validated SearchDto searchDto){
        return ResponseResult.success(classifyService.searchSelect(type, currentPage, size, searchDto));
    }

}
