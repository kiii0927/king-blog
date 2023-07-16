package com.king.sys.bean.factory;

import com.king.common.module.constant.Constant;
import com.king.common.utils.MD5Util;
import com.king.sys.bean.dto.UserRegisterDto;
import com.king.sys.bean.entity.system.User;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
public final class UserFactory {

    private UserFactory(){}

    public static User getUser(UserRegisterDto userRegisterDto){
         return User.builder()
                .username(userRegisterDto.getUsername())
                .originalPassword(userRegisterDto.getPassword())
                .password(MD5Util.inputPassToDBPass(userRegisterDto.getPassword(), Constant.SALT))
                .email(userRegisterDto.getEmail())
                //.loginTime(LocalDateTime.now())
                .build();
    }

}
