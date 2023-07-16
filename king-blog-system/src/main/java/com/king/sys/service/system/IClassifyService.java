package com.king.sys.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.dto.SearchDto;
import com.king.sys.bean.entity.system.Blog;
import com.king.sys.bean.entity.system.Classify;

import java.util.List;

/**
 * <p>
 *    分类表 服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
public interface IClassifyService extends IService<Classify> {

    /**
     * 查询全部
     * @return list
     */
    List<Classify> selectAll();

    /**
     * 搜索查询
     * @param type
     * @param currentPage
     * @param size
     * @param searchDto
     * @return Page<Blog>
     */
    Page<Blog> searchSelect(Integer type, Integer currentPage, Integer size, SearchDto searchDto);

}
