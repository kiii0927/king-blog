package com.king.sys.bean.factory;

import com.king.sys.bean.dto.UserInfoDto;
import com.king.sys.bean.entity.system.UserInfo;
import com.king.sys.bean.enums.GenderEnum;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public final class UserInfoFactory {

    private UserInfoFactory(){}

    public static UserInfo getUserInfo(UserInfoDto userInfoDto){
        return UserInfo.builder()
                .identify(userInfoDto.getToken()).nickname(userInfoDto.getNickname())
                .age(userInfoDto.getAge()).gender(GenderEnum.getRealGender(userInfoDto.getGender()))
                .phone(userInfoDto.getPhone()).home(userInfoDto.getHome())
                .occupation(userInfoDto.getOccupation()).tencentQQ(userInfoDto.getTencent_qq())
                .remark(userInfoDto.getRemark()).location(userInfoDto.getLocation())
                .authLogin(userInfoDto.isAuthLogin())
                .build();
    }
}
