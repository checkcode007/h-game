package com.z.core.schedule;

import com.z.core.service.game.aladdin.AladdinRoom;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时线程池
 * @author zcj
 * @version 0.0.1
 * @since 2024-08-30
 */
public class MySchedule extends ScheduledThreadPoolExecutor {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(MySchedule.class);

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
