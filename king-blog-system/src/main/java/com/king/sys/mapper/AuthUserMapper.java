package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.entity.auth.AuthUser;
import org.apache.ibatis.annotations.Param;

/**
 * 第三方授权表 Mapper 接口
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public interface AuthUserMapper extends BaseMapper<AuthUser> {

    String findTokenByUuidAndSource(@Param("uuid") String uuid, @Param("source") String source);
}
