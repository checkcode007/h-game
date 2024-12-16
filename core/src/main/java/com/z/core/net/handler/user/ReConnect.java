package com.z.core.net.handler.user;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.handler.IHandler;
import com.z.core.service.user.UserBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 重连
 */
@Component
public class ReConnect implements IHandler<User.C_10011> {

    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
//    private static final Logger log = LogManager.getLogger(Login.class);
    @Autowired
    private UserBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_RECONNECT;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10011  req =  User.C_10011.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10011 req) {
        return service.reconnect(ctx,req.getUid(),req.getPwd(),req.getGameType(),req.getRoomType());
    }

}
