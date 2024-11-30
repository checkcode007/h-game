package com.z.core.net.handler.wallet;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.wallet.WalletBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.CommonUser;
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
public class Transfer implements IHandler<User.C_10207> {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
//    private static final Logger log = LogManager.getLogger(Transfer.class);
    @Autowired
    WalletBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_BANK_TRANSFER;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10207  req = User.C_10207.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10207 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        boolean b =  service.changeBank(CommonUser.BankType.BT_TRANSFER,uid,req.getTargetId(),req.getGold());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_TRANSFER).setOk(true);
        if(!b){
            res.setOk(false);
            res.setFailMsg("取出异常");
            log.error("uid:"+uid+" 取出异常 :"+req.getGold());
        }
        return res.build();
    }
}
