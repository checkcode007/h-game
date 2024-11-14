package com.z.core.schedule;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时线程池
 * @author zcj
 * @version 0.0.1
 * @since 2024-08-30
 */
@Log4j2
public class MySchedule extends ScheduledThreadPoolExecutor {

    //关闭等待时间（分钟）
    public static final int TIME_SHUTDOWN = 3;

    public MySchedule(int corePoolSize) {
        super(corePoolSize);
    }

    public MySchedule(int corePoolSize, String prefix,Runnable command, long initialDelay, long delay,TimeUnit timeUnit) {
        super(corePoolSize, new MyThreadFactory(prefix));
        init();
        start(command, initialDelay, delay,timeUnit);
    }

    public void init(){
        setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    public void start(Runnable command, long initialDelay, long delay,TimeUnit timeUnit){
        scheduleWithFixedDelay(command, initialDelay, delay,timeUnit);
    }

    public void shutdown(){
        super.shutdown();
        try {
            if (!awaitTermination(TIME_SHUTDOWN, TimeUnit.MINUTES)) {
                shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("shutdown force",e);
        }
    }
}
