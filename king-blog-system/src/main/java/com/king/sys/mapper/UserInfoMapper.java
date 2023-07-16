package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.dto.UserInfoDto;
import com.king.sys.bean.entity.system.UserInfo;
import com.king.sys.bean.enums.GenderEnum;
import org.apache.ibatis.annotations.Param;

/**
 * 用户信息表 Mapper 接口
 *
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 根据 token 查询用户性别
     * @param token 用户唯一标识
     * @return int
     */
    Integer findGenderByToken(@Param("token") String token);

    default void insert(UserInfoDto userInfoDto, GenderEnum realGender){
        String token = userInfoDto.getToken();
        // create wrapper
        LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInfo::getIdentify, token);
        if (!this.exists(wrapper)) {
            this.insert(UserInfo.builder()
                    .identify(token).nickname(userInfoDto.getNickname())
                    .age(userInfoDto.getAge()).gender(realGender)
                    .phone(userInfoDto.getPhone()).home(userInfoDto.getHome())
                    .occupation(userInfoDto.getOccupation()).tencentQQ(userInfoDto.getTencent_qq())
                    .remark(userInfoDto.getRemark()).location(userInfoDto.getLocation())
                    .authLogin(userInfoDto.isAuthLogin())
                    .build());
        }
    }

}
