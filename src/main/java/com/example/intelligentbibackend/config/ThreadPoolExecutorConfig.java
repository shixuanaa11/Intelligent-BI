package com.example.intelligentbibackend.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author :AXuan
 * 线程池配置类
 */
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadFactory threadFactory=new ThreadFactory() {
            private int count=1;
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread=new Thread(r);
                thread.setName("线程"+count);
                return thread;
            }
        };
        //下面这行代码分别是线程池默认2个，但是如果任务多（超过任务队列了），可以将线程池提到4个，线程持续时间100秒，任务队列默认4个，也就是说这里面可以执行的4个加上排队的4个，一共最多8个，超过8个再加报错
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(2,4,100, TimeUnit.SECONDS,new ArrayBlockingQueue<>(4),threadFactory);
        return threadPoolExecutor;
    }
}
