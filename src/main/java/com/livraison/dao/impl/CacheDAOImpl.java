package com.livraison.dao.impl;

import com.livraison.dao.CacheDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class CacheDAOImpl implements CacheDAO {

    private static final String PREFIX = "statut:";
    private static final long TTL_MINUTES = 30;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void setStatut(String colisId, String statut) {
        redisTemplate.opsForValue().set(PREFIX + colisId, statut, TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public Optional<String> getStatut(String colisId) {
        String value = redisTemplate.opsForValue().get(PREFIX + colisId);
        return Optional.ofNullable(value);
    }

    @Override
    public void deleteStatut(String colisId) {
        redisTemplate.delete(PREFIX + colisId);
    }

    @Override
    public boolean exists(String colisId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + colisId));
    }
}
