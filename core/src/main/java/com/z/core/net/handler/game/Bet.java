package com.z.core.net.handler.game;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.handler.IHandler;
import com.z.core.service.game.card.CardService;
import com.z.core.service.wallet.WalletBizService;
import com.z.model.common.MsgId;
import com.z.model.mysql.GWallet;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 下注
 */
@Service
public class Bet implements IHandler<Game.C_20007> {
    @Autowired
    CardService service;
    @Autowired
    WalletBizService walletService;

    @Override
    public int getMsgId() {
        return MsgId.C_BET;
    }

    @Override
    public AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception {
        List<ByteString> list = msgReq.getMsgList();
        Game.C_20007  req =  Game.C_20007.parseFrom(ByteString.copyFrom(list).toByteArray());
        return handleDo(ctx,req);
    }

    @Override
    public AbstractMessageLite handleDo(ChannelHandlerContext ctx, Game.C_20007 req) {
        long uid = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        MyMessage.MyMsgRes.Builder res =MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_INTOGAME);
        boolean b_bet = service.bet(uid,req.getRoomId(),req.getGameId(),req.getSuit(),req.getBetGold());
        if(!b_bet){
            res.setOk(false).setFailMsg("进入异常");
            return res.build();
        }
        GWallet wallet = walletService.findById(uid);
        long gold =0L;
        if(wallet!=null){
            gold = wallet.getGold();
        }
        Game.S_20008.Builder b = Game.S_20008.newBuilder().setGold(gold);
        res.setOk(true).addMsg(ByteString.copyFrom(b.build().toByteArray()));
        return res.build();
    }
}
