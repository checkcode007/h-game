package com.z.core.service.game.majiang;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.common.util.JsonUtils;
import com.z.core.service.game.game.SuperRound;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.majiang.Goal;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.User;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MaJiangRound extends SuperRound {
    protected Logger log = LoggerFactory.getLogger(getClass());
    CSlotService service;
    WalletBizService walletService;
    /**
     * 选择的所有符号
     */
    Map<Integer, Slot> slots;
    /**
     * 下注基数比值
     */
    public static final int BASE = 20;
    /**
     * 池子里所有的符号
     */
    private Table<Integer, Integer, SlotModel> board;
    /**
     * 是否免费轮
     */
    boolean free;

    Map<CommonGame.MJ,Goal> delMap;

    List<Integer> rowRadio = Arrays.asList(1,2,3,5);
    /**
     * 赢的总次数
     */
    int winC = 0;
    /**
     * 连续赢的次数
     */
    int lianxuC = 0;


    public MaJiangRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        super(id, gameType, roomType);
        service = SpringContext.getBean(CSlotService.class);
        walletService = SpringContext.getBean(WalletBizService.class);
        board = HashBasedTable.create();
        delMap = new HashMap<>();
    }

    public void init(Map<Integer, Slot> slots) {
        this.slots = new HashMap<>(slots);
    }

    /**
     * 生成符号
     */
    //todo 金色符号不出现在第一列
    public void generate() {
        board.clear();
        for (int i = 0; i < 5; i++) {
            int size = 5;
            if (i == 0 || i == 4) {
                size = 4;
            }
            boolean hu = false;
            for (int j = 0; j < size; j++) {
                Slot slot = random(slots, i, hu);
                SlotModel model = SlotModel.builder().type(slot.getK()).x(i).y(j).gold(slot.isGold()).build();
                if (slot.getK() == CommonGame.MJ.HU.getNumber()) {
                    hu = true;
                }
                board.put(model.getX(), model.getY(), model);
            }

        }
    }

    @Override
    public MsgResult<Game.MjBetMsg> bet(long uid, int type, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("type:" + type)
                .add("gold:" + gold).add("id:" + id).add("free:" + free);
        log.info(sj.toString());
        this.free = free;
        User user = UserService.ins.get(uid);
        if(free){
            gold = user.getFreeBetGold();
            sj.add("freeBet:"+user.getFreeBetGold());
        }
        //下注
        MsgResult ret = super.bet(uid, type, gold, free);
        if (!ret.isOk()) {
            log.error(sj.add("bet fail").toString());
            return ret;
        }
        //生成第一次符号==========
        generate();
        log.info("bet下注前:" + id);
        print(board);
        Table<Integer, Integer, SlotModel> preBoard = HashBasedTable.create();
        preBoard.putAll(board);

        Game.MjBetMsg.Builder builder = Game.MjBetMsg.newBuilder().setRoundId(id);
        //第一次检测============
        //免费次数判断
        int freeC = 0;
        int index = 0;
        long rewardGold = 0L;
        //消除情况下，多次判断
        check();
        while (!delMap.isEmpty()) {
            Game.MjOne.Builder b = Game.MjOne.newBuilder();
            //倍率
            int rate = 0;
            int freeCout = 0;
            for (Goal g: delMap.values()) {
                for (SlotModel p : g.getPoints()) {
                    b.addGoals(Game.MjModel.newBuilder().setType(CommonGame.MJ.forNumber(p.getType())).setX(p.getX()).setY(p.getY()).setGold(p.isGold()).build());
                }
                rate+=g.getRate();
                freeCout+=g.getFree();
                log.info("k:" + g.getMj() +" c:"+g.getC() +" rate:"+g.getRate() +" free:"+free);
            }

            freeC += freeCout;
            int rowRate = rowRadio.get(index>rowRadio.size()-1?rowRadio.size()-1:index);
            rowRate = free?rowRate:rowRate*2;
            long addGold = (gold / BASE) * rate *rowRate;
            StringJoiner sj1 = new StringJoiner(",");
            sj1.add("r:"+rate).add("rowR:"+rowRate).add("index:"+index).add("baseG:"+(gold/BASE)).add("addG:"+addGold);
            log.info(sj1.toString());
            b.setGold(addGold);
            b.setRowRadio(rowRate);
            rewardGold += b.getGold();
            b.addAllMjs(allToModel(preBoard));
            builder.addMjOnes(b.build());
            preBoard.clear();
            preBoard.putAll(board);
            winC++;
            if (freeCout>0) {
                break;
            }
            move();
            log.info("消除后的牌面：");
            print(board);
            reset();
            //下一轮检测
            check();
            index++;
            lianxuC++;
        }

        sj.add("index:"+index).add("gold1:"+rewardGold);

        if (rewardGold > 0) {
            walletService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, rewardGold, gameType, roomType);
        }else {
            lianxuC = 0;
        }
        if(free){
            user.subFree();
        }else{
            user.addFree(freeC);
            if(freeC>0){
                user.setFreeBetGold((int)gold);
            }else if(user.getFreeBetGold()>0){
                user.setFreeBetGold(0);
            }
        }

        builder.setFree(freeC > 0);
        builder.setAddFreeC(freeC);
        builder.setTotalFreeC(user.getFree());
        builder.setGold(rewardGold).addAllMjs(allToModel(board));
        builder.setLeaveGold(WalletService.ins.get(uid).getGold());
        log.info(sj.add("success").toString());
        return new MsgResult(builder.build());
    }
    public void reset(){
        delMap.clear();
    }
    /**
     * 检测
     *
     * @return
     */
    public void check() {
        checkCommon();
        checkSpecial();
    }


    /**
     * 检测普通符号
     */
    public void checkCommon() {
        // 1. 遍历行列位置和对应的值
        Map<Integer, List<SlotModel>> firstMap = new HashMap<>();
        Collection<SlotModel> row_1 = board.row(0).values();
        for (SlotModel m : row_1) {
            if (m == null) continue;
            if (m.getType() == CommonGame.MJ.HU.getNumber()) continue;
            List<SlotModel> list = firstMap.getOrDefault(m.getType(), new ArrayList<>());
            firstMap.putIfAbsent(m.getType(), list);
            list.add(m);
        }
        for (int k : firstMap.keySet()) {
            CommonGame.MJ type = CommonGame.MJ.forNumber(k);
            List<SlotModel> lianjie = new ArrayList<>(firstMap.get(k));
            int c = 1;
            for (int i = 1; i < 5; i++) {
                //每列加1
                Collection<SlotModel> list = board.row(i).values();
                //每列相同的汇总
                boolean b_col_had = false;
                for (SlotModel e : list) {
                    if (e.getType() == k || e.getType() == CommonGame.MJ.BAIDA.getNumber()) {//百搭处理
                        c++;
                        lianjie.add(e);
                        b_col_had = true;
                        log.info(" col:" + i + " type:" + type.getNumber() + "->" + e);
                        break;
                    }
                }
                if (!b_col_had) break;
            }
            if (c > 1) { //移除匹配的
                CSlot slot = service.get(gameType, type.getNumber(), c);
                if (slot != null) {
                    List<SlotModel> toRemove = new ArrayList<>();
                    for (var m : lianjie) {
                        int x = m.getX();
                        for (SlotModel e : board.row(x).values()) {
                            if (e.getType() == k || e.getType() == CommonGame.MJ.BAIDA.getNumber()) {
                                toRemove.add(e);
                            }
                        }
                        log.info("del--->"+x+"--->"+type + "-->del-->" + m);
                    }
                    // 进行删除操作

                    Map<Integer,Integer> rateMap = new HashMap();
                    for (var e : toRemove) {
                        board.remove(e.getX(), e.getY());
//                        delList.add(e);
                         if (e.getType() != CommonGame.MJ.BAIDA.getNumber() && e.isGold()) {
                            SlotModel baida = SlotModel.builder().type(CommonGame.MJ.BAIDA.getNumber()).x(e.getX()).y(e.getY()).build();
                            board.put(baida.getX(), baida.getY(),baida);
                        }
                        rateMap.put(e.getX(),  rateMap.getOrDefault(e.getX(),0)+1);
                    }
                    int rate =1;
                    for (Integer v : rateMap.values()) {
                        rate = rate*v;
                    }
                    rate= rate * slot.getRate();
                    Goal goal = new Goal(type,c,rate,slot.getFree());
                    goal.addPoint(lianjie);
                    delMap.put(type,goal);
                }
            }
        }
    }

    /**
     * 检测特殊符号-胡
     */
    public void checkSpecial() {
        CommonGame.MJ type = CommonGame.MJ.HU;
        int c = 0;
        Map<Integer,Integer> map = new HashMap<>();
        for (Table.Cell<Integer, Integer, SlotModel> cell : board.cellSet()) {
            int x = cell.getRowKey();
            int y = cell.getColumnKey();
            SlotModel m = cell.getValue();
            if(m == null) continue;
            if (m.getType() != type.getNumber()) continue;
            c++;
            map.put(x,y);
        }
        CSlot cslot = service.get(gameType,type.getNumber(),c);
        if(cslot == null){
            return;
        }
        Goal goal = new Goal(type,c,0,cslot.getFree());
        delMap.put(type,goal);
        map.forEach((x,y)->{
            SlotModel model = board.remove(x,y);
            if(model!=null){
                goal.addPoint(model);
                log.info("del--->"+x+"--->"+type + "-->del-->" + model);
            }
        });
    }

    // 消除符号并让下方的符号前移‘
    //todo 金色牌变成百搭
    private boolean move() {
        moveForward();
        boolean b = false;

        Map<Integer,Integer> huMap = new HashMap<>();
        for (int x = 0; x < 5; x++) {
            int size = x == 0 || x == 4 ? 4 : 5;
            for (int y = 0; y < size; y++) {
                SlotModel m = board.get(x, y);
                if (m == null) continue;
                if (m.getType() != CommonGame.MJ.HU.getNumber()) continue;
                huMap.put(x,huMap.getOrDefault(x,0)+1);
            }
        }
        for (int x = 0; x < 5; x++) {
            int size = x == 0|| x== 4?4:5;
            boolean hu = huMap.containsKey(x);
            for (int y = 0; y < size; y++) {
                SlotModel m =  board.get(x,y);
                if(m == null){
                    Slot slot;
                    if(x == 1){
                        slot = random(slots, x, hu);
                    } else {
                        slot = random(slots, x, hu);
                    }
                    SlotModel model = SlotModel.builder().type(slot.getK()).gold(slot.isGold())
                            .x(x).y(y).build();
                    log.info("x:-->"+x+"----->"+model);
                    board.put(x,y,model);
                    if(slot.getK() == CommonGame.MJ.HU.getNumber()){
                        hu = true;
                    }
                }
            }

        }
        //免费次数第三列都是金色牌处理
        col3Gold();

        return b;
    }

    public void moveForward() {
        for (int i = 0; i < 5; i++) {
            moveForward(i);
        }
        for (Table.Cell<Integer, Integer, SlotModel> cell : board.cellSet()) {
            int x = cell.getRowKey();
            int y = cell.getColumnKey();
            SlotModel m = cell.getValue();
            if (m == null) continue;
            if(m.getY()!= y)m.setY(y);
        }
    }
    // 移动指定列中的数据x
    private void moveForward( int x) {
        // 获取该列的所有SlotModel
        List<SlotModel> list = new ArrayList<>();
        int size = x==0|| x== 4?4:5;
        for (int i = 0; i < size; i++) {
            SlotModel m = board.get(x,i);
            list.add(m);
        }
        // 去除 null 值
        list.removeIf(item -> item == null);
        // 将非 null 的元素填充到原位置
        for (int i = 0; i < size; i++) {
            if (i < list.size()) {
                board.put(x, i, list.get(i));
            }else{
                board.remove(x, i);
            }
        }

    }

    /**
     * 第三列金色处理
     */
    public void col3Gold() {
        if (!free) return;
        for (SlotModel m : board.row(2).values()) {
            if (m.isGold()|| m.getType() == CommonGame.MJ.HU.getNumber() || m.getType() == CommonGame.MJ.BAIDA.getNumber()) continue;
            m.setGold(true);
        }
    }

    public Slot random(Map<Integer, Slot> slots, int i, boolean hu) {
        Set<Integer> goals = new HashSet<>();
        for (CommonGame.MJ mj : delMap.keySet()) {
            goals.add(mj.getNumber());
        }
        return MJCommon.ins.random(board,slots,goals, i, hu,free,winC,lianxuC);
    }

    public void print(Table<Integer, Integer, SlotModel> board) {
        MJCommon.ins.printTable(board);
    }

    public List<Game.MjModel> allToModel(Table<Integer, Integer, SlotModel> board) {
        return MJCommon.ins.allToModelTable(board);
    }

    @Override
    public String toString() {
        return "round{" +
                "id=" + id +
                '}';
    }

    public static void main(String[] args) {
        List<Game.MjModel> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(Game.MjModel.newBuilder().setType(CommonGame.MJ.B_2).setX(i).build());
        }
        System.err.println(JsonUtils.listObjToJson(Collections.singletonList(list)).replace("\\n", ""));
    }
}
