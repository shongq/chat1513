package com.example.prj1513.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    StockService stockService;

    @BeforeEach
    void init() {
        final String key = "peanut";
        final int amount = 100;

        stockService.setStock(key, amount);
    }

    /*@Test
    @DisplayName("재고 세팅 후 재고 조회")
    void setStockAndCurrentStock() {
        //given
        String key = "홍큐나마타타";
        int stock = 100;

        //when
        stockService.setStock(key, stock);
        Integer currentStock = stockService.currentStock(key).intValue();

        //then
        assertThat(currentStock).isEqualTo(100);
    }*/

    @Test
    @Order(1)
    @DisplayName("상품 수량 확인")
    void getAmount() {
        //given
        final int stock = 100;
        final String key = "peanut";

        //when
        Integer currentStock = stockService.currentStock(key);

        //then
        assertThat(currentStock).isEqualTo(stock);
    }

    @Test
    @Order(2)
    @DisplayName("상품 재고 감소")
    void decreaseAmount(){
        //given
        final int amount = 100;
        final int decreaseCount = 2;
        final String key = "peanut";

        //when
        stockService.decrease(key, decreaseCount);
        Integer currentStock = stockService.currentStock(key);

        //then
        assertThat(currentStock).isEqualTo(amount-decreaseCount);
    }

    @Test
    @Order(3)
    @DisplayName("분산 락 테스트")
    void redissonLockTest() throws InterruptedException {
        //given
        final int userCount = 100;
        final int decreaseCount = 2;
        final int soldOutCount = 0;
        final String key = "peanut";
        final CountDownLatch countDownLatch = new CountDownLatch(userCount);

        //when
        List<Thread> workers = Stream
                .generate(() -> new Thread(new BuyWoker(key, decreaseCount, countDownLatch)))
                .limit(userCount)
                .collect(Collectors.toList());
        workers.forEach(Thread::start);
        countDownLatch.await();

        //then
        final int currentCount = stockService.currentStock(key);
        assertThat(currentCount).isEqualTo(soldOutCount);
    }

    private class BuyWoker implements Runnable {
        private String stockKey;
        private int count;
        private CountDownLatch countDownLatch;

        public BuyWoker(String stockKey, int count, CountDownLatch countDownLatch) {
            this.stockKey = stockKey;
            this.count = count;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            stockService.decrease(this.stockKey, count);
            countDownLatch.countDown();
        }
    }
}