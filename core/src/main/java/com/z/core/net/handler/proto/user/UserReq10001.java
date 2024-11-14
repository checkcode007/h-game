package com.z.core.net.handler.proto.user;

import com.google.protobuf.AbstractMessageLite;
import com.z.core.net.handler.MsgData;
import com.z.core.net.handler.SuperHandler;
import com.z.core.service.UserBizService;
import com.z.model.proto.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户登录
 */
@Service
public class UserReq10001 extends SuperHandler {

    private static final Logger log = LogManager.getLogger(UserReq10001.class);
    @Autowired
    private UserBizService service;
    @Override
    public AbstractMessageLite handle(MsgData msgData) {
        log.info("=========>"+msgData);
        User.UserMsg userMsg = msgData.getUserMsg();
        User.User_Req_10001 req10001 = userMsg.getReq10001();
        return service.reg(req10001.getPhone(),req10001.getPwd());
    }
}
