package co.ohmygoods.global.config;

import co.ohmygoods.global.idempotency.entity.Idempotency;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory())
                .cacheDefaults(redisCacheConfig)
                .build();
    }

    @Bean
    public RedisTemplate<String, Idempotency> idempotencyRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Idempotency> irt = new RedisTemplate<>();
        irt.setConnectionFactory(connectionFactory);
        irt.setKeySerializer(new StringRedisSerializer());
        irt.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return irt;
    }
}
