package com.king.sys.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.king.sys.bean.entity.message.UserMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 * @description 针对表【user_message】的数据库操作Mapper
 **/
@DS("slave")
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    /**
     * 自定义list查询
     * @param wrapper wrapper
     * @return list
     */
    List<UserMessage> selectMyList(@Param(Constants.WRAPPER) Wrapper<UserMessage> wrapper);
}
