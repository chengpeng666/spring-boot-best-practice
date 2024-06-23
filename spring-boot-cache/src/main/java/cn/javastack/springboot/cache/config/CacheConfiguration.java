package cn.javastack.springboot.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号：Java技术栈
 */
@Configuration
public class CacheConfiguration {

    /**
     * 优先配置文件中的配置
     * @return
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("calc", defaultCacheConfig(55))
                .withCacheConfiguration("test", defaultCacheConfig(15));

    }

    /**
     * 默认配置中进行了序列化的配置
     * @param second
     * @return
     */
    private RedisCacheConfiguration defaultCacheConfig(Integer second) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //配置序列化 解决乱码的问题
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(second))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        return config;

    }

    /**
     * 针对不同的redis的key 有不同的过期时间
     * @return
     */
    private Map<String,RedisCacheConfiguration> initCacheConfigMap() {

        Map<String,RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("User",this.defaultCacheConfig(1000));
        configMap.put("User1",this.defaultCacheConfig(1000));
        configMap.put("User2",this.defaultCacheConfig(1000));
        configMap.put("User3",this.defaultCacheConfig(1000));
        return configMap;
    }
}