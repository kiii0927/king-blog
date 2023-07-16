package com.king.core.web.interceptor;

import com.king.common.utils.CookieUtil;
import com.king.common.utils.JwtUtil;
import com.king.sys.assets.HostHolder;
import com.king.sys.bean.vo.UserVo;
import com.king.sys.service.system.IUserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * <p>
 *    登录入场卷拦截器<br />
 *    需要注意,注入的组件会为null。<a href='https://blog.csdn.net/dling8/article/details/91423813/'>点查看</a>
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 用于在将请求发送到控制器之前执行操作。
     * 此方法应返回true，以将响应返回给客户端。
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        //查询 ticket(jwt)
        String ticket = CookieUtil.getValue(request, "kblog-JWT");
        //System.out.println("ticket = " + ticket);
        Optional.ofNullable(ticket)
                .ifPresent(t -> {
                    //查询凭证
                    // 检查凭证是否有效
                    boolean verify = JwtUtil.verifyToken(t);
                    if (verify) {
                        //验证成功, 解析凭证
                        String v = JwtUtil.parseToken(t);
                        UserVo userVo = userService.getUserInfoByTokenPro(v);
                        Optional.ofNullable(userVo)
                                .ifPresent(vo -> {
                                    //在本次请求中持有用户
                                    hostHolder.setUser(vo);
                                });
                    }
                });
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 用于在将响应发送到客户端之前执行操作。
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 用于在完成请求和响应后执行操作。
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        Optional.ofNullable(hostHolder)
                .ifPresent(HostHolder::clear);
    }

}
