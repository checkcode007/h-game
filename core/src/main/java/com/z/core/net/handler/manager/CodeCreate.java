package com.z.core.net.handler.manager;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.user.CodeBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 点卡生成-生成
 */
@Component
public class CodeCreate implements IHandler<User.C_10403> {

    @Autowired
    private CodeBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_CODE_CREATE_CREATE;
    }

    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10403 req = User.C_10403.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx, req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10403 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        return service.codeCreate(uid,req.getBindUid());
    }
}