package com.king.core.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充 config
 *
 * @author king
 * @version 1.0
 * @since 2023-06-26
 **/
@Slf4j
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName("token", String.valueOf(IdUtil.getSnowflakeNextId()), metaObject);
        this.setFieldValByName("identify", IdUtil.simpleUUID(), metaObject); // uuid
        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("updateTime", now, metaObject);
        this.setFieldValByName("loginTime", now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName("updateTime", now, metaObject);
        this.setFieldValByName("loginTime", now, metaObject);
    }
}
