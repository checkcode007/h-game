package com.z.core.net.handler.email;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.wallet.WalletBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 邮件列表
 */
@Service
public class EmalReceive implements IHandler<User.C_10303> {
    @Autowired
    WalletBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_EMAIL_RECEIVE;
    }

    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10303  req =  User.C_10303.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10303 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        return service.receiveMail(uid,req.getId());
    }
}
