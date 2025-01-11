package com.z.core.service.game.slot;


import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.game.PoolService;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.game.Round;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletService;
import com.z.model.BetParam;
import com.z.model.bo.slot.*;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
/**
 * 房间
 */
public class SlotRoom extends SuperRoom {
    private static final Log log = LogFactory.getLog(SlotRoom.class);

    /**
     * 中奖的集合
     */
    protected List<Rewardline> rewardlines = new ArrayList<>();

    /**
     * 是否免费轮
     */
    protected  boolean free;
    /**
     * 免费次数
     */
    protected  int freeC = 0;
    /**
     * 总免费次数
     */
    protected  int totalFreeC = 0;

    /**
     * 高级玩法次数
     */
    protected  int highC = 0;

    protected  int totalHighC = 0;

    /**
     * 总奖励金额
     */
    protected long rewardGold = 0L;
    /**
     * 总倍率
     */
    protected int rate = 0;
    /**
     * 百搭符号
     */
    protected int baida=0;
    /**
     * 全屏幕符号
     */
    protected int fullType=0;
    protected int fullRate=0;
    /**
     * 中奖的支付线
     */
    protected List<Game.PayLine> payLines = new ArrayList<>();
    /**
     * 所有的坐标符号
     */
    protected  List<Game.Spot> spots = new ArrayList<>();

    /**
     * 退出图标类型
     */
    protected int quitType =0;

    protected BetParam param;


    public SlotRoom(CRoom cRoom,long uid) {
        super(cRoom,uid);

    }
    //todo 抽取到super
    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
        CSlot slot = service.getQuit(gameType);
        if(slot!=null){
            quitType = slot.getSymbol();
        }
        param = new BetParam();
        param.setUid(uid);
    }
    /**
     * 生成符号
     */
    public void generate() {
        board.clear();
        initParam();
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                param.setX(i);
                Slot slot = random(slots);
                SlotModel model =  SlotCommon.ins.toModel(slot,i,j);
                board.put(model.getX(), model.getY(), model);
            }
        }
    }
    /**
     * 是否变化牌的原点牌
     */
    public boolean isOrigin(int type){
        return false;
    }
    public int  getChangeType(){
        return 0;
    }
    /**
     * 进入下一轮的初始化
     */
    public void nextRound(){
        rewardlines.clear();
        freeC = 0;
        free = false;
        rewardGold = 0L;
        rate = 0;
        spots.clear();
        highC = 0;
        fullType = 0;
        fullRate = 0;
    }
    /**
     * 创建轮数
     * @param uid
     * @param gold
     * @return
     */
    public IRound createRound(long uid, long gold) {//百变玛丽
        Round round = new Round(roundIndex.incrementAndGet(),gameType,roomType);
        roundMap.put(uid, round);
        return round;
    }
    public void initParam(){
        Wallet wallet = WalletService.ins.get(uid);
        param.setGameType(gameType);
        param.setUid(uid);
        param.setState(user.getBetState().getK());
        param.setFree(free);
        param.setWinC(wallet.getWins());
        param.setTotalC(wallet.getBetC());
    }
    /**
     * 下注
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    public MsgResult bet(long uid, int type, long gold, boolean free) {
        nextRound();
        var round = createRound(uid, gold);
        StringJoiner sj = new StringJoiner(",").add("gameType:"+gameType).add("roomType:"+roomType).add("uid:"+uid).add("rid:" + id).add("roundId:" + round.getId()).add("uid:" + uid).add("gold:" + gold);
        log.info(sj.add("betState:"+user.getBetState()).toString());
        var roundCheck = round.bet(uid, 0, gold, free);
        if (!roundCheck.isOk()) {
            log.error("roundCheck fail");
            return roundCheck;
        }
        if (free) {
            gold = user.getFreeBetGold();
            sj.add("freeBet:" + user.getFreeBetGold());
        }
        this.free = free;
        betGold = gold;
        long realGold = getBetGold();
        sj.add("lastG:" + betGold).add("realG:" + realGold);
        //生成符号
        log.info("用户状态:"+user.getBetState()+"生成------>:" + round.getId());
        generate();
        print();
        spots = toSpots();
        checklines();
        checkBounus();
        payLines = toPayLines();
        for (Game.PayLine payLine : payLines) {
            rewardGold += payLine.getGold();
            rate += payLine.getRate();
//            highC += payLine.getHighC();
        }
        if(fullType>0){
            sj.add("fullType:"+fullType).add("fullRate:" + fullRate);
            rewardGold +=getBetGold()*fullRate;
            rate += fullRate;
        }
        if (rewardGold > 0) {
            walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, rewardGold, gameType, roomType);
        }
        if (free) {
            user.subFree();
            if (freeC > 0) {
                user.addFree(freeC);
            }
        } else {
            if(user.getGmFreeC()>0){
                freeC = user.getGmFreeC();
                user.resetGmFreeC();
            }
            user.addFree(freeC);
            if (freeC > 0) {
                user.setFreeBetGold((int) gold);
            } else if (user.getFreeBetGold() > 0) {
                user.setFreeBetGold(0);
            }
        }
        if(free){
            highC = 0;
        }
        if(highC>0){
            user.addHighC(highC);
        }
        totalFreeC  = user.getFree();
        totalHighC = user.getHighC();
        log.info(sj.add("free:"+freeC).add("highC:"+highC).add("rewardGold:" + rewardGold).add("success").toString());
        return new MsgResult<Game.Line9BetMsg>(true);
    }
    /**
     * 所有支付线
     */
    public void checklines() {
        List<Rewardline> rewardlines = new ArrayList<>();
        for (Payline payline : lines.values()) {
            Rewardline line = checkLine(payline);
            if (line == null) continue;
            rewardlines.add(line);
        }
        long realGold = getBetGold();
        for (Rewardline line : rewardlines) {
            CSlot cSlot = service.get(gameType, line.getK(), line.getPoints().size());
            if (cSlot == null) continue;
            line.setRate(cSlot.getRate());
            if (isPool(line.getK())) {
                poolLine(line);
            }else {
                line.setGold(realGold * line.getRate());
            }
            line.setSpecialC(cSlot.getC1());
            this.rewardlines.add(line);
            highC +=cSlot.getC1();
            log.info(line.toString());
        }
    }
    public void checkBounus(){
        int c = 0;
        int symbol = 0;
        for (SlotModel m : board.values()) {
            Slot slot = slots.get(m.getK());
            if (!slot.isBonus()) continue;
            c++;
            symbol = m.getK();
        }
        if(c<1) return;
        CSlot cSlot = service.get(gameType, symbol, c);
        if (cSlot == null) return;
        if(cSlot.getFreeMax()>0){
            freeC += RandomUtil.randomInt(cSlot.getFree(),cSlot.getFreeMax()+1);
        }else{
            freeC += cSlot.getFree();
        }
    }
    /**
     * 游戏上方奖池支付线处理
     */
    public void poolLine(Rewardline line) {
        long poolGold = PoolService.ins.get(gameType);
        line.setGold(poolGold * line.getRate() / 10000);
        log.info("poolGold:" + poolGold +":"+ line.getGold());
    }

    /**
     * 检查一条线
     * @param line
     * @return ()
     */
    public Rewardline checkLine(Payline line){
        Rewardline payline = checkHigher(line);
        if(payline!=null){
            return payline;
        }
        //从左到右
        int leftType = 0;
        List<Point> leftList = new ArrayList<>();
        boolean had_baida = false;
        for (Point p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            int type = m.getK();
            if(m.isBaida()){
               had_baida = true;
            }
            if(leftType<1){
                leftType = type;
                leftList.add(p);
            }else if(isSame(x,leftType,type)){
                leftList.add(p);
            }else{
                break;
            }
        }
        if(payline == null){
            payline = new Rewardline(leftType,line.getLineId());
        }
        payline.addPoints(leftList);
        payline.setHadBaida(had_baida);
        return payline;
    }

    /**
     * 检查触发高级玩法的符号
     * @param line
     * @return
     */
    public Rewardline checkHigher(Payline line){
        Rewardline payline = null;
        int type = 0;
        for (Point p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            type = m.getK();
            if(!m.isScatter()) continue;
            if(payline==null){
                payline =new Rewardline(type,line.getLineId());
            }
            payline.addSpecicalC();
            payline.addPoint(p);
        }
        if(payline == null){
            return null;
        }
        CSlot slot = service.get(gameType,type,payline.getSpecialC());
        if(slot == null) return null;
        return payline;
    }

    public boolean isPool(int type) {
        return false;
    }

    public Slot random (Map < Integer, Slot > slots) {
        Set<Integer> rewardSymbols = new HashSet<>();
        for (Rewardline line : rewardlines) {
            rewardSymbols.add(line.getK());
        }
        Slot slot =  SlotCommon.ins.random(gameType,board,slots, rewardSymbols, param);
        if(slot.isScatter()){
            param.addScatter();
        } else if (slot.isBonus()) {
            param.addBonus();
        } else if (slot.isBaida()) {
            param.addBaida();
        }
        return slot;
    }
    public void print () {
        SlotCommon.ins.print(board,gameType,roomType,id,uid);
    }
    public List<Game.Spot> toSpots(){
        List<Game.Spot> list = new ArrayList<>(30);
        for (SlotModel m : board.values()) {
            Game.Spot.Builder b = Game.Spot.newBuilder();
            b.setSymbol(m.getK()).setX(m.getX()).setY(m.getY()).setChangeType(m.getChangeType());
            list.add(b.build());
        }
        return list;
    }
    public List<Game.PayLine> toPayLines(){
        List<Game.PayLine> list = new ArrayList<>();
        for (Rewardline m : rewardlines) {
            Game.PayLine.Builder b= Game.PayLine.newBuilder();
            b.setLineId(m.getLineId()).setGold(m.getGold()).setRate(m.getRate()).setHighC(m.getSpecialC());
            for (Point p : m.getPoints()) {
                b.addSpots(Game.Spot.newBuilder().setSymbol(m.getK()).setX(p.getX()).setY(p.getY()).build());
            }
            list.add(b.build());
        }
        return list;
    }

    @Override
    public void update(long now) {
        super.update(now);

    }
    public static void main(String[] args) {
        // 定义位置从 1 到 9，允许出现的位置为 1, 3, 5, 7
        // 使用一个 32 位整数来表示这些位置的状态
        int allowedPositions = 0b010101010;  // 二进制表示：1, 3, 5, 7 位置为允许

        // 检查某个位置是否可以出现
        int position = 5;  // 假设要检查位置5是否允许

        // 判断该位置是否在允许的位置列表中
        if ((allowedPositions & (1 << (position - 1))) != 0) {
            System.out.println("位置 " + position + " 是允许的");
        } else {
            System.out.println("位置 " + position + " 不允许");
        }

        // 检查另一个位置 2 是否可以出现
        position = 2;
        if ((allowedPositions & (1 << (position - 1))) != 0) {
            System.out.println("位置 " + position + " 是允许的");
        } else {
            System.out.println("位置 " + position + " 不允许");
        }
    }
}
