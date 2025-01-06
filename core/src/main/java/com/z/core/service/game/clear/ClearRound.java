package com.z.core.service.game.clear;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.game.game.SuperRound;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotCommon;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Goal;
import com.z.model.bo.slot.Slot;
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

public class ClearRound extends SuperRound {
    protected Logger log = LoggerFactory.getLogger(getClass());
    protected CSlotService service;
    WalletBizService walletService;
    /**
     * 选择的所有符号
     */
    protected Map<Integer, Slot> slots;
    /**
     * 下注基数比值
     */
    public static final int BASE = 20;
    /**
     * 池子里所有的符号
     */
    protected Table<Integer, Integer, SlotModel> board;
    /**
     * 是否免费轮
     */
    protected boolean free;

    protected Map<Integer, Goal> delMap;
    /**
     * 连续中奖倍率
     */
    protected List<Integer> rowRadio = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    /**
     * 赢的总次数
     */
    protected int winC = 0;
    /**
     * 连续赢的次数
     */
    protected int lianxuC = 0;

    protected int wildIndex = 0;

    public ClearRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        super(id, gameType, roomType);
        service = SpringContext.getBean(CSlotService.class);
        walletService = SpringContext.getBean(WalletBizService.class);
        board = HashBasedTable.create();
        delMap = new HashMap<>();
    }

    @Override
    public void init(long uid, int rowSize, int colSize) {
        super.init(uid, rowSize, colSize);
        log.info("colsize-->" + COL_SIZE + " rowsize-->" + ROW_SIZE);
    }

    public void setWildIndex(int wildIndex) {
        this.wildIndex = wildIndex;
    }

    public void init(Map<Integer, Slot> slots) {
        this.slots = new HashMap<>(slots);
    }

    /**
     * 生成符号
     */
    public void generate() {
        board.clear();
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                Slot slot = random(slots, i);
                SlotModel model = SlotCommon.ins.toModel(slot, i, j);
                board.put(model.getX(), model.getY(), model);
            }
        }
    }

    @Override
    public MsgResult<Game.ClearGameMsg> bet(long uid, int type, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("type:" + type)
                .add("gold:" + gold).add("id:" + id).add("free:" + free);
        log.info(sj.toString());
        this.free = free;
        User user = UserService.ins.get(uid);
        if (free) {
            gold = user.getFreeBetGold();
            sj.add("freeBet:" + user.getFreeBetGold());
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

        Game.ClearGameMsg.Builder builder = Game.ClearGameMsg.newBuilder().setRoundId(id);
        //第一次检测============
        //免费次数判断
        int freeC = 0;
        int index = 0;
        long rewardGold = 0L;
        //消除情况下，多次判断
        check();
        while (!delMap.isEmpty()) {
            //倍率
            int rate = 0;
            int freeCout = 0;
            Game.Round.Builder b = Game.Round.newBuilder();
            for (Goal g : delMap.values()) {
                for (SlotModel p : g.getPoints()) {
                    b.addDelSpots(Game.Spot.newBuilder().setSymbol(p.getType()).setX(p.getX()).setY(p.getY())
                            .setGold(p.isGold()).build());
                }
                rate += g.getRate();
                freeCout += g.getFree();
                log.info("k:" + g.getK() + " c:" + g.getC() + " rate:" + g.getRate() + " free:" + free);
            }
            freeC += freeCout;
            int rowRate = rowRadio.get(index > rowRadio.size() - 1 ? rowRadio.size() - 1 : index);
            rowRate = free ? rowRate : rowRate * 2;
            long addGold = (gold / BASE) * rate * rowRate;
            StringJoiner sj1 = new StringJoiner(",");
            sj1.add("r:" + rate).add("rowR:" + rowRate).add("index:" + index).add("baseG:" + (gold / BASE)).add("addG:" + addGold).add("freeC:" + freeCout);
            log.info(sj1.toString());
            rewardGold += b.getGold();
            b.addAllSpots(allToModel(preBoard));
            builder.addRounds(b.setGold(addGold).setRowRadio(rowRate).build());
            winC++;
            index++;
            lianxuC++;
            if (wildIndex > 0) {
                break;
            }
            move();
            preBoard.clear();
            preBoard.putAll(board);
            log.info("消除后的牌面：");
            print(board);
            reset();
            //下一轮检测
            check();

        }
        sj.add("index:" + index).add("gold1:" + rewardGold);
        if (rewardGold > 0) {
            walletService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, rewardGold, gameType, roomType);
        } else {
            lianxuC = 0;
        }
        if (free) {
            user.subFree();
        } else {
            if (freeC > 0) {
                user.setFreeBetGold((int) gold);
            } else if (user.getFreeBetGold() > 0) {
                user.setFreeBetGold(0);
            }
        }
        user.addFree(freeC);
        builder.setAddFreeC(freeC).setTotalFreeC(user.getFree());
        builder.setWildIndex(wildIndex).setGold(rewardGold);
        builder.setLeaveGold(WalletService.ins.get(uid).getGold());

        if (wildIndex < 1) { //不是冰球大wild 显示最后一轮
            builder.addAllLastView(allToModel(board));
        } else if (index < 1) {//冰球大wild 没有消除 显示最后一轮展示
            builder.addAllLastView(allToModel(board));
        }
        log.info(sj.add("success").toString());
        return new MsgResult(builder.build());
    }

    public void reset() {
        delMap.clear();
    }

    /**
     * 检测
     *
     * @return
     */
    public void check() {
        checkCommon();
        checkBonus();
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
            Slot slot = slots.get(m.getType());
            if (slot.isBonus()) continue;
            List<SlotModel> list = firstMap.getOrDefault(m.getType(), new ArrayList<>());
            firstMap.putIfAbsent(m.getType(), list);
            list.add(m);
        }
        for (int k : firstMap.keySet()) {
            List<SlotModel> lianjie = new ArrayList<>(firstMap.get(k));
            int c = 1;
            for (int i = 1; i < COL_SIZE; i++) {
                //每列加1
                Collection<SlotModel> list = board.row(i).values();
                //每列相同的汇总
                boolean b_col_had = false;
                for (SlotModel e : list) {
                    Slot slot = slots.get(e.getType());
                    if (e.getType() == k || slot.isBaida()) {//百搭处理
                        c++;
                        lianjie.add(e);
                        b_col_had = true;
                        log.info(" col:" + i + " type:" + k + "->" + e);
                        break;
                    }else {
                        break;
                    }
                }
                if (!b_col_had) break;
            }
            if (c < 2) continue;
            //移除匹配的
            CSlot slot = service.get(gameType, k, c);
            if (slot != null) {
                List<SlotModel> toRemove = new ArrayList<>();
                for (var m : lianjie) {
                    int x = m.getX();
                    for (SlotModel e : board.row(x).values()) {
                        if (e.getType() == k) {
                            toRemove.add(e);
                        } else if (slots.get(e.getType()).isBaida()) {
                            toRemove.add(e);
                        }
                    }
                    log.info("del--->" + x + "--->" + k + "-->del-->" + m);
                }
                // 进行删除操作
                Map<Integer, Integer> rateMap = new HashMap();
                for (var e : toRemove) {
                    board.remove(e.getX(), e.getY());
                    if (!e.isBaida() && e.isGold()) {
                        CSlot wildSlot = service.getWild(gameType);
                        Slot s = slots.get(wildSlot.getType());
                        SlotModel baida = SlotCommon.ins.toModel(s, e.getX(), e.getY());
                        board.put(baida.getX(), baida.getY(), baida);
                    }
                    rateMap.put(e.getX(), rateMap.getOrDefault(e.getX(), 0) + 1);
                }
                int rate = 1;
                for (Integer v : rateMap.values()) {
                    rate = rate * v;
                }
                rate = rate * slot.getRate();
                Goal goal = new Goal(k, c, rate, slot.getFree());
                goal.addPoint(lianjie);
                delMap.put(k, goal);
            }
        }
    }

    /**
     * 检测bonus
     */
    public void checkBonus() {
        int k = slots.values().stream().filter(Slot::isBonus).findFirst().map(Slot::getK).orElse(0);
        if (k < 1) return;
        int c = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (Table.Cell<Integer, Integer, SlotModel> cell : board.cellSet()) {
            int x = cell.getRowKey();
            int y = cell.getColumnKey();
            SlotModel m = cell.getValue();
            if (m == null) continue;
            if (m.getType() != k) continue;
            c++;
            map.put(x, y);
        }
        CSlot cslot = service.get(gameType, k, c);
        if (cslot == null) {
            return;
        }
        Goal goal = new Goal(k, c, 0, cslot.getFree());
        delMap.put(k, goal);
        map.forEach((x, y) -> {
            SlotModel model = board.remove(x, y);
            if (model != null) {
                goal.addPoint(model);
                log.info("del--->" + x + "--->" + k + "-->del-->" + model);
            }
        });
    }

    // 消除符号并让下方的符号前移‘
    protected void move() {
        moveForward();
        for (int x = 0; x < COL_SIZE; x++) {
            for (int y = 0; y < ROW_SIZE; y++) {
                SlotModel m = board.get(x, y);
                if (m == null) {
                    Slot slot;
                    if (x == 1) {
                        slot = random(slots, x);
                    } else {
                        slot = random(slots, x);
                    }
                    SlotModel model = SlotCommon.ins.toModel(slot, x, y);
                    log.info("x:-->" + x + "----->" + model);
                    board.put(x, y, model);
                }
            }
        }
    }

    public void moveForward() {
        for (int i = 0; i < COL_SIZE; i++) {
            moveForward(i);
        }
        for (Table.Cell<Integer, Integer, SlotModel> cell : board.cellSet()) {
            int x = cell.getRowKey();
            int y = cell.getColumnKey();
            SlotModel m = cell.getValue();
            if (m == null) continue;
            if (m.getY() != y) m.setY(y);
        }
    }

    // 移动指定列中的数据x
    private void moveForward(int x) {
        // 获取该列的所有SlotModel
        List<SlotModel> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            SlotModel m = board.get(x, i);
            list.add(m);
        }
        // 去除 null 值
        list.removeIf(item -> item == null);
        // 将非 null 的元素填充到原位置
        for (int i = 0; i < ROW_SIZE; i++) {
            if (i < list.size()) {
                board.put(x, i, list.get(i));
            } else {
                board.remove(x, i);
            }
        }

    }

    public Slot random(Map<Integer, Slot> slots, int i) {
        return SlotCommon.ins.random(gameType, board, slots, delMap.keySet(), i, free,user);
    }

    public void print(Table<Integer, Integer, SlotModel> board) {
        SlotCommon.ins.print(board, gameType, roomType, 0, 0);
    }

    public List<Game.Spot> allToModel(Table<Integer, Integer, SlotModel> board) {
        return SlotCommon.ins.allToModelTable(board);
    }

    @Override
    public String toString() {
        return "round{" +
                "id=" + id +
                '}';
    }

}
