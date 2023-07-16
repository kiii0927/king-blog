package com.king.sys.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.entity.message.SysMessage;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-09
 * @description 针对表【sys_message】的数据库操作Mapper
 **/
@DS("slave")
public interface SysMessageMapper extends BaseMapper<SysMessage> {

    /**
     * 根据key查询
     * @param key key
     * @return {@link SysMessage}
     */
    SysMessage selectByKey(@Param("key") String key);

    /**
     * 通过用户 id 查询 create time
     * @param uid 用户id
     * @return create time
     */
    LocalDateTime findCreate_time(String uid);
}
