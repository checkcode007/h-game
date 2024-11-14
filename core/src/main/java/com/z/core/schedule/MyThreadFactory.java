package com.z.core.schedule;


import java.util.concurrent.ThreadFactory;

/**
 * 线程工厂类
 * @author zcj
 * @version 0.0.1
 * @since 2024-08-30
 */
public class MyThreadFactory implements ThreadFactory {

    private String prefix;

    public MyThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    private static int count = 0;

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, prefix +"-"+count++);
    }
}
