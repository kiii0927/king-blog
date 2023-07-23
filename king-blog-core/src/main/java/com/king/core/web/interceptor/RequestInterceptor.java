package com.king.core.web.interceptor;

import com.king.common.exception.RequestException;
import com.king.common.utils.CookieUtil;
import com.king.common.utils.JwtUtil;
import com.king.common.utils.ToolUtil;
import com.king.sys.assets.HostHolder;
import com.king.sys.bean.vo.UserVo;
import com.king.sys.service.system.IUserService;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 *    请求拦截器<br />
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {

    /**
     * 在进入这个拦截器之前, 对跨域提供支持
     * @param response
     * @param request
     * @return boolean
     */
    private boolean responseCors(HttpServletResponse response, HttpServletRequest request) {
        if (RequestMethod.OPTIONS.name().equals(request.getMethod())) {
            // response.setHeader("Cache-Control","no-cache");
            response.setHeader("Access-control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
            response.setHeader("Access-Control-Allow-Headers", "*");
            // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
            response.setStatus(HttpStatus.OK.value());
            return true;
        }
        return false;
    }

    /**
     * 用于在将请求发送到控制器之前执行操作。
     * 此方法应返回true，以将响应返回给客户端。
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 在进入这个拦截器之前，对跨域提供支持
        if (responseCors(response, request)) {
            return false;
        }

        //查询 ticket(jwt)
        String ticket = CookieUtil.getValue(request, "kblog-JWT");

        if (ticket == null) {
            ticket = ToolUtil.getIdentify(request, "identify");
        }

        if (ticket == null) {
            log.error("identify be null. request identify undefined.");
            throw new RequestException("请求失败");
        }

        /*
         * 小于32位的是临时identify(uuid), 大于32的是jwt
         * 临时的不需要校验是否合法
         */
        if (ticket.length() > 32) {
            boolean verify = JwtUtil.verifyToken(ticket);
            if (!verify) {
                throw new RequestException("请求失败，身份凭证已失效！");
            }
        }

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
    @SneakyThrows
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
