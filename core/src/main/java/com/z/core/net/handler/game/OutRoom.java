package com.z.core.net.handler.game;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.game.game.RoomBizService;
import com.z.model.common.MsgId;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 退出房间
 */
@Service
public class OutRoom implements IHandler<Game.C_20105> {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    RoomBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_INTOROOM;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        Game.C_20105  req =  Game.C_20105.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, Game.C_20105 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
      return service.out(uid,"outReq");

    }

}
