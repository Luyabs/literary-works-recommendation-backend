package edu.shu.abs.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;


/**
 * 通过使用 @Cachable 来自动缓存 <br>
 * e.g. @Cacheable(cacheNames = "{getWorkPage}")
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // enableDefaultTyping 方法已经过时，使用新的方法activateDefaultTyping
        // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // String序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 序列化方式
        template.setKeySerializer(stringRedisSerializer);

        // hash的key序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        // value的序列化方式（json序列化）
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // value的序列化方式（String序列化，value 不是String类型会报错，建议使用json序列化）
        // template.setValueSerializer(stringRedisSerializer);

        // hash的value序列化方式（json序列化）
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 自定义key生成策略
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            Cacheable annotation = method.getAnnotation(Cacheable.class);
            String[] cacheNames = annotation.cacheNames();
            for (String elem : cacheNames) {
                sb.append(elem).append(".");
            }
            if (cacheNames.length >= 1)
                sb.deleteCharAt(sb.length() - 1); // 删除掉 cacheNames 中的最后一个点(.)

            sb.append("::").append(target.getClass().getSimpleName())
                    .append("::").append(method.getName())
                    .append("::").append(Arrays.toString(objects));

            return sb.toString();
        };
    }
}
