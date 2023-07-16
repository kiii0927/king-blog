package com.king.core.aspectj.redisLimit;

import com.google.common.base.Strings;
import com.king.common.exception.RedisLimitException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * redis接口限流aop
 *
 * @author k
 * @since 2023-03-12
 **/
@Slf4j
@Aspect
@Component
public class RedisLimitAop {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.king.core.aspectj.redisLimit.RedisLimit)")
    private void check() {}

    /**
     * 默认redis脚本
     */
    private DefaultRedisScript<Long> redisScript;


    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/rateLimiter.lua")));
    }

    @Before("check()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //拿到RedisLimit注解，如果存在则说明需要限流
        //RedisLimit redisLimit = method.getAnnotation(RedisLimit.class);

        Optional.of(method.getAnnotation(RedisLimit.class))
                .ifPresent(v -> {
                    //获取redis的key
                    String key = v.key();
                    String className = method.getDeclaringClass().getName();
                    String name = method.getName();

                    String limitKey = key + className + name;

                    log.info(limitKey);
                    if (Strings.isNullOrEmpty(key)) {
                        throw new RedisLimitException("key cannot be null");
                    }

                    long limit = v.permitsPerSecond();
                    long expire = v.expire();

                    //Tuple1<String> tuple1 = Tuple.of(key);
                    List<String> keys = new ArrayList<>();
                    keys.add(key);

                    Long count = stringRedisTemplate.execute(redisScript, keys, String.valueOf(limit), String.valueOf(expire));

                    log.info("Access try count is {} second for key={}", count, key);

                    if (count != null && count == 0) {
                        log.error("获取key失败，key为{}", key);
                        throw new RedisLimitException(v.msg());
                    }
                });
    }
}
