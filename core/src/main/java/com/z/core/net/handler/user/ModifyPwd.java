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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 修改密码
 */
@Component
public class ModifyPwd implements IHandler<User.C_10007> {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_PWD;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10007  req =  User.C_10007.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10007 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        return service.modifyPwd(uid,req.getPwd());
    }

}
