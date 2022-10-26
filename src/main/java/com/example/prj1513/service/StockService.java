package com.example.prj1513.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


/**
 * 동시성 제어를 위한 stockService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final RedissonClient redissonClient;
    private final RedisTemplate redisTemplate;

    public void decrease(final String key, final int count) {
        final String lockName = key + ":lock";
        final RLock lock = redissonClient.getLock(lockName);
        final String worker = Thread.currentThread().getName();

        try {
            if(!lock.tryLock(3, 3, TimeUnit.SECONDS))
                return;

            final int stock = currentStock(key);
            if(stock <= 0) {
                log.info("[{}] 현재 남은 재고가 없습니다. ({}개)", worker, stock);
                return;
            }

            log.info("현재 진행중인 사람 : {} & 현재 남은 재고 : {}개", worker, stock);

            setStock(key, stock-count);
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        } finally {
            if(lock!=null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    public Integer currentStock(String key) {
        Object redisData = redisTemplate.opsForValue().get("stock:" + key);

        return Integer.parseInt(redisData.toString());
    }

    public void setStock(String key, Integer stock) {
        redisTemplate.opsForValue().set("stock:" + key, stock, Duration.ofDays(1));
    }
}

