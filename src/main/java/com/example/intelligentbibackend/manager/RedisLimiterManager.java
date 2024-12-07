package com.example.intelligentbibackend.manager;

import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.config.RedissionConfig;
import com.example.intelligentbibackend.exception.BesinessException;
import jakarta.annotation.Resource;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * @author axuan
 * 通用限流器工具类
 * 限制对某个资源的访问频率，防止资源被过度使用。例如，可以用于限制API的调用频率，防止DDoS攻击，或者限制数据库查询的频率
 */
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    public void doRateLimiter(String key) {
        RRateLimiter limiter = redissonClient.getRateLimiter(key);
// Initialization required only once.
// 5 permits per 2 seconds
        //1秒2次
        limiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

// acquire 3 permits or block until they became available
        boolean canOP = limiter.tryAcquire(1);
        if (!canOP){
            throw new BesinessException(ErrorCode.TOO_MANY_REQUEST);
        }

    }

}
