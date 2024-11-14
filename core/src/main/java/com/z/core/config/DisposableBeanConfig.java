package com.z.core.config;

import com.z.core.schedule.ScheduledManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

/**
 * 程序结束执行
 */
@Log4j2
@Component
public class DisposableBeanConfig implements DisposableBean, ExitCodeGenerator {

    @Autowired
    ScheduledManager scheduledManager;

    @Override
    public void destroy() throws Exception {
        scheduledManager.shutdown();
        log.info("========================程序结束============================");
    }

    @Override
    public int getExitCode() {
        return 5;
    }
}
