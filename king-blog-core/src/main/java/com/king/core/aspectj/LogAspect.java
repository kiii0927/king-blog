package com.king.core.aspectj;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 切入点 "com.example.controller下面的所有类所有方法（.. 可选参数()）"
     */
    @Pointcut("execution(public * com.king.controller.*.*(..))")
    public void pointcut() {}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        //获得request实例
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;
        HttpServletRequest request = attributes.getRequest();
        //获取ip
        String ip = request.getRemoteHost();

        //获取当前时间
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = dateTimeFormatter.format(LocalDateTime.now());

        //获取当前时间
        //String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获得方法路径及方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        // 获取请求参数
        Object[] args = joinPoint.getArgs();

        // 用户[ip],在[时间],访问了[com.example.service.xxx()].
        log.info(String.format("用户[%s],在[%s],访问了[%s],请求参数%s.", ip, date, target, Arrays.toString(args)));
    }
}
