package com.msb.ibs.corp.cross.exchange.infrastracture.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${msb.ibs.corp.thread.core.pool.size}")
    private int corePoolSize;
    @Value("${msb.ibs.corp.thread.max.pool.size}")
    private int maxPoolSize;
    @Value("${msb.ibs.corp.thread.queue.capacity}")
    private int queueCapacity;

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("threadPoolTaskExecutor-");
        executor.initialize();
        return executor;
    }
}