package com.king.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.king.common.module.constant.CacheConstant;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * caffeine 缓存 config
 *
 * @author king
 * @version 1.0
 * @since 2023-06-26
 **/
@Configuration
public class CaffeineConfig {

    @Bean
    public CacheManager caffeineCacheManager(){
        // 创建一个简单的缓存管理器
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // setter
        cacheManager.setCaches(getNames());
        return cacheManager;
    }

    private List<CaffeineCache> getNames() {
        /* 添加缓存器名称 */
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(new CaffeineCache(CacheConstant.CACHE_5SECS,
                Caffeine.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(5, TimeUnit.SECONDS).build()));
        caches.add(new CaffeineCache(CacheConstant.CACHE_10SECS,
                Caffeine.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(10, TimeUnit.SECONDS).build()));
        caches.add(new CaffeineCache(CacheConstant.CACHE_30SECS,
                Caffeine.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(30, TimeUnit.SECONDS).build()));
        caches.add(new CaffeineCache(CacheConstant.CACHE_60SECS,
                Caffeine.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(60, TimeUnit.SECONDS).build()));
        caches.add(new CaffeineCache(CacheConstant.CACHE_180SECS,
                Caffeine.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(180, TimeUnit.SECONDS).build()));
        return caches;
    }


}
