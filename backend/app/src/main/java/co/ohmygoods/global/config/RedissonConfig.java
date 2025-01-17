package co.ohmygoods.global.config;

import co.ohmygoods.global.idempotency.vo.Idempotency;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisProperties properties;

    private static final String REDISSON_SERVER_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_SERVER_PREFIX + properties.getHost() + ":" + properties.getPort());
        return Redisson.create(config);
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
