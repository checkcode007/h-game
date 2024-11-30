package com.z.core.net.handler.game;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.game.room.RoomBizService;
import com.z.model.common.MsgId;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 进入游戏
 */
@Service
public class IntoGame implements IHandler<Game.C_20001> {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private RoomBizService service;

    @Override
    public int getMsgId() {
        return MsgId.C_INTOGAME;
    }

    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        Game.C_20001  req =  Game.C_20001.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, Game.C_20001 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        List<CRoom> list = service.into(uid,req.getGameType());

        Game.S_20002.Builder b = Game.S_20002.newBuilder();
        if(list!=null){
            for (CRoom cRoom : list) {
                Game.Room.Builder room = Game.Room.newBuilder();
                log.info(cRoom.toString());
                room.setId(cRoom.getId()).setType(CommonGame.RoomType.forNumber(cRoom.getType())).setMinBalance(cRoom.getMinBalance());
                b.addRooms(room.build());
            }
        }
        MyMessage.MyMsgRes.Builder res =MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_INTOGAME);
        res.setOk(true).addMsg(ByteString.copyFrom(b.build().toByteArray()));
        return  res.build();
    }


}
