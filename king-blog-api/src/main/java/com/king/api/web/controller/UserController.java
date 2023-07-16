package com.king.api.web.controller;

import com.king.common.module.domain.ResponseResult;
import com.king.common.validator.UpdateGroup;
import com.king.core.aspectj.preven.Prevent;
import com.king.core.aspectj.redisLimit.RedisLimit;
import com.king.sys.bean.dto.EmailDto;
import com.king.sys.bean.dto.LoginDto;
import com.king.sys.bean.dto.UserRegisterDto;
import com.king.sys.bean.dto.VerifyCodeDto;
import com.king.sys.bean.vo.UserVo;
import com.king.sys.service.system.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * <p>
 *   用户表 前端控制器
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-07-02
 **/
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private IUserService userService;

    //@Autowired
    //private IValidationService validationService;

    /**
     * 发送人
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮箱验证码
     * @param email 接收者
     */
    @CrossOrigin
    @Prevent(message = "请勿重复操作", value = "10")
    @GetMapping("/sendEmail/{email}")
    public ResponseResult<String> sendEmailCode(@PathVariable String email) {
        userService.sendEmailCode(email,from);

        return ResponseResult.success();
    }

    /**
     * 用户留言
     */
    @Prevent(message = "请勿重复操作", value = "10")
    @PostMapping("/leaveWord")
    public void leaveWord(@RequestBody @Validated EmailDto emailDto){
        userService.leaveWord(emailDto, from);
    }

    /**
     * 根据 token 获取用户信息
     * @param token 用户token
     */
    @CrossOrigin
    @GetMapping("getUserInfo")
    public UserVo getUserInfo(@RequestParam("token") String token){
        //return Result.success(userService.getUserInfoByTokenPro(token));
        return userService.getUserInfoByTokenPro(token);
    }

    /**
     * 用户注册
     * @param userRegisterDto 注册信息
     */
    @PostMapping("/register")
    @Prevent(message = "请勿重复请求" ,value = "10")
    @RedisLimit(key = "redis-limit:register", permitsPerSecond = 100, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    public ResponseResult<Object> register(@RequestBody @Validated UserRegisterDto userRegisterDto){
        return ResponseResult.success(userService.saveUser(userRegisterDto));
    }

    /**
     * 用户登录
     * @param loginDto 登录信息
     */
    @PostMapping("/login")
    @Prevent(message = "请勿重复请求" ,value = "10")
    @RedisLimit(key = "redis-limit:login", permitsPerSecond = 100, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    public ResponseResult<Object> login(@RequestBody @Validated LoginDto loginDto){
        //return Result.success(userService.login(loginDto));
        return ResponseResult.success(userService.login(loginDto));
    }

    /**
     * 是否登录
     */
    @GetMapping("/isLogin")
    public ResponseResult<Object> isLogin(@RequestParam(name = "jwt") String jwt){
        return ResponseResult.success(userService.isLogin(jwt));
    }

    /**
     * 获取用户唯一标识
     */
    @GetMapping("/getIdentify")
    public ResponseResult<String> getIdentify(){
        return ResponseResult.success(userService.addIdentify());
    }

    /**
     * 获取图片验证码
     */
    @GetMapping("/getImgCode")
    @RedisLimit(key = "redis-limit:getImgCode", permitsPerSecond = 100, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    public ResponseResult<Map<String, String>> getImgCode(){
        return ResponseResult.success(this.userService.getImgCode());
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    @Prevent(message = "请勿重复请求" ,value = "10")
    @RedisLimit(key = "redis-limit:resetPwd", permitsPerSecond = 100, expire = 1, msg = "当前排队人数较多，请稍后再试！")
    public String resetPwd(@RequestBody @Validated(UpdateGroup.class) VerifyCodeDto vcd){
        this.userService.resetPassword(vcd);
        return "ok";
    }

}
