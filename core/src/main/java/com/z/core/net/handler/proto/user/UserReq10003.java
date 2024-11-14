package com.z.core.net.handler.proto.user;

import com.google.protobuf.AbstractMessageLite;
import com.z.core.net.handler.MsgData;
import com.z.core.net.handler.SuperHandler;
import com.z.core.service.UserBizService;
import com.z.model.proto.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户登录s
 */
@Log4j2
@Component
public class UserReq10003 extends SuperHandler {

    @Autowired
    private UserBizService service;

    @Override
    public AbstractMessageLite handle(MsgData msgData) {
        log.info("=========>"+msgData);
        User.UserMsg userMsg = msgData.getUserMsg();
        User.User_Req_10003 req10003 = userMsg.getRes10003();
        return service.login(req10003.getType(),req10003.getUid(),req10003.getPhone(), req10003.getPwd());
    }
}
