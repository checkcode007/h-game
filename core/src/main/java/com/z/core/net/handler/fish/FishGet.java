package com.z.core.net.handler.fish;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.game.fish.FishBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 捕鱼-补到鱼
 */
@Service
public class FishGet implements IHandler<Game.C_20203> {
    @Autowired
    FishBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_FISH_GET;
    }

    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        Game.C_20203  req =  Game.C_20203.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, Game.C_20203 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        return service.fishGet(uid,req.getFireType(),req.getFishsList());
    }
}
