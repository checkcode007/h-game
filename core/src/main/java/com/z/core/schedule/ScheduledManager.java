package com.z.core.schedule;

import cn.hutool.core.thread.ThreadUtil;
import com.z.core.service.game.card.CardService;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component
public class ScheduledManager {
    public List<MySchedule> scheduleList = new ArrayList<>();
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CardService cardService;

    private ScheduledManager(){
        MySchedule schedule = new MySchedule(1,"common",this::exe,20,10, TimeUnit.SECONDS);
        scheduleList.add(schedule);
        schedule = new MySchedule(1,"card",this::exe1,25,5, TimeUnit.SECONDS);
        scheduleList.add(schedule);
        schedule = new MySchedule(1,"mali",this::exe2,10,1, TimeUnit.SECONDS);
        scheduleList.add(schedule);

    }

    public void exe(){
//        log.info("exe=>"+Thread.currentThread().getId()+" :"+Thread.currentThread().getName()+" :"+this.hashCode());
    }

    public void exe1(){
//        log.info(+Thread.currentThread().getId()+" :"+Thread.currentThread().getName()+" :"+this.hashCode());
//        cardService.exe();
    }

    public void exe2(){
//        log.info(Thread.currentThread().getId()+" :"+Thread.currentThread().getName()+" :"+this.hashCode());
        try {
            UserService.ins.exe();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        try {
            WalletService.ins.exe();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public void shutdown(){
        log.info("shutdown1");
        for (MySchedule schedule : scheduleList) {
            try {
                schedule.shutdown();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
        try {
            UserService.ins.shutDown();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        try {
            WalletService.ins.shutDown();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        log.info("shutdown2");
    }

    public static void exeee(){
//        log.info("exe=>"+Thread.currentThread().getId()+" :"+Thread.currentThread().getName());

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++){
            MySchedule schedule = new MySchedule(1,"common",()->exeee(),0,3, TimeUnit.SECONDS);
            System.out.println(i);
        }
        ThreadUtil.safeSleep(5000);
    }
}
