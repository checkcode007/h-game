package com.z.core.net.handler.wallet;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.wallet.BankLogBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 转账
 */
@Service
public class BankLog implements IHandler<User.C_10213> {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
//    private static final Logger log = LogManager.getLogger(BankLog.class);
    @Autowired
    BankLogBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_BANK_LOG;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10213  req = User.C_10213.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10213 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        List<User.BankLog> list=  service.getList(uid);
        User.S_10214 s10214 = User.S_10214.newBuilder().addAllGoldlogs(list).build();
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_LOG).setOk(true);
        return res.addMsg(ByteString.copyFrom(s10214.toByteArray())).build();
    }
}
