package com.z.core.service.game.slot;


import cn.hutool.core.util.RandomUtil;
import com.z.core.service.game.PoolService;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.game.Round;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.*;
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

    protected SlotMachine machine;

    protected  Map<Integer,Rewardline> lineMap;


    public SlotRoom(CRoom cRoom,long uid) {
        super(cRoom,uid);
        machineService = SpringContext.getBean(SlotMachineService.class);
        machine = machineService.getMachine(gameType);
        lineMap = new HashMap<>();
    }
    //todo 抽取到super
    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
        CSlot slot = service.getQuit(gameType);
        if(slot!=null){
            quitType = slot.getSymbol();
        }
        roomWinGold = 0;


    }
    public boolean checkPayLine(Rewardline line){
        for (Rewardline line1 : lineMap.values()) {
            for (SlotModel m : line1.getPoints()) {
                for (SlotModel m1 : line.getPoints()) {
                    if(m.getX() == m1.getX() && m.getY() == m1.getY()){
                        if(line1.getK()!=line.getK()){
                           return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    /**
     * 生成符号
     */
    public void generate() {
        board.clear();
        initParam();
        int num = RandomUtil.randomInt(0, 3);
        List<Slot> list = new ArrayList<>(slots.values());
        list.removeIf(e->e.isBonus()|| e.isScatter()|| e.isBaida());
        Set<Integer> hadSet = new HashSet<>();

        for (int i = 0; i < num; i++) {
            Rewardline line = machine.randomLine();
//            if(hadSet.contains(line.getK())){
//                continue;
//            }
            if (!checkPayLine(line)) {
               continue;
            }

            lineMap.put(line.getLineId(), line);
            hadSet.add(line.getK());
        }
        StringJoiner sj = new StringJoiner(",");
        for (Integer i : hadSet) {
            sj.add(i.toString());
        }
        log.info("had:"+sj);
        for (Rewardline line : lineMap.values()) {
            list.removeIf(e->e.getK()== line.getK());
            for (SlotModel m : line.getPoints()) {
                board.put(m.getX(), m.getY(), m);
            }
        }
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                SlotModel m =  board.get(i,j);
                if(m!=null) continue;
                Collections.shuffle(list);
                Slot slot = list.get(0);
                m = SlotCommon.ins.toModel(slot,i,j);
                if(i == 0){
                    list.removeIf(e->e.getK()== slot.getK());
                }
                board.put(m.getX(), m.getY(), m);
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
        freeC = 0;
        free = false;
        rewardGold = 0L;
        rate = 0;
        spots.clear();
        highC = 0;
        fullType = 0;
        fullRate = 0;
        lineMap.clear();
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
        log.info(sj.add("betState:"+user.getSlotState()).toString());
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
        log.info("用户状态:"+user.getSlotState()+"生成------>:" + round.getId());
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

        roomWinGold += realGold;
        roomBetGold += gold;
        param.setRoomWinGold(roomWinGold);
        param.setRoomBetGold(roomBetGold);
        log.info(sj.add("free:"+freeC).add("highC:"+highC).add("rewardGold:" + rewardGold).add("success").toString());
        return new MsgResult<Game.Line9BetMsg>(true);
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
     * 所有支付线
     */
    public void checklines() {
        if(lineMap.isEmpty()){
            return;
        }
        List<Rewardline> rewardlines = new ArrayList<>();
        long realGold = getBetGold();
        for (Rewardline line : lineMap.values()) {
            highC += line.getSpecialC();
            line.setGold(highC);
            line.setGold(realGold * line.getRate());
            log.info(line.toString());
        }
    }


    /**
     * 检查触发高级玩法的符号
     * @param line
     * @return
     */
    public Rewardline checkHigher(Line line){
        Rewardline payline = null;
        int type = 0;
        for (SlotModel p : line.getPoints()) {
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

//    public Slot random (Map < Integer, Slot > slots) {
//        Set<Integer> rewardSymbols = new HashSet<>();
//        for (Rewardline line : rewardlines) {
//            rewardSymbols.add(line.getK());
//        }
//        Slot slot =  SlotCommon.ins.random(gameType,board,slots, rewardSymbols, param);
//        if(slot.isScatter()){
//            param.addScatter();
//        } else if (slot.isBonus()) {
//            param.addBonus();
//        } else if (slot.isBaida()) {
//            param.addBaida();
//        }
//        return slot;
//    }
    public void print () {
        SlotCommon.ins.print(board,gameType,roomType,id,uid);
    }
    public List<Game.Spot> toSpots(){
        List<Game.Spot> list = new ArrayList<>(30);
        for (SlotModel m : board.values()) {
            Game.Spot.Builder b = Game.Spot.newBuilder();
            b.setSymbol(m.getK()).setX(m.getX()).setY(m.getY()).setChangeType(m.getChangeType());
            b.setC(m.getC());
            if(m.getFromPoints()!=null){
                for (Point p : m.getFromPoints()) {
                    b.addFrom(Game.Point.newBuilder().setX(p.getX()).setY(p.getY()));
                }
            }
            list.add(b.build());
        }
        return list;
    }
    public List<Game.PayLine> toPayLines(){
        List<Game.PayLine> list = new ArrayList<>();
        for (Rewardline m : lineMap.values()) {
            Game.PayLine.Builder b= Game.PayLine.newBuilder();
            b.setLineId(m.getLineId()).setGold(m.getGold()).setRate(m.getRate()).setHighC(m.getSpecialC());
            for (SlotModel p : m.getPoints()) {
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
