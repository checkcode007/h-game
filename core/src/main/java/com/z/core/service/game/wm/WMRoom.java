package com.z.core.service.game.wm;


import cn.hutool.core.util.RandomUtil;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.slot.*;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 水浒传
 */
public class WMRoom extends SlotRoom {
//    protected Logger log = LoggerFactory.getLogger(getClass());

    private static final Log log = LogFactory.getLog(WMRoom.class);

    public WMRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
    }

    /**
     * 下注
     * 免费次数不进入小玛丽
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    @Override
    public MsgResult<Game.WMBetMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        Wallet wallet = WalletService.ins.get(uid);
        Game.WMBetMsg.Builder b = Game.WMBetMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
        b.setAddFreeC(freeC).setTotalFreeC(totalFreeC).setHighC(totalHighC);
        b.setFullType(fullType).setFullRate(fullRate);
        var ret = new MsgResult<Game.WMBetMsg>(true);
        ret.ok(b.build());
        return ret;
    }
    @Override
    public void checklines() {
        //全屏判断
        int fullType=0;
        for (SlotModel m : board.values()) {
            if(fullType == 0){
                fullType = m.getK();
            } else if ( fullType != m.getK()) {
                fullType= 0;
                break;
            }
        }
        if(fullType >1){
            this.fullType = fullType;
            this.fullRate= service.getFull(gameType,fullType).getRate();
            return;
        }

        List<Rewardline> rewardlines = new ArrayList<>();
        for (Line payline : lineMap.values()) {
            Rewardline line = checkLine(payline);
            if (line == null) continue;
            rewardlines.add(line);
        }
        long base = getBetGold();
        for (Rewardline line : rewardlines) {
            CSlot cSlot = service.get(gameType, line.getK(), line.getPoints().size());
            if (cSlot == null) continue;
            line.setRate(cSlot.getRate());
            line.setGold(base * line.getRate());
            line.setSpecialC(cSlot.getC1());
            this.rewardlines.add(line);
            highC +=cSlot.getC1();
            log.info(line.toString());
        }

    }

    /**
     * 检查一条线
     * @param line
     * @return ()
     */
    public Rewardline checkLine(Line line){
        Rewardline payline = checkHigher(line);
        if (payline != null) return payline;
        //从左到右
        int leftType = 0;
        List<SlotModel> leftList = new ArrayList<>();

        for (SlotModel p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            int type = m.getK();
            if(leftType<1){
                leftType = type;
                leftList.add(p);
            }else if(type==leftType){
                leftList.add(p);
            }else{
                break;
            }
        }
        //从右到左
        int rightType = 0;
        List<SlotModel> rightList = new ArrayList<>();
        for (int i = line.getPoints().size() - 1; i >= 0; i--) {
            SlotModel p =line.getPoints().get(i);
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            int type = m.getK();
            if(rightType<1){
                rightType = type;
                rightList.add(p);
            }else if(type==rightType){
                rightList.add(p);
            }else{
                break;
            }
        }
        int leftRate = 0,rightRate = 0;
        if(!leftList.isEmpty()){
            CSlot slot = service.get(gameType,leftType,leftList.size());
            if (slot!=null){
                leftRate = slot.getRate();
            }
        }
        if (!rightList.isEmpty()){
            CSlot slot = service.get(gameType,rightType,rightList.size());
            if (slot!=null){
                rightRate = slot.getRate();
            }
        }

        if(leftRate>=rightRate){
            if(payline == null){
                payline = new Rewardline(leftType,line.getLineId());
            }
            payline.addPoints(leftList);
        }else{
            if(payline == null){
                payline = new Rewardline(rightType,line.getLineId());
            }
            payline.addPoints(rightList);
        }
        return payline;
    }

    /**
     * 检查触发高级玩法的符号
     * @param line
     * @return
     */
    public Rewardline checkHigher(Line line){
        int leftC = 0,rightC = 0;
        List<SlotModel> leftList = new ArrayList<>();
        List<SlotModel> rightList = new ArrayList<>();
        //从左到右
        for (SlotModel p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            if(!m.isScatter()) continue;
            leftC++;
            leftList.add(p);
        }
        //从右到左
        for (int i = line.getPoints().size() - 1; i >= 0; i--) {
            SlotModel p =line.getPoints().get(i);
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            if(!m.isScatter()) continue;
            rightC++;
            rightList.add(p);
        }
        Rewardline payline = null;
        int type = 0;
        if(payline==null){
            CSlot slot = service.getScatter(gameType);
            payline =new Rewardline(slot.getSymbol(),line.getLineId());
            type = slot.getSymbol();
        }
        if(leftC>=rightC){
            payline.addSpecicalC(leftList.size());
            payline.addPoints(leftList);
        }else{
            payline.addSpecicalC(rightList.size());
            payline.addPoints(rightList);
        }
        if(payline.getSpecialC()<1) return null;
        CSlot slot = service.get(gameType,type,payline.getSpecialC());
        if(slot == null) return null;
        return payline;
    }

    @Override
    public void generate() {
        super.generate();
    }




    /**
     * 骰子比大小（比倍）
     *
     */
    public Game.S_20312.Builder compareDice(CommonGame.WMDice type, long gold){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("rid:"+id).add("type:"+type).add("gold:"+gold);
        log.info(sj.toString());
        int dice1 = RandomUtil.randomInt(1,7);
        int dice2 = RandomUtil.randomInt(1,7);
        int number = dice1 + dice2;
        int diceRate = 0;
        CommonGame.WinState winState = CommonGame.WinState.FAIL;
        if(number<7){
            if(type == CommonGame.WMDice.WD_SMALL){
                winState = CommonGame.WinState.WIN;
                if(dice1 == dice2){
                    diceRate = 2;
                }else{
                    diceRate = 4;
                }
            }
        }else if(number==7){
            if(type == CommonGame.WMDice.WD_TIE){
                winState = CommonGame.WinState.WIN;
                diceRate = 6;
            }
        }else{
            if(type == CommonGame.WMDice.WD_BIG){
                winState = CommonGame.WinState.WIN;
                if(dice1 == dice2){
                    diceRate = 2;
                }else{
                    diceRate = 4;
                }
            }
        }
        if(winState == CommonGame.WinState.WIN){
            gold = diceRate * gold;
            walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, gold, gameType, roomType);
        }else{
            walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.SUB, uid, gold, gameType, roomType);
        }
        Wallet wallet =WalletService.ins.get(uid);
        Game.S_20312.Builder b = Game.S_20312.newBuilder();
        b.setGold(gold).setLeaveGold(wallet.getGold()).setState(winState).setDice1(dice1).setDice2(dice2);
        return b;
    }
}
