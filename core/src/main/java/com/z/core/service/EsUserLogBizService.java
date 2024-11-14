package com.z.core.service;

import com.z.common.util.DateTimeUtil;
import com.z.dbes.service.EsUserLogService;
import com.z.model.es.EsUserLog;
import com.z.model.mysql.GUser;
import com.z.model.type.UserAction;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class EsUserLogBizService {
    @Autowired
    EsUserLogService service;

    public void reg(GUser user) {
        DateTime now = DateTime.now();
        EsUserLog esUserLog = new EsUserLog();
        esUserLog.setId(user.getId());
        esUserLog.setPhone(user.getPhone());
        esUserLog.setT(now.getMillis());
        esUserLog.setAction(UserAction.REG.k);
        esUserLog.setDay(DateTimeUtil.getDateShortInt(now));
        esUserLog.setD(DateTimeUtil.format(now));
        esUserLog.setName(user.getName());
        esUserLog.setRobot(user.getRobot());
        esUserLog.setDeviceId(user.getDeviceId());
        esUserLog.setIp(user.getIp());
        service.add(String.valueOf(user.getId()),esUserLog);
    }
    public void login(GUser user) {
        DateTime now = DateTime.now();
        EsUserLog esUserLog = new EsUserLog();
        esUserLog.setId(user.getId());
        esUserLog.setPhone(user.getPhone());
        esUserLog.setT(now.getMillis());
        esUserLog.setAction(UserAction.LOGIN.k);
        esUserLog.setDay(DateTimeUtil.getDateShortInt(now));
        esUserLog.setD(DateTimeUtil.format(now));
        esUserLog.setName(user.getName());
        esUserLog.setRobot(user.getRobot());
        esUserLog.setDeviceId(user.getDeviceId());
        esUserLog.setIp(user.getIp());
        service.add(String.valueOf(user.getId()),esUserLog);
    }
}
