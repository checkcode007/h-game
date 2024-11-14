package com.z.core.schedule;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时线程池管理类
 * @author zcj
 * @version 1.0.0、
 * @since 2024-08-30
 */
@Log4j2
@Component
public class ScheduledManager {
    public List<MySchedule> scheduleList = new ArrayList<>();

    private ScheduledManager(){
        MySchedule schedule = new MySchedule(1,"common",this::exe,20,10, TimeUnit.SECONDS);
        scheduleList.add(schedule);
        schedule = new MySchedule(1,"ranking",this::exe1,25,5, TimeUnit.SECONDS);
        scheduleList.add(schedule);

    }

    public void exe(){
        log.info("exe=>"+Thread.currentThread().getId()+" :"+Thread.currentThread().getName()+" :"+this.hashCode());

    }

    public void exe1(){
        log.info(+Thread.currentThread().getId()+" :"+Thread.currentThread().getName()+" :"+this.hashCode());
    }

    public void exe2(){
        log.info(Thread.currentThread().getId()+" :"+Thread.currentThread().getName()+" :"+this.hashCode());
    }

    public void shutdown(){
        log.info("shutdown1");
        for (MySchedule schedule : scheduleList) {
            schedule.shutdown();
        }

        log.info("shutdown2");
    }

    public static void exeee(){
        log.info("exe=>"+Thread.currentThread().getId()+" :"+Thread.currentThread().getName());

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++){
            MySchedule schedule = new MySchedule(1,"common",()->exeee(),0,3, TimeUnit.SECONDS);
            System.out.println(i);
        }
        ThreadUtil.safeSleep(5000);
    }
}
