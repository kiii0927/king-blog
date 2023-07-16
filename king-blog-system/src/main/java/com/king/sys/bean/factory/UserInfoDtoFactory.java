package com.king.sys.bean.factory;

import cn.hutool.json.JSONObject;
import com.king.sys.bean.dto.UserInfoDto;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class UserInfoDtoFactory {

    private UserInfoDtoFactory(){}

    public static UserInfoDto getUserInfoDto(JSONObject jsonObject){
        return getUserInfoDto(jsonObject, true);
    }

    public static UserInfoDto getUserInfoDto(JSONObject jsonObject, boolean authLogin){
        return UserInfoDto.builder()
                .username(jsonObject.getStr("username")).nickname(jsonObject.getStr("nickname"))
                .gender(jsonObject.getStr("gender")).home(jsonObject.getStr("blog"))
                .remark(jsonObject.getStr("remark")).location(jsonObject.getStr("location"))
                .authLogin(authLogin)
                .build();
    }

}
