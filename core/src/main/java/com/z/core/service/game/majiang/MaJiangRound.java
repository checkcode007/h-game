package com.z.core.service.game.majiang;

import com.google.common.collect.Table;
import com.z.core.service.game.clear.ClearRound;
import com.z.core.service.game.slot.SlotCommon;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.slot.Goal;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.Wallet;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class MaJiangRound extends ClearRound {
    private static final Log log = LogFactory.getLog(MaJiangRound.class);

    public MaJiangRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType, int base) {
        super(id, gameType, roomType, base);
    }

    /**
     * 生成符号
     */
    //todo 金色符号不出现在第一列
    @Override
    public void generate() {
        board.clear();
        initParam();
        for (int i = 0; i < 5; i++) {
            int size = 5;
            if (i == 0 || i == 4) {
                size = 4;
            }
            for (int j = 0; j < size; j++) {
                param.setX(i);
                Slot slot = random(slots);
                SlotModel model = SlotCommon.ins.toModel(slot, i, j);
                board.put(model.getX(), model.getY(), model);
            }
        }
    }

    /**
     * 检测
     *
     * @return
     */
    @Override
    public void check() {
        checkCommon();
        checkBonus();
    }

    /**
     * 检测普通符号
     */
    @Override
    public void checkCommon() {
        log.info("checkCommon---------->start");
        // 1. 遍历行列位置和对应的值
        Map<Integer, List<SlotModel>> firstMap = new HashMap<>();
        Collection<SlotModel> row_1 = board.row(0).values();
        for (SlotModel m : row_1) {
            if (m == null) continue;
            if (m.isBonus()) continue;
            List<SlotModel> list = firstMap.getOrDefault(m.getK(), new ArrayList<>());
            firstMap.putIfAbsent(m.getK(), list);
            list.add(m);
        }
        for (int k : firstMap.keySet()) {
            List<SlotModel> lianjie = new ArrayList<>(firstMap.get(k));
            //连接各数，
            int c = 1;
            for (int i = 1; i < COL_SIZE; i++) {
                //每列加1
                Collection<SlotModel> list = board.row(i).values();
                //每列相同的汇总
                boolean b_col_had = false;
                for (SlotModel e : list) {
                    if (e.getK() == k || e.isBaida()) {//百搭处理
                        lianjie.add(e);
                        b_col_had = true;
//                        log.info(" col:" + i + " type:" + k + "->" + e);
                    }
                }
                if (b_col_had) {
                    c++;
                } else {
                    break;
                }
            }
            log.info("k------->:" + k + " c--->" + c);
            if (c < 2) continue;
            //移除匹配的
            CSlot slot = service.get(gameType, k, c);
            if (slot != null) {
                List<SlotModel> toRemove = new ArrayList<>();
                for (var m : lianjie) {
                    int x = m.getX();
                    for (SlotModel e : board.row(x).values()) {
                        if (e.getK() == k || e.isBaida()) {
                            toRemove.add(e);
                        }
                    }
                    log.info("del--->" + x + "--->" + k + "-->del-->" + m);
                }
                // 进行删除操作
                Map<Integer, Integer> colCMap = new HashMap();//每排个数
                for (var e : toRemove) {
                    board.remove(e.getX(), e.getY());
                    if (!e.isBaida() && e.isGold()) {
                        CSlot wildSlot = service.getWild(gameType);
                        Slot s = slots.get(wildSlot.getSymbol());
                        SlotModel baida = SlotCommon.ins.toModel(s, e.getX(), e.getY());
                        board.put(baida.getX(), baida.getY(), baida);
                    }
                    colCMap.put(e.getX(), colCMap.getOrDefault(e.getX(), 0) + 1);
                }
                int rate = 1;
                for (Integer v : colCMap.values()) {
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
     * 检测特殊符号-胡
     */
    @Override
    public void checkBonus() {
        CSlot slot = service.getBonus(gameType);
        int type = slot.getSymbol();
        int c = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (Table.Cell<Integer, Integer, SlotModel> cell : board.cellSet()) {
            int x = cell.getRowKey();
            int y = cell.getColumnKey();
            SlotModel m = cell.getValue();
            if (m == null) continue;
            if (m.getK() != type) continue;
            c++;
            map.put(x, y);
        }
        CSlot cslot = service.get(gameType, type, c);
        if (cslot == null) {
            return;
        }
        Goal goal = new Goal(type, c, 0, cslot.getFree());
        delMap.put(type, goal);
        map.forEach((x, y) -> {
            SlotModel model = board.remove(x, y);
            if (model != null) {
                goal.addPoint(model);
                log.info("del--->" + x + "--->" + type + "-->del-->" + model);
            }
        });
    }

    // 消除符号并让下方的符号前移‘
    @Override
    public void move() {
        Wallet wallet = WalletService.ins.get(uid);
        moveForward();
        Map<Integer, Integer> huMap = new HashMap<>();
        for (int x = 0; x < 5; x++) {
            int size = x == 0 || x == 4 ? 4 : 5;
            for (int y = 0; y < size; y++) {
                SlotModel m = board.get(x, y);
                if (m == null) continue;
                if (!m.isBonus()) continue;
                huMap.put(x, huMap.getOrDefault(x, 0) + 1);
            }
        }
        for (int x = 0; x < 5; x++) {
            int size = x == 0 || x == 4 ? 4 : 5;
            for (int y = 0; y < size; y++) {
                SlotModel m = board.get(x, y);
                if (m == null) {
                    Slot slot;
                    param.setX(x);
                    slot = random(slots);
                    SlotModel model = SlotCommon.ins.toModel(slot, x, y);
                    log.info("x:-->" + x + "----->" + model);
                    board.put(x, y, model);
                }
            }
        }
        //免费次数第三列都是金色牌处理
        col3Gold();
    }

    @Override
    public void moveForward() {
        super.moveForward();
    }

    @Override
    public void moveForward(int x) {
        int rowSize = ROW_SIZE;
        if (x == 0 || x == 4) {
            rowSize = 4;
        }
        // 获取该列的所有SlotModel
        List<SlotModel> list = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            SlotModel m = board.get(x, i);
            list.add(m);
        }
        // 去除 null 值
        list.removeIf(item -> item == null);
        // 将非 null 的元素填充到原位置
        for (int i = 0; i < rowSize; i++) {
            if (i < list.size()) {
                board.put(x, i, list.get(i));
            } else {
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
            if (m.isGold() || m.isBonus() || m.isBaida())
                continue;
            m.setGold(true);
        }
    }


    @Override
    public String toString() {
        return "round{" +
                "id=" + id +
                '}';
    }

}
