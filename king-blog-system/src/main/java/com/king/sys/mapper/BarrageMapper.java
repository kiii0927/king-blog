package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.king.sys.bean.entity.system.Barrage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *    针对表【t_barrage(弹幕表)】的数据库操作Mapper
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
public interface BarrageMapper extends BaseMapper<Barrage> {

    /**
     * 查询最后新增的id
     * @return int
     */
    Integer findLastInsertId();


    /**
     * 自定义list查询
     * @param wrapper wrapper
     * @return list
     */
    List<Barrage> selectMyList(@Param(Constants.WRAPPER) Wrapper<Barrage> wrapper);

    /**
     * 获取 total
     * @return total
     */
    Integer findTotal();

}
