package com.king.api.web.controller.auth;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import com.king.common.module.domain.ResponseResult;
import com.king.sys.bean.entity.auth.AuthUser;
import com.king.sys.bean.properties.OAuthProperties;
import com.king.sys.service.auth.IAuthUserService;
import com.king.sys.service.auth.IRestAuthService;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *    第三方登录前端控制器
 * </p>
 *
 * @see <a href="https://github.com/justauth/JustAuth-demo/blob/master/src/main/java/me/zhyd/justauth/RestAuthController.java">官方推荐使用 JustAuth-demo</a>
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@RestController
@RequestMapping("/v1/oauth")
public class OAuthController {


    // 初始化 AuthRequest
    //private AuthRequest authRequest = null;

    // 注入第三方登录的配置
    @Autowired
    private OAuthProperties properties;

    // 注入第三方登录服务类
    @Autowired
    private List<IRestAuthService> restAuthServices;

    // 注入第三方授权服务类
    @Autowired
    private IAuthUserService authUserService;

    /**
     * 获取授权链接并跳转到第三方授权页面
     * @param type 登录类型
     * @return 统一结果集
     */
    @GetMapping("/render/{type}")
    public ResponseResult<Object> renderAuth(@PathVariable String type) {
        System.out.println("第三方登录>>>>{" + type + "}登录 ");

        // 获取 auth request
        AuthRequest authRequest = this.getAuthRequest(type);

        // 生成授权的url
        String token = AuthStateUtils.createState();
        String authorizeUrl = authRequest.authorize(token);

        //将这个url返回给前端Vue, 由Vue去执行 授权页
        return ResponseResult.success(MapUtil.builder()
                .put("url", authorizeUrl)
                .map());
    }

    /**
     * 用户在确认第三方平台授权（登录）后， 第三方平台会重定向到该地址，并携带code、state等参数
     *
     * @param callback 第三方回调时的入参
     * @param type 登录类型
     * @param response response
     */
    @GetMapping("/callback/{type}")
    @SuppressWarnings(value = { "unchecked" })
    public void login(AuthCallback callback, @PathVariable String type, HttpServletResponse response) throws IOException {

        AuthRequest authRequest = this.getAuthRequest(type);

        final JSONObject result = this.authUserService.login(authRequest.login(callback));

        System.out.println("result = " + result);

        // 重定向到前端
        //response.sendRedirect("http://localhost:9027/#/authlogin?result=" + result);
    }

    @GetMapping("/revoke/{source}/{uuid}")
    @SuppressWarnings(value = { "rawtypes" })
    public ResponseResult<Object> revokeAuth(@PathVariable("source")String source, @PathVariable("uuid") String uuid) {
        AuthUser authUser = this.authUserService.getAuthUser(uuid, source);
        Objects.requireNonNull(authUser, "注销失败，用户不存在");

        AuthRequest authRequest = this.getAuthRequest(source);

        AuthResponse response = authRequest.revoke(AuthToken.builder().accessToken(authUser.getToken()).build());

        if (response.ok()) {
            authUserService.remove(uuid, source);
            return ResponseResult.success("用户 [" + authUser.getUsername() + "] 的 授权状态 已收回！");
        }
        return ResponseResult.fail("用户 [" + authUser.getUsername() + "] 的 授权状态 收回失败！" + response.getMsg());
    }

    /*@GetMapping("/{id}")
    public ResponseResult<AuthUser> getAuthUser(@PathVariable String id){
        return ResponseResult.success(this.authUserService.getById(id));
    }*/


    /**
     * 根据具体的授权来源，获取授权请求工具类
     * @param source 授权来源
     * @return authRequest
     */
    public AuthRequest getAuthRequest(String source){
        // 通过流判断出是哪种登录方式
        IRestAuthService authService = this.restAuthServices.stream().filter(r -> r.isCurrentAuthLogin(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的登录类型"));
        return authService.authLogin(properties);
    }

}
