package com.z.core.net.handler.line9;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.game.line9.Line9RankService;
import com.z.model.common.MsgId;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 获取九线拉王的幸运玩家
 */
@Service
public class Line9Rank implements IHandler<Game.C_20301> {
    @Autowired
    Line9RankService service;

    @Override
    public int getMsgId() {
        return MsgId.C_LINE9_RANK;
    }

    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        Game.C_20301  req =  Game.C_20301.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, Game.C_20301 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        return service.getRank(uid);
    }
}
