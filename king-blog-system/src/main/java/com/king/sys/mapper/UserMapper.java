package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.entity.system.User;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *    用户表 mapper 接口
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
public interface UserMapper extends BaseMapper<User> {
    /**
     * 通过用户邮箱拿到 UserId
     * @param email 用户邮箱
     * @return 用户Id
     */
    int findUserIdByEmail(@Param("email") String email);
}
