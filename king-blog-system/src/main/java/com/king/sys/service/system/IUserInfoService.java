package com.king.sys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.dto.UserInfoDto;
import com.king.sys.bean.entity.system.UserInfo;
import lombok.NonNull;

/**
 * <p>
 *    用户信息服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 校验qq号码是否存在
     * @param tencentQQ qq号码
     */
    void verifyTencentQQIsExist(String tencentQQ);

    /**
     * 获取用户信息
     * @param token 用户唯一标识 包括 user表, AuthUser表
     * @return {@link UserInfo}
     */
    UserInfo getUserInfoByToken(@NonNull String token);

    /**
     * 获取用户性别
     * @param token 用户唯一标识
     * @return
     */
    Integer getUserGender(String token);

    /**
     * 校验用户手机号码是否存在
     * @param phone 手机号
     */
    void verifyPhoneIsExist(String phone);

    /**
     * 更新用户数据 User表
     * @param userInfoDto {@link UserInfoDto}
     * @return {@link UserInfo}
     */
    UserInfo updateUserInfo(UserInfoDto userInfoDto);

    /**
     * 保存用户
     * @param userInfoDto {@link UserInfoDto}
     */
    void save(UserInfoDto userInfoDto);

    /**
     * 校验用户是否存在
     * @param token 用户唯一标识
     * @return
     */
    boolean isExist(String token);
}
