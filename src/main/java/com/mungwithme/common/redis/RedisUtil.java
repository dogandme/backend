package com.mungwithme.common.redis;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /**
     * 지정된 키(key)에 해당하는 데이터를 Redis에서 가져오는 메서드
     */
    public String getData(String key){
        try {
            ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return null;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return null;
        }

    }

    /**
     * 지정된 키(key)에 값을 저장하는 메서드
     */
    public void setData(String key,String value){
        ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
        valueOperations.set(key,value);
    }

    /**
     * 지정된 키(key)에 값을 저장하고, 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
     */
    public void setDataExpire(String key,String value,long duration){
        ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
        Duration expireDuration= Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);
    }

    /**
     * 지정된 키(key)에 해당하는 데이터를 Redis에서 삭제하는 메서드
     */
    public void deleteData(String key){
        redisTemplate.delete(key);
    }

    public Boolean hasRedis(String key) {
        try {
            if (!StringUtils.hasText(key)) {
                return false;
            }
            return redisTemplate.hasKey(key);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
            return false;
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
            return false;
        }
    }

    public void removeAll(List<String> keys) {
        try {

            redisTemplate.delete(keys);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed", e);
        } catch (Exception e) {
            log.error("Error saving data to Redis", e);
        }
    }
}
