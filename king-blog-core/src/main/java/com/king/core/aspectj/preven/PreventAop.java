package com.king.core.aspectj.preven;

import com.king.common.exception.WrongInputException;
import com.king.common.module.domain.ResponseCode;
import com.king.common.utils.RedisCacheUtil;
import com.king.common.utils.ToolUtil;
import me.zhyd.oauth.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * <p>
 *    防刷切面实现类
 * </p>
 *
 * @author king
 * @since 2023-02-01
 **/
@Aspect
@Component
public class PreventAop {

    private static final Logger log = LoggerFactory.getLogger(PreventAop.class);

    @Autowired
    private RedisCacheUtil redisUtil;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.king.core.aspectj.preven.Prevent)")
    public void pointcut() {}

    /**
     * 处理前
     * @param joinPoint {@link JoinPoint}
     */
    @Before("pointcut()")
    public void joinPoint(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("接口防刷...");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName(),
                methodSignature.getParameterTypes());
        // 获取 prevent注解
        Prevent preventAnnotation = method.getAnnotation(Prevent.class);
        String methodFullName = method.getDeclaringClass().getName() + method.getName();

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        String identify = request.getHeader("identify");
        entrance(preventAnnotation, identify, methodFullName);
    }

    /**
     * 入口
     * @param prevent {@link Prevent}
     * @param key key
     * @param methodFullName 目标方法
     * @throws UnsupportedEncodingException
     */
    private void entrance(Prevent prevent, String key,String methodFullName) {
        Optional.ofNullable(key).orElseThrow(() -> {
            log.info("缺少身份验证参数(jwt). identify can't be null");
            return new IllegalArgumentException("操作失败");
        });
        PreventStrategy strategy = prevent.strategy();
        if (strategy == PreventStrategy.DEFAULT) {
            defaultHandle(key, prevent, methodFullName);
        } else {
            throw new WrongInputException(ResponseCode.OTHER_FAIL.getCode(), "无效的策略");
        }
    }

    /**
     * 默认处理方式
     * @param prevent {@link Prevent}
     * @param key key
     * @param methodFullName 目标方法
     */
    private void defaultHandle(String key, Prevent prevent, String methodFullName){
        String base64Str = ToolUtil.toBase64String(methodFullName);
        long expire = Long.parseLong(prevent.value());
        String resp = redisUtil.get(key);
        if (StringUtils.isEmpty(resp)) {
            redisUtil.set(key, base64Str, expire);
        } else {
            //String message = expire + "s" + prevent.message();
            String message =  prevent.message();
            throw new WrongInputException(ResponseCode.OTHER_FAIL.getCode(), message);
        }
    }

}
