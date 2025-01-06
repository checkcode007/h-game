package com.z.core.service.game.corpse;


import cn.hutool.core.util.RandomUtil;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.service.game.PoolService;
import com.z.core.service.game.line9.Line9RankService;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgId;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import com.z.model.type.AddType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * 僵尸新娘房间
 */
public class CorpseRoom extends SlotRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    Line9RankService line9Service;
    CSlotService service;


    public CorpseRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        line9Service = SpringContext.getBean(Line9RankService.class);
        service = SpringContext.getBean(CSlotService.class);
    }

    /**
     * 下注
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    public MsgResult<Game.CorpseMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        var b = Game.CorpseMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        b.setAddFreeC(freeC).setTotalFreeC(totalFreeC).setCatchGame(highC>0?true:false);
        Wallet wallet = WalletService.ins.get(uid);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
        addRecord(uid);
        PoolService.ins.add(gameType, gold);
        var ret = new MsgResult<Game.CorpseMsg>(true);
        ret.ok(b.build());
        return ret;
    }


    @Override
    public long getBetGold() {
        return betGold / cfgBizService.getBB_XML_bet_base();
    }

    @Override
    public boolean isPool(int type) {
        return type == CommonGame.LINE9.L9_BOX_VALUE;
    }
    /**
     * 奖池支付线处理
     */
    @Override
    public void poolLine(Rewardline line) {
        long poolGold = PoolService.ins.get(gameType);
        line.setGold(poolGold * line.getRate() / 10000);
        log.info("poolGold:" + poolGold +":"+ line.getGold());
    }

    @Override
    public boolean isSame(int i, int k1, int k2) {
        if (super.isSame(i, k1, k2)) {
            return true;
        }
        if (i != 0) {
            if (k2 == CommonGame.LINE9.L9_BAR_VALUE) {
                return true;
            }
        }
        return false;
    }

    public void addRecord(long uid) {
        long gold = 0L;
        for (Rewardline m : rewardlines) {
            if (m.getK() == CommonGame.LINE9.L9_BOX_VALUE) {
                gold += m.getGold();
            }
        }
        if (gold > 0) {
            line9Service.add(uid, gold);
        }
    }

    @Override
    public void update(long now) {
        super.update(now);
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_LINE9_BOX_POOL).setOk(true);
        res.addMsg(ByteString.copyFrom(Game.S_20304.newBuilder().setGold(PoolService.ins.get(gameType)).build().toByteArray()));
        UserChannelManager.sendMsg(uid, res.build());
    }

    /**
     * 太极计数
     */
    int addIndex=0;
    /**
     * 鬼火计数
     */
    int subIndex=0;
    /**
     * 僵尸新娘的抓鬼玩法倍数
     */
    List<Integer> rateList = new ArrayList<>();
    /**
     * 抓鬼的总倍率
     */
    int catchRate = 0;
    public void initRate(){
        int current = 10;  // 初始值
        int step = 5;     // 步长

        while (current <= 150) {
            rateList.add(current);  // 将当前数值加入列表
            current += step;    // 更新当前值
            step *= 5;          // 步长递增为上一步的 5 倍
        }
    }

    public int getRate() {
        if(rateList.isEmpty()){
            initRate();
        }
        Collections.shuffle(rateList);
        return rateList.get(0);
    }


    /**
     * 开始抓鬼游戏
     */
    public void startCatchGame(){
        addIndex = 0;
        subIndex = 0;
    }

    /**
     * 抓鬼
     */
    public MsgResult<Game.S_20324.Builder> catchGame(){
        int betGold = (int)getBetGold();
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("rid:"+id);
        log.info(sj.add("betGold:"+betGold).toString());
        if(addIndex>=3 || subIndex>=3 ){
          log.error(sj.add("game over").add("add:"+addIndex).add("sub:"+subIndex).toString());
          return new MsgResult("游戏已经结束");
        }

        int random = RandomUtil.randomInt(1, 10);
        int type = 0;
        if(random%2==0){//太极图
            addIndex++;
            type = 1;
        }else{//鬼火
            subIndex++;
            type = 2;
        }
        boolean over = false;
        if(addIndex>=3 || subIndex>=3 ){
            over = true;
        }
        long gold = 0L;
        int rate =  getRate();
        catchRate += rate;
        if(over){
            gold = betGold*catchRate*Math.max(1,addIndex);
            walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, gold, gameType, roomType);
        }
        Wallet wallet = WalletService.ins.get(uid);
        Game.S_20324.Builder b = Game.S_20324.newBuilder().setOver(over).setTotalRate(catchRate);
        b.setType(type).setBetGold(betGold).setC1(addIndex).setC2(subIndex);
        b.setRate(rate).setGold(gold);
        b.setLeaveGold(wallet.getGold());
        log.info(b.toString());
        return new MsgResult<Game.S_20324.Builder>(b);
    }
}
