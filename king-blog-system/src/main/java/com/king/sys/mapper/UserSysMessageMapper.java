package com.king.sys.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.king.sys.bean.entity.message.UserSysMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 * @description 针对表【user_sys_message】的数据库操作Mapper
 **/
@DS("slave")
public interface UserSysMessageMapper extends BaseMapper<UserSysMessage> {

    /**
     * 自定义list查询
     * @param wrapper wrapper
     * @return list
     */
    List<String> selectMyList(@Param(Constants.WRAPPER) Wrapper<UserSysMessage> wrapper, int offset);

    /**
     * 修改成已读
     * @param uid uid
     * @param sid sid
     */
    Integer updateIsReadAndSysId(@Param("uid") String uid, @Param("sid") String sid);

}
