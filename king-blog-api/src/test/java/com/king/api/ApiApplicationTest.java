package com.king.api;

import com.king.common.module.constant.Constant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiApplicationTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void test01(){
        System.out.println("redisTemplate = " + redisTemplate);
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        System.out.println("stringRedisTemplate = " + stringRedisTemplate);
    }

    @Test
    void test02(){
        //try {
        //    String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        //    System.out.println("path = " + path);
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}

        //File file = new File(Constant.DEFAULT_AVATAR_UPLOAD_PATH);
        //if (!file.exists()) {
        //    boolean mkdirs = file.mkdirs();
        //    System.out.println("mkdirs = " + mkdirs);
        //}
    }
}
