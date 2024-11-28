package com.z.core.net.handler.wallet;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.transfer.TransferBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 转账
 */
@Service
public class TransferLog implements IHandler<User.C_10211> {
    private static final Logger log = LogManager.getLogger(TransferLog.class);
    @Autowired
    TransferBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_BANK_TRANSFER_LOG;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10211  req = User.C_10211.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10211 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        List<User.TransforLog> list=  service.getList(req.getType(),uid);
        User.S_10212 s10212 = User.S_10212.newBuilder().addAllLogs(list).build();
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_TRANSFER_LOG).setOk(true);
        return res.addMsg(ByteString.copyFrom(s10212.toByteArray())).build();
    }
}
