package com.king.api.web.controller;

import com.king.common.module.domain.ResponseResult;
import com.king.common.validator.UpdateGroup;
import com.king.core.aspectj.preven.Prevent;
import com.king.sys.bean.dto.UserInfoDto;
import com.king.sys.bean.entity.system.UserInfo;
import com.king.sys.service.system.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *    用户信息表 前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@RestController
@RequestMapping("/v1/user-info")
public class UserInfoController {

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 获取用户信息
     * @param token 用户唯一标识
     */
    @GetMapping("/getUserinfo/{token}")
    public ResponseResult<Object> getUserInfo(@PathVariable String token){
        UserInfo userInfo =  userInfoService.getUserInfoByToken(token);
        return ResponseResult.success(userInfo);
    }

    /**
     * 修改用户信息
     * @param userInfoDto dto
     */
    @Prevent(message = "重复请求" ,value = "10")
    @PutMapping("/update")
    public ResponseResult<Object> update(@Validated(UpdateGroup.class) @RequestBody UserInfoDto userInfoDto){
        return ResponseResult.success(userInfoService.updateUserInfo(userInfoDto));
    }
}
