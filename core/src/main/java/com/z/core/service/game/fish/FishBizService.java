package com.z.core.service.game.fish;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.service.game.game.RoomBizService;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.game.room.RoomService;
import com.z.core.service.user.UserBizService;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgId;
import com.z.model.mysql.cfg.CFish;
import com.z.model.mysql.cfg.CFishFire;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import com.z.model.type.AddType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 捕鱼管理类
 */
@Service
public class FishBizService {
    private static final Log log = LogFactory.getLog(FishBizService.class);

//    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CFishService cFishService;
    @Autowired
    UserBizService userService;

    @Autowired
    WalletBizService walletBizService;
    @Autowired
    private RoomBizService roomBizService;



    public AbstractMessageLite initInfo(long uid, CommonGame.RoomType roomType) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_FISH_INIT).setOk(true);
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

        log.info(sj.add("success").toString());
        Game.S_20202.Builder b = Game.S_20202.newBuilder();
        for (CFishFire v : cFishService.getFire(roomType).values()) {
            b.addFires(Game.FishFireModel.newBuilder().setType(CommonGame.FishFire.valueOf(v.getType())).setGold(v.getGold()).build());
        }
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    /**
     * 捕获到鱼
     * @param uid
     * @param fishFire
     * @return
     */
    public AbstractMessageLite fishFire(long uid, CommonGame.FishFire fishFire) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("fire:" + fishFire);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_FISH_FIRE).setOk(true);
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
        CommonGame.GameType gameType = user.getGameType();
        CommonGame.RoomType roomType = user.getRoomType();
        sj.add("roomId:" + user.getRoomId()).add("gameType:" + gameType).add("roomType:" + roomType);
        CFishFire cFishFire = cFishService.getFire(fishFire,roomType);
        if(cFishFire == null) {
            log.error(sj.add("cFishFire null").toString());
            res.setOk(false).setFailMsg("配置异常");
            return res.build();
        }
        sj.add("cfg:" + cFishFire.getId()).add("gold:" + cFishFire.getGold());

        if (!walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.SUB, uid, cFishFire.getGold(),gameType,roomType)) {
            log.error(sj.add("sub gold fail").toString());
            res.setOk(false).setFailMsg("扣除金币失败");
            res.setFailMsg("扣除金币失败");
            return res.build();
        }
        Game.S_20206.Builder b = Game.S_20206.newBuilder();
        Wallet wallet = WalletService.ins.get(uid);
        b.setLeaveGold(wallet.getGold());
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    /**
     * 捕获到鱼
     * @param uid
     * @param fishFire
     * @return
     */
    public AbstractMessageLite fishGet(long uid, CommonGame.FishFire fishFire, List<Game.Fish> fishList) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("fire:" + fishFire)
                .add("fishs:" + fishList);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_FISH_GET).setOk(true);
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
        CommonGame.GameType gameType = user.getGameType();
        CommonGame.RoomType roomType = user.getRoomType();
        sj.add("rId:" + user.getRoomId()).add("gType:" + gameType).add("rType:" + roomType);
        CFishFire cFishFire = cFishService.getFire(fishFire,roomType);
        if(cFishFire == null) {
            log.error(sj.add("cFishFire null").toString());
            res.setOk(false).setFailMsg("配置异常");
            return res.build();
        }
       SuperRoom room =  RoomService.ins.getRoom(user.getRoomId());
        if(room == null){
            log.error(sj.add("room null").toString());
            res.setOk(false).setFailMsg("没有进入房间");
            return res.build();
        }
        FishRoom fishRoom =(FishRoom)room;
        Game.S_20204.Builder b =fishRoom.fishCatch(uid,cFishFire,fishList);
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }



}
