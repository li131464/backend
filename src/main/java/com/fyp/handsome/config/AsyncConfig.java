package com.fyp.handsome.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步任务配置类
 * 配置视频分析专用的线程池
 * @author ziye
 */
@Slf4j
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 默认异步任务执行器
     * 用于一般的异步任务
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：基础线程数量
        executor.setCorePoolSize(5);
        // 最大线程数：当队列满时，扩展到的最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量：等待队列的大小
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("handsome-async-");
        // 空闲线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：当线程池达到最大容量时，由调用者线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 初始化
        executor.initialize();
        
        log.info("默认异步任务执行器初始化完成，核心线程数：{}，最大线程数：{}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        
        return executor;
    }

    /**
     * 视频分析专用异步任务执行器
     * 用于视频分析、轮询等长时间运行的任务
     */
    @Bean("videoAnalysisExecutor")
    public Executor videoAnalysisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 视频分析任务通常较少但耗时较长，设置较小的核心线程数
        executor.setCorePoolSize(3);
        // 最大线程数
        executor.setMaxPoolSize(8);
        // 队列容量：设置较大的队列来缓冲任务
        executor.setQueueCapacity(100);
        // 线程名前缀
        executor.setThreadNamePrefix("video-analysis-");
        // 空闲线程存活时间（秒）
        executor.setKeepAliveSeconds(300);
        // 拒绝策略：当线程池达到最大容量时，由调用者线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间（秒）- 视频分析可能需要更长时间
        executor.setAwaitTerminationSeconds(300);
        // 初始化
        executor.initialize();
        
        log.info("视频分析异步任务执行器初始化完成，核心线程数：{}，最大线程数：{}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        
        return executor;
    }
} 