package com.king.sys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.module.domain.ResponseCode;
import com.king.sys.bean.dto.UserInfoDto;
import com.king.sys.bean.factory.UserInfoFactory;
import com.king.sys.bean.entity.system.UserInfo;
import com.king.sys.mapper.UserInfoMapper;
import com.king.sys.service.system.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * <p>
 *    用户信息表 服务实现类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 校验qq是否存在
     * @param tencentQQ qq
     */
    @Override
    public void verifyTencentQQIsExist(String tencentQQ) {
        // 创建 wrapper
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getTencentQQ, tencentQQ);

        boolean exists = userInfoMapper.exists(wrapper);
        if(exists) throw new ServiceException(ResponseCode.ERROR.getCode(),"该QQ号码已存在！");
    }

    /**
     * 校验手机号码是否存在
     * @param phone 手机号码
     */
    @Override
    public void verifyPhoneIsExist(@NonNull String phone) {
        // 创建 wrapper
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, phone);

        boolean exists = userInfoMapper.exists(wrapper);
        if(exists) throw new ServiceException(ResponseCode.ERROR.getCode(),"该手机号码已存在！");
    }

    /**
     * 修改用户信息
     * @param userInfoDto dto
     * @return {@link UserInfo}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo updateUserInfo(@NonNull UserInfoDto userInfoDto) {
        // 创建 wrapper
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfo::getId, Long.parseLong(userInfoDto.getId()));

        // builder 一个 userInfo 对象
        UserInfo userInfo = UserInfoFactory.getUserInfo(userInfoDto);

        // 执行更新
        int update = userInfoMapper.update(userInfo, updateWrapper);
        if (update <= 0) {
            throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
        }

        // 返回更新的数据
        return userInfoMapper.selectById(userInfoDto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(@NonNull UserInfoDto userInfoDto) {
        String token = userInfoDto.getToken();
        if (!this.isExist(token)) {
            int insert = userInfoMapper.insert(UserInfoFactory.getUserInfo(userInfoDto));

            if (insert <= 0) {
                throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
            }
        }
    }

    @Override
    public boolean isExist(@NonNull String token) {
        return this.userInfoMapper.exists(new LambdaUpdateWrapper<UserInfo>()
                .eq(UserInfo::getIdentify, token));
    }

    @Override
    public UserInfo getUserInfoByToken(@NonNull String token) {
        return Optional.ofNullable(this.userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getIdentify, token)))
                .orElseThrow(() -> new IllegalArgumentException("获取失败，该用户不存在"));
    }

    /**
     * 根据用户ID获取用户性别
     * @param token 用户唯一标识
     * @return int
     */
    @Override
    public Integer getUserGender(@NonNull String token) {
        return this.userInfoMapper.findGenderByToken(token);
    }
}
