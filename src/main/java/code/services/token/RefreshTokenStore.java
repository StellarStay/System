package code.services.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenStore {
    private final StringRedisTemplate redis;
    private final long refreshTtlMs;

    public RefreshTokenStore(StringRedisTemplate redis,
                             @Value("${app.jwt.refresh-ttl-ms}") long refreshTtlMs) {
        this.redis = redis;
        this.refreshTtlMs = refreshTtlMs;
    }

    private String key(String userId) {
        return "refresh:user:" + userId;
    }

    public void save(String userId, String jti) {
        redis.opsForValue().set(key(userId), jti, refreshTtlMs, TimeUnit.MILLISECONDS);
    }

    public String getJti(String userId) {
        return redis.opsForValue().get(key(userId));
    }

    public void delete(String userId) {
        redis.delete(key(userId));
    }

    public void extendTtl(String userId) {
        redis.expire(key(userId), refreshTtlMs, TimeUnit.MILLISECONDS);
    }
}
