package com.z.core.net.handler.game;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.game.card.CardGame;
import com.z.core.service.game.game.GameBizService;
import com.z.core.service.game.room.RoomBizService;
import com.z.model.common.MsgId;
import com.z.model.mysql.GRoom;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 进入房间
 */
@Service
public class IntoRoom implements IHandler<Game.C_20003> {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    RoomBizService service;
    @Autowired
    GameBizService gameBizService;

    @Override
    public int getMsgId() {
        return MsgId.C_INTOROOM;
    }
    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        Game.C_20003  req =  Game.C_20003.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, Game.C_20003 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        MyMessage.MyMsgRes.Builder res =MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_INTOROOM).setOk(true);
        GRoom gRoom = service.intoGameRoom(uid, req.getGameType(),req.getRoomType());
        if(gRoom == null){
            res.setOk(false).setFailMsg("房间数据错误");
            res.setFailMsg("房间数据错误");
            return res.build();
        }
        CardGame game = gameBizService.into(uid,gRoom.getId(),req.getGameType());
        Game.S_20004.Builder b = Game.S_20004.newBuilder();
        b.setRoomId((int)gRoom.getId()).setGameId(game.getId()).setState(game.getState());
        b.setLeaveTime(game.getNextTime());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

}
