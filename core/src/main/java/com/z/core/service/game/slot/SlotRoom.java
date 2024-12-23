package com.z.core.service.game.slot;


import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.game.Round;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.*;
import com.z.model.bo.user.User;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import com.z.model.type.PosType;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;
/**
 * 房间
 */
public class SlotRoom extends SuperRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected CSlotService service;
    protected CPaylineService paylineService;
    protected WalletBizService walletBizService;
    protected CCfgBizService cfgBizService;
    /**
     * 选择的所有符号
     */
    protected Map<Integer, Slot> slots;

    protected List<Slot> allSlots;
    /**
     * 支付线
     */
    protected Map<Integer,Payline> lines;

    /**
     * 中奖的集合
     */
    protected List<RewardPayline> rewardPaylines = new ArrayList<>();

    protected int COL_SIZE=5,ROW_SIZE=3;

    protected long betGold;

    /**
     * 池子里所有的符号
     */
    protected Table<Integer,Integer,SlotModel> board;
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
     * 中奖的支付线
     */
    protected List<Game.PayLine> payLines = new ArrayList<>();
    /**
     * 所有的坐标符号
     */
    protected  List<Game.Spot> spots = new ArrayList<>();



    public SlotRoom(CRoom cRoom,long uid) {
        super(cRoom,uid);
        service = SpringContext.getBean(CSlotService.class);
        paylineService = SpringContext.getBean(CPaylineService.class);
        walletBizService = SpringContext.getBean(WalletBizService.class);
        cfgBizService = SpringContext.getBean(CCfgBizService.class);
        slots = new HashMap<>();
        allSlots = new ArrayList<>();
        lines = new HashMap<>();
        board = HashBasedTable.create();

    }

    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
        lines =  paylineService.getMap(gameType);
        Map<Integer, List<CSlot>> map = service.getMap(gameType);
        for (List<CSlot> list : map.values()) {
            for (CSlot slot : list) {
                int k = slot.getSymbol();
                Slot st = slots.getOrDefault(k, new Slot(slot.getW1()));
                slots.putIfAbsent(k, st);
                BeanUtils.copyProperties(slot, st);
                st.setK(slot.getSymbol());
                st.setPosType(PosType.getType(slot.getPosType()));
                st.setBaida(slot.isBaida());
                if(StringUtils.isNotEmpty(slot.getPos())){
                    String[] ss = slot.getPos().split(",");
                    for (String s : ss) {
                        st.addPos(Integer.parseInt(s));
                    }
                }
            }
        }
        allSlots.addAll(slots.values());

    }
    /**
     * 生成符号
     */
    public void generate() {
        board.clear();
        List<SlotModel>  origins = new ArrayList<>();
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                Slot slot = random(slots, i);
                SlotModel model = SlotModel.builder().type(slot.getK()).x(i).y(j).gold(slot.isGold()).build();
                board.put(model.getX(),model.getY(),model);
                if (isOrigin(model.getType())){
                    origins.add(model);
                }
            }
        }
        for (SlotModel m : origins) {
            int x = m.getX();
            int y = m.getY();
            m.setChangeType(getChangeType());
            SlotModel m1 =  board.get(x-1,y);
            if(m1!=null){
                m1.setChangeType(getChangeType());
            }
            m1 =  board.get(x+1,y);
            if(m1!=null){
                m1.setChangeType(getChangeType());
            }
            m1 =  board.get(x,y-1);
            if(m1!=null){
                m1.setChangeType(getChangeType());
            }
            m1 =  board.get(x,y+1);
            if(m1!=null){
                m1.setChangeType(getChangeType());
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
        rewardPaylines.clear();
        freeC = 0;
        free = false;
        rewardGold = 0L;
        rate = 0;
        spots.clear();
        highC = 0;
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
        log.info(sj.toString());
        var roundCheck = round.bet(uid, 0, gold, free);
        if (!roundCheck.isOk()) {
            log.error("roundCheck fail");
            return roundCheck;
        }
        User user = UserService.ins.get(uid);
        if (free) {
            gold = user.getFreeBetGold();
            sj.add("freeBet:" + user.getFreeBetGold());
        }
        this.free = free;
        betGold = gold / SlotCommon.BASE;
        sj.add("betGold:" + betGold);
        //生成符号
        log.info("生成------>:" + round.getId());
        generate();
        print();
        spots = toSpots();
        checklines();
        payLines = toPayLines();
        for (Game.PayLine payLine : payLines) {
            rewardGold += payLine.getGold();
            rate += payLine.getRate();
//            highC += payLine.getHighC();
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
        List<RewardPayline> rewardlines = new ArrayList<>();
        for (Payline payline : lines.values()) {
            RewardPayline line = checkLine(payline);
            if (line == null) continue;
            rewardlines.add(line);
        }
        long base = getBetGold();
        for (RewardPayline line : rewardlines) {
            CSlot cSlot = service.get(gameType, line.getK(), line.getPoints().size());
            if (cSlot == null) continue;
            line.setRate(cSlot.getRate());
            if (!poolLine(line)) {
                line.setGold(base * line.getRate());
            }
            line.setSpecialC(cSlot.getC1());
            rewardPaylines.add(line);
            if(cSlot.getFreeMax()>0){
                freeC += RandomUtil.randomInt(cSlot.getFree(),cSlot.getFreeMax()+1);
            }else{
                freeC += cSlot.getFree();
            }
            highC +=cSlot.getC1();
            log.info(line.toString());
        }
    }
    /**
     * 检查一条线
     * @param line
     * @return ()
     */
    public RewardPayline checkLine(Payline line){
        RewardPayline payline = null;
        for (Point p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            log.info("x:"+x+" y:"+p.getY() +"------>"+m);
            int type = m.getChangeType()>0 ?m.getChangeType():m.getType();
            if(!isSpecialType(type)) continue;
            if(payline==null){
                payline =new RewardPayline(type,line.getLineId());
            }
            payline.addSpecicalC();
            payline.addPoint(p);
        }

        for (Point p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            int type = m.getType();
            if(payline==null){
                payline =new RewardPayline(type,line.getLineId());
                payline.addPoint(p);
            }else if (isSame(x,type,payline.getK())){
                payline.addPoint(p);
            }else {
                break;
            }
        }
        return payline;
    }

    /**
     *
     * @return
     */
    public boolean isSpecialType(int type){
        return false;
    }

    /**
     * 获取下注金额 比例减少后的
     * @return
     */
    public long getBetGold() {
        return betGold / cfgBizService.getBB_XML_bet_base();
    }

    public boolean isPool(int type) {
        return type == CommonGame.LINE9.L9_BOX_VALUE;
    }

    /**
     * 奖池支付线处理
     */
    public boolean poolLine(RewardPayline line) {
        return false;
    }
    /**
     * 判断是否相同的符号
     * @param i
     * @param k1
     * @param k2
     * @return
     */

    public boolean isSame(int i, int k1, int k2) {
        if(k1 ==k2){
            return true;
        }
        CSlot slot1 = service.get(gameType,k1).get(0);
        CSlot slot2 = service.get(gameType,k2).get(0);

        if(slot1.isBaida() || slot2.isBaida()){
            return true;
        }

        return false;
    }
    public Slot random (Map < Integer, Slot > slots, int i){
        Set<Integer> rewardSymbols = new HashSet<>();
        for (RewardPayline line : rewardPaylines) {
            rewardSymbols.add(line.getK());
        }
        return SlotCommon.ins.random(gameType,board,slots, rewardSymbols, i);
    }
    public void print () {
        SlotCommon.ins.print(board,gameType,roomType,id,uid);
    }
    public List<Game.Spot> toSpots(){
        List<Game.Spot> list = new ArrayList<>(30);
        for (SlotModel m : board.values()) {
            Game.Spot.Builder b = Game.Spot.newBuilder();
            b.setSymbol(m.getType()).setX(m.getX()).setY(m.getY()).setChangeType(m.getChangeType());
            list.add(b.build());
        }
        return list;
    }
    public List<Game.PayLine> toPayLines(){
        List<Game.PayLine> list = new ArrayList<>();
        for (RewardPayline m : rewardPaylines) {
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
