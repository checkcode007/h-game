package com.z.core.service.game.game;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.game.slot.CPaylineService;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.BetParam;
import com.z.model.bo.slot.Payline;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.type.PosType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class SuperRoom implements IRoom{
    private static final Log log = LogFactory.getLog(SuperRoom.class);
    protected CSlotService service;
    protected CPaylineService paylineService;
    protected WalletBizService walletBizService;
    /**
     * 房间id
     */
    protected long id;
    /**
     * 房间配置id
     */
    protected int cfgId;
    /**
     * 游戏类型
     */
    protected CommonGame.GameType gameType;
    /**
     * 房间类型
     */
    protected CommonGame.RoomType roomType;
    /**
     * 支付线
     */
    protected Map<Integer, Payline> lines;
    /**
     * 池子里所有的符号
     */
    protected Table<Integer,Integer, SlotModel> board;
    /**
     * 当前人数
     */
    protected int curC;
    /**
     * 最大人数
     */
    protected int maxC;
    /**
     *  门槛-最低入场资金
     */
    protected long minBalance;

    /**
     * 当前轮
     */
    protected IRound curRound;
    /**
     * 选择的所有符号
     */
    protected Map<Integer, Slot> slots;

    protected List<Slot> allSlots;
    /**
     * 奖池倍率
     */
    protected Map<Integer,Integer> radioMap = new HashMap<>();

    protected Map<Long, IRound> roundMap = new ConcurrentHashMap<>();
    protected AtomicInteger roundIndex = new AtomicInteger(0);

    static AtomicLong  atomicLong = new AtomicLong(0);
    protected long uid;

    //col 第几列 row 第几排
    protected int COL_SIZE=5,ROW_SIZE=3;
    /**
     * 下注比值
     */
    protected int base=10;

    protected long betGold;

    protected int betMin;
    protected int betMax;
    protected User user;

    protected BetParam param;


    public SuperRoom(CRoom cRoom,long uid) {
        this.gameType = CommonGame.GameType.forNumber(cRoom.getGameType());
        this.roomType = CommonGame.RoomType.forNumber(cRoom.getType());
        this.id = atomicLong.incrementAndGet();
        this.uid = uid;
        this.cfgId= cRoom.getId();
        this.maxC =cRoom.getMaxPlayers();
        this.minBalance = cRoom.getMinBalance();
        base = cRoom.getBase();
        betMin = cRoom.getBetMin();
        betMax = cRoom.getBetMax();
        slots = new HashMap<>();
        allSlots = new ArrayList<>();
        lines = new HashMap<>();
        board = HashBasedTable.create();
        service = SpringContext.getBean(CSlotService.class);
        paylineService = SpringContext.getBean(CPaylineService.class);
        walletBizService = SpringContext.getBean(WalletBizService.class);
    }


    @Override
    public void init(CRoom cRoom) {
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
                st.setOnly(slot.isOnly());
                if(st.getRate1()<1 || slot.getRate()<st.getRate1()){
                    st.setRate1(slot.getRate());
                }
                if(st.getRate2()<1 || slot.getRate()>st.getRate2()){
                    st.setRate2(slot.getRate());
                }
                if(StringUtils.isNotEmpty(slot.getPos())){
                    String[] ss = slot.getPos().split(",");
                    for (String s : ss) {
                        st.addPos(Integer.parseInt(s));
                    }
                }
            }
        }
        allSlots.addAll(slots.values());
        lines =  paylineService.getMap(gameType);
        param = new BetParam();
        param.setUid(uid);
        user = UserService.ins.get(uid);
    }

    public void initParam(){
        Wallet wallet = WalletService.ins.get(uid);
        param.setGameType(gameType);
        param.setUid(uid);
        param.setState(user.getSlotState().getK());
        param.setWinC(wallet.getWins());
        param.setTotalC(wallet.getBetC());
    }
    @Override
    public MsgResult check(long uid,long curGold) {
        if(curC>maxC){
            return new MsgResult("房间已满");
        }
        if(curGold<minBalance){
            return new MsgResult("门槛条件不满足");
        }
        return new MsgResult(true);
    }

    public boolean betCheck(long uid,long gold) {
        if(gold<betMin || gold>betMax){
            return false;
        }
        return true;
    }
    @Override
    public MsgResult enter(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        log.info(sj.toString());
        Wallet wallet = WalletService.ins.get(uid);
        if(wallet == null ){
            log.error(sj.add("wallet null").toString());
            return new MsgResult("金额不足");
        }
        MsgResult ret = check(uid,wallet.getGold());
        if(!ret.isOk()){
            log.error(sj.add("check fail").toString());
            return ret;
        }
        if(!UserService.ins.enter(uid,gameType,roomType,cfgId,id)){
            log.error(sj.add("user change cache fail").toString());
            return ret;
        }
        this.curC++;
        MsgResult afterRet = afterEnter(uid);
        if(!afterRet.isOk()){
            log.error(sj.add("after fail:"+afterRet.getMessage()).toString());
            return ret;
        }

        return new MsgResult(true);
    }

    @Override
    public MsgResult afterEnter(long uid) {
        log.info("uid:"+uid);
        return new MsgResult(true);
    }

    @Override
    public MsgResult out(long uid) {
        if(curRound!=null){
            curRound.out(uid);
        }
        user = null;
        UserService.ins.out(uid);
        param = null;
        return new MsgResult(true);
    }

    /**
     * 结算-根据下注用户
     * @return
     */
    public MsgResult settle(long uid){
        return new MsgResult(true);
    }
    /**
     * 结算
     * @return
     */
    public MsgResult settle(){
        return new MsgResult(true);
    }

    /**
     * 判断是否相同的符号
     * 百搭不能替换scatter ，bonus
     *
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
        boolean b1 = slot1.isBonus()|| slot1.isScatter();
        boolean b2 = slot2.isBonus()|| slot2.isScatter();
        if(slot1.isBaida() && !b2){
            return true;
        }
        if(slot2.isBaida() && !b1){
            return true;
        }
        return false;
    }

    public void update(long now){

    }


    /**
     * 获取下注金额 比例减少后的
     * @return
     */
    public long getBetGold() {
        return betGold / base;
    }

    @Override
    public int getRadio(int type) {
        return radioMap.getOrDefault(type,0);
    }
    @Override
    public boolean isFull(long id) {
        return false;
    }

    @Override
    public boolean isIDle(long id) {
        return false;
    }

    public int getCurC() {
        return curC;
    }

    public int getMaxC() {
        return maxC;
    }

    public long getMinBalance() {
        return minBalance;
    }

    @Override
    public long getId() {
        return id;
    }

    public int getCfgId() {
        return cfgId;
    }

    public CommonGame.GameType getGameType() {
        return gameType;
    }

    public CommonGame.RoomType getRoomType() {
        return roomType;
    }

    public long getUid() {
        return uid;
    }

    public int getBase() {
        return base;
    }

    public int getBetMin() {
        return betMin;
    }

    public int getBetMax() {
        return betMax;
    }
}
