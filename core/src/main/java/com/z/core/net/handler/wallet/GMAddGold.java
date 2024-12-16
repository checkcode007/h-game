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
import com.z.model.type.AddType;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * gm -加金币
 */
@Service
public class GMAddGold implements IHandler<User.C_10321> {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
//    private static final Logger log = LogManager.getLogger(BankDeposit.class);
    @Autowired
    WalletBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_GM_ADDGOLD;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        User.C_10321  req = User.C_10321.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, User.C_10321 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        service.changeGold(CommonUser.GoldType.GT_GM, AddType.ADD,uid,req.getGold(),null,null);
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_GM_ADDGOLD).setOk(true);
        return res.build();
    }
}
