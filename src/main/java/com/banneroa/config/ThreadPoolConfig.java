package com.banneroa.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author rjj
 * @date 2024/1/5 - 16:41
 */
@Configuration
@Slf4j
public class ThreadPoolConfig {

    private static final Integer CORE_POOL_SIZE = 17;
    private static final Integer MAXIMUM_POOL_SIZE = 50;
    private static final Integer KEEP_ALIVE_TIME = 500;
    private static final Integer QUEUE_SIZE = 1000;

    @Bean("taskThreadPool")
    public ExecutorService executorService(){
        log.info("开启线程池");
        AtomicInteger i = new AtomicInteger(1);
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                r->new Thread(r,"ThreadPool--"+i.getAndIncrement()),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
