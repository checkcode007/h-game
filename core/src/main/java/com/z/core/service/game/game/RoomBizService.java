package com.z.core.service.game.game;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.common.util.PbUtils;
import com.z.core.service.game.aladdin.AladdinRoom;
import com.z.core.service.game.football.BallRoom;
import com.z.core.service.game.line9.Line9Room;
import com.z.core.service.game.majiang.MaJiangRoom;
import com.z.core.service.game.mali.MaliHigherRoom;
import com.z.core.service.game.mali.MaliRoom;
import com.z.core.service.game.room.RoomService;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.model.bo.user.User;
import com.z.model.common.MsgId;
import com.z.model.common.MsgResult;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import com.z.model.type.AddType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

/**
 * 游戏管理类
 */
@Service
public class RoomBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    WalletBizService walletBizService;

    public AbstractMessageLite enter(long uid, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("roomType:" + roomType).add("gameType:" + gameType);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_INTOROOM).setOk(true);
        SuperRoom room = RoomService.ins.addRoom(uid, gameType, roomType);
        if (room == null) {
            log.error(sj.add("room data fail").toString());
            res.setOk(false).setFailMsg("房间数据错误");
            return res.build();
        }

        MsgResult enterRet = room.enter(uid);
        if (!enterRet.isOk()) {
            log.error(sj.add("enter fail:" + enterRet.getMessage()).toString());
            res.setOk(false).setFailMsg(enterRet.getMessage());
            return res.build();
        }
        log.info(sj.add("success").toString());
        Game.S_20004.Builder b = Game.S_20004.newBuilder();
        b.setRoomId(room.getId());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    public  AbstractMessageLite out(long uid){
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_OUT_ROON).setOk(true);
        User user = UserService.ins.get(uid);
        if (user == null) {
            log.error(sj.add("user data fail").toString());
            res.setOk(false).setFailMsg("用户为空");
            return res.build();
        }
        if (user.getRoomId() < 1) {
            log.error(sj.add("had out").toString());
            return res.build();
        }
        sj.add("roomId:" + user.getRoomId()).add("gameType:" + user.getGameType()).add("roomType:" + user.getRoomType());
        SuperRoom room = RoomService.ins.getRoom(user.getRoomId());
        if (room == null) {
            log.error(sj.add("room data fail").toString());
            res.setOk(false).setFailMsg("房间数据错误");
            res.setFailMsg("房间数据错误");
            return res.build();
        }
        room.out(uid);
        RoomService.ins.removeRoom(room.getId());
        log.info(sj.add("success").toString());
        return res.build();
    }

    /**
     * 进入后请求信息
     * @return
     */
    public AbstractMessageLite initInfo(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        User user = UserService.ins.get(uid);
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_ENTER_INIT).setOk(true);
        if (user == null) {
            log.error(sj.add("user null").toString());
            res.setOk(false).setFailMsg("用户数据错误");
            return res.build();
        }
        CommonGame.GameType gameType=  user.getGameType();
        sj.add("roomId:" + user.getRoomId()).add("gameType:" + user.getGameType()).add("roomType:" + user.getRoomType());
        Game.S_20102.Builder b =Game.S_20102.newBuilder();
        SuperRoom room = RoomService.ins.getRoom(user.getRoomId());
        if(gameType == CommonGame.GameType.MAJIANG_2){
//            MaJiangRoom maJiangRoom = (MaJiangRoom) room;
//            b.addAllMjs(maJiangRoom.enterInit(uid).getT());
        }
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    public AbstractMessageLite bet(long uid, long gold,boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_SLOT_BET).setOk(true);
        User user = UserService.ins.get(uid);
        if (user == null) {
            log.error(sj.add("user data fail").toString());
            res.setOk(false).setFailMsg("用户为空");
            return res.build();
        }
        if (user.getRoomId() < 1) {
            log.error(sj.add("not in room").toString());
            res.setOk(false).setFailMsg("用户不在游戏中");
            return res.build();
        }
        sj.add("roomId:" + user.getRoomId()).add("gameType:" + user.getGameType()).add("roomType:" + user.getRoomType());

        SuperRoom room = RoomService.ins.getRoom(user.getRoomId());
        if (room == null) {
            log.error(sj.add("room data fail").toString());
            res.setOk(false).setFailMsg("房间数据错误");
            res.setFailMsg("房间数据错误");
            return res.build();
        }
        CommonGame.GameType gameType = user.getGameType();
        if(gameType == CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER){
            if(user.getHighC()<1){
                log.error(sj.add("higher fail").toString());
                res.setOk(false).setFailMsg("没有免费次数了");
                res.setFailMsg("没有免费次数了");
                return res.build();
            }
        }else{
            if(free){
                if(user.getFree()<1){
                    log.error(sj.add("free fail").toString());
                    res.setOk(false).setFailMsg("没有免费次数了");
                    res.setFailMsg("没有免费次数了");
                    return res.build();
                }
            }else{
                if (!walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.SUB, uid, gold,gameType,user.getRoomType())) {
                    log.error(sj.add("sub gold fail").toString());
                    res.setOk(false).setFailMsg("扣除金币失败");
                    res.setFailMsg("扣除金币失败");
                    return res.build();
                }
            }
        }

        MsgResult msgResult = null;
        Game.S_20104.Builder b = Game.S_20104.newBuilder();
        if (gameType == CommonGame.GameType.BAIBIAN_XIAOMALI || gameType == CommonGame.GameType.JINGDIAN_XIAOMALI ) {
            msgResult = maliBet((MaliRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setMali((Game.MaliBetMsg) msgResult.getT());
            }
        } else if (gameType == CommonGame.GameType.MAJIANG_2) {
            msgResult = majiangBet((MaJiangRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setMj((Game.MjBetMsg) msgResult.getT());
            }
        } else if (gameType == CommonGame.GameType.JIUXIANLAWANG) {
            msgResult = line9Bet((Line9Room) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setLine9((Game.Line9BetMsg) msgResult.getT());
            }
        } else if (gameType == CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER) {
            msgResult = maliHighBet((MaliHigherRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setHightMali((Game.MaliHighMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.SHAOLIN_ZUQIU) {
            msgResult = footBallBet((BallRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setHightMali((Game.MaliHighMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.ALADING) {
            msgResult = aladdinBet((AladdinRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setAladdin((Game.AladdinMsg) msgResult.getT());
            }
        }

        if (!msgResult.isOk()) {
            log.error(sj.add("bet fail").toString());
            res.setOk(false).setFailMsg(msgResult.getMessage());
            res.setFailMsg(msgResult.getMessage());
            return res.build();
        }
        log.info(sj.add("ret:" + PbUtils.pbToJson(b)).add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    public MsgResult<Game.MaliBetMsg> maliBet(MaliRoom maliRoom, long uid, long gold,boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.MaliBetMsg> msgRet = maliRoom.bet(uid, 0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.MaliHighMsg> maliHighBet(MaliHigherRoom maliRoom, long uid, long gold,boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.MaliHighMsg> msgRet = maliRoom.bet(uid,  gold);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }

    public MsgResult<Game.MjBetMsg> majiangBet(MaJiangRoom room, long uid, long gold,boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.MjBetMsg> msgRet = room.bet(uid, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }

    public MsgResult<Game.Line9BetMsg> line9Bet(Line9Room room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.Line9BetMsg> msgRet = room.bet(uid, 0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.FootBallMsg> footBallBet(BallRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.FootBallMsg> msgRet = room.bet(uid, 0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.AladdinMsg> aladdinBet(AladdinRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.AladdinMsg> msgRet = room.bet(uid, 0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }

}
