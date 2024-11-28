package com.z.core.net.handler.user;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.user.UserBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 绑定手机
 */
@Component
public class BindPhone implements IHandler<User.C_10009> {

    private static final Logger log = LogManager.getLogger(BindPhone.class);
    @Autowired
    private UserBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_PHONE;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10009  req =  User.C_10009.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10009 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        return service.bindPhone(uid,req.getPhone());
    }
}
