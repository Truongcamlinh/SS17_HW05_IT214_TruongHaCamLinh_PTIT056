package com.grabfood.menu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabfood.menu.entity.Menu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig implements CachingConfigurer {
    private final RedisConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public CacheConfig(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper,
                       @Value("${menu.cache-ttl:5m}") Duration ttl) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
        this.ttl = ttl;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        Jackson2JsonRedisSerializer<Menu> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Menu.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .build();
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
}
