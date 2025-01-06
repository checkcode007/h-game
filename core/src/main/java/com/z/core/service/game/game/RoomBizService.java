package com.z.core.service.game.game;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.common.util.PbUtils;
import com.z.core.service.game.aladdin.AladdinRoom;
import com.z.core.service.game.corpse.CorpseRoom;
import com.z.core.service.game.football.BallRoom;
import com.z.core.service.game.line9.Line9Room;
import com.z.core.service.game.majiang.MaJiangRoom;
import com.z.core.service.game.mali.MaliHigherRoom;
import com.z.core.service.game.mali.MaliRoom;
import com.z.core.service.game.pig.PigRoom;
import com.z.core.service.game.puck.PuckRoom;
import com.z.core.service.game.room.RoomService;
import com.z.core.service.game.wm.WMHigherRoom;
import com.z.core.service.game.wm.WMRoom;
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
        sj.add("roomId:" + user.getRoomId()).add("gameType:" + user.getGameType()).add("roomType:" + user.getRoomType());
        Game.S_20102.Builder b =Game.S_20102.newBuilder();
         RoomService.ins.getRoom(user.getRoomId());
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
                b.setMj((Game.ClearGameMsg) msgResult.getT());
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
                b.setFootBall((Game.FootBallMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.ALADING) {
            msgResult = aladdinBet((AladdinRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setAladdin((Game.AladdinMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.BINGQIUTUPO) {
            msgResult = puckBet((PuckRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setPuck((Game.ClearGameMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.SHUIHUZHUAN) {
            msgResult = wmBet((WMRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setWaterMargin((Game.WMBetMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.SHUIHUZHUAN_HIGHER) {
            msgResult = wmHigherBet((WMHigherRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setWmHigher((Game.WMHighMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.JINZHUSONGFU) {
            msgResult = pigBet((PigRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setPig((Game.PigMsg) msgResult.getT());
            }
        }else if (gameType == CommonGame.GameType.JIANGSHIXINNIANG) {
            msgResult = corpseBet((CorpseRoom) room, uid, gold,free);
            if(msgResult.isOk()) {
                b.setCorpse((Game.CorpseMsg) msgResult.getT());
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

    public MsgResult<Game.ClearGameMsg> majiangBet(MaJiangRoom room, long uid, long gold,boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.ClearGameMsg> msgRet = room.bet(uid, gold,free);
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

    public MsgResult<Game.ClearGameMsg> puckBet(PuckRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.ClearGameMsg> msgRet = room.bet(uid, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.WMBetMsg> wmBet(WMRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.WMBetMsg> msgRet = room.bet(uid,0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.WMHighMsg> wmHigherBet(WMHigherRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.WMHighMsg> msgRet = room.bet(uid,0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.PigMsg> pigBet(PigRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.PigMsg> msgRet = room.bet(uid,0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }
    public MsgResult<Game.CorpseMsg> corpseBet(CorpseRoom room, long uid, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("free:" + free);
        MsgResult<Game.CorpseMsg> msgRet = room.bet(uid,0, gold,free);
        if (!msgRet.isOk()) {
            log.error(sj.add("bet fail").toString());
            return msgRet;
        }
        return msgRet;
    }

    /**
     * 水浒传比大小
     * @param uid
     * @param wmDice
     * @param gold
     * @return
     */
    public AbstractMessageLite wmDice(long uid, CommonGame.WMDice wmDice, long gold) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gold:" + gold).add("dice:" + wmDice);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_WM_DICE).setOk(true);
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
        if(gameType != CommonGame.GameType.SHUIHUZHUAN){
            log.error(sj.add("room  fail").toString());
            res.setOk(false).setFailMsg("不是水浒传房间");
            res.setFailMsg("不是水浒传房间");
            return res.build();
        }
        WMRoom wmRoom =(WMRoom)room;
        Game.S_20312.Builder s20312 = wmRoom.compareDice(wmDice,gold);
        log.info(sj.add("ret:" + PbUtils.pbToJson(s20312)).add("success").toString());
        return res.addMsg(ByteString.copyFrom(s20312.build().toByteArray())).build();
    }


    /**
     * 抓鬼游戏开始
     * @param uid
     * @return
     */
    public AbstractMessageLite corPseStart(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CORPSE_START).setOk(true);
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
        if(gameType != CommonGame.GameType.JIANGSHIXINNIANG){
            log.error(sj.add("room  fail").toString());
            res.setOk(false).setFailMsg("不是僵尸新娘房间");
            res.setFailMsg("不是僵尸新娘房间");
            return res.build();
        }
        CorpseRoom corpseRoom =(CorpseRoom)room;
        corpseRoom.startCatchGame();
        log.info(sj.add("success").toString());
        return res.build();
    }

    /**
     * 抓鬼游戏-猜灯笼
     * @param uid
     * @return
     */
    public AbstractMessageLite corPseCatch(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CORPSE_CATCH).setOk(true);
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
            return res.build();
        }
        CommonGame.GameType gameType = user.getGameType();
        if(gameType != CommonGame.GameType.JIANGSHIXINNIANG){
            log.error(sj.add("room  fail").toString());
            res.setOk(false).setFailMsg("不是僵尸新娘房间");
            return res.build();
        }
        CorpseRoom corpseRoom =(CorpseRoom)room;
        MsgResult<Game.S_20324.Builder> msgResult = corpseRoom.catchGame();
        if (!msgResult.isOk()) {
            log.error(sj.add("data error").toString());
            res.setOk(false).setFailMsg(msgResult.getMessage());
            return res.build();
        }
        Game.S_20324.Builder b = msgResult.getT();
        log.info(sj.add("ret:" + PbUtils.pbToJson(b)).add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }
}
