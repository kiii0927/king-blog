package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.entity.system.Validation;

/**
 * <p>
 *    mapper 接口
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
public interface ValidationMapper extends BaseMapper<Validation> {

    default void addCode(Validation validation) {
        // 创建 wrapper
        LambdaQueryWrapper<Validation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Validation::getEmail, validation.getEmail())
                .eq(Validation::getType, validation.getType());
        // 如果有同类型的验证, 直接修改就好了, 没有的话就新增一个
        if (selectOne(wrapper) != null) {
            update(validation, wrapper);
        } else{
            //save(validation);
            insert(validation);
        }
    }
}
