package com.z.core.service.user;

import com.z.common.util.DateTimeUtil;
import com.z.dbes.service.EsUserLogService;
import com.z.model.es.EsUserLog;
import com.z.model.mysql.GUser;
import com.z.model.type.UserAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Log4j2
@Service
public class EsUserLogBizService {
    private static final Log log = LogFactory.getLog(EsUserLogBizService.class);

//    protected Logger log = LoggerFactory.getLogger(getClass());
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
