package com.king.api.web.controller;

import com.king.common.module.domain.ResponseCode;
import com.king.common.module.domain.ResponseResult;
import com.king.common.validator.ValidatorGroup;
import com.king.sys.bean.dto.VerifyCodeDto;
import com.king.sys.service.system.IUserInfoService;
import com.king.sys.service.system.IValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *    前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-07
 **/
@RestController
@RequestMapping("/v1/validation")
public class ValidationController {

    @Autowired
    private IValidationService validationService;

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 监测邮箱是否存在
     * @param email 邮箱
     * @return r
     */
    @GetMapping("/email/{email}")
    public ResponseResult<String> validationEmail(@PathVariable String email){
        boolean isExist = validationService.validationEmailIsExist(email);
        return !isExist ? ResponseResult.success() :ResponseResult.fail(ResponseCode.ERROR.getCode(),"该邮箱已存在！");
    }

    /**
     * 检测用户是否存在
     * @param username 用户名
     * @return res
     */
    @GetMapping("/user")
    public ResponseResult<Object> validationUser(@RequestParam(name = "username")String username){
        boolean isExist = validationService.validationUserIsExist(username);
        return !isExist ? ResponseResult.success() : ResponseResult.fail(ResponseCode.ERROR.getCode(),"该用户已存在！");
    }

    /**
     * 校验qq是否存在
     * @param tencentQQ qq
     * @return res
     */
    @GetMapping("/validQQ")
    public ResponseResult<String> validTencentQQ(@RequestParam("qq") String tencentQQ){
        userInfoService.verifyTencentQQIsExist(tencentQQ);
        return  ResponseResult.success();
    }

    /**
     * 校验手机号码是否存在
     * @param phone
     * @return
     */
    @GetMapping("/validPhone")
    public ResponseResult<String> validPhone(@RequestParam("phone") String phone){
        userInfoService.verifyPhoneIsExist(phone);
        return ResponseResult.success();
    }

    /**
     * 校验邮箱验证码是否合法
     * @param VerifyCodeDto {@link VerifyCodeDto}
     */
    @PostMapping("/emailCode")
    public ResponseResult<String> validEmailCode(@RequestBody @Validated(ValidatorGroup.class) VerifyCodeDto VerifyCodeDto){
        boolean isExist = validationService.validationEmailCode(VerifyCodeDto);
        if (!isExist) return ResponseResult.fail(ResponseCode.ERROR.getCode(), "注册失败，验证码过期！");
        return ResponseResult.success();
    }


    /**
     * 校验图片验证码
     */
    @GetMapping("/imgCode")
    protected ResponseResult<Object> validImgCode(@RequestParam(name = "email") String email){
        boolean isExist = validationService.validationEmailIsExist(email);
        return isExist ? ResponseResult.success() : ResponseResult.fail(ResponseCode.ERROR.getCode(),"该邮箱未注册过");
    }

}
