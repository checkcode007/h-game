package com.z.core.service.game.majiang;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.common.util.JsonUtils;
import com.z.common.util.SpringContext;
import com.z.core.service.game.game.SuperRound;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletBizService;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.Slot;
import com.z.model.bo.majiang.MjGoal;
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
import org.springframework.beans.BeanUtils;

import java.util.*;

public class MaJiangRound extends SuperRound {
    protected Logger log = LoggerFactory.getLogger(getClass());
    public static final int BASE = 20;
    private List<List<SlotModel<CommonGame.MJ>>> board;
    Map<Integer, Slot> slots = new HashMap<>();

    List<Slot> allSlots = new ArrayList<>();

    CSlotService service;
    WalletBizService walletService;

    public MaJiangRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        super(id,gameType,roomType);
        service = SpringContext.getBean(CSlotService.class);
        walletService = SpringContext.getBean(WalletBizService.class);
        board = new ArrayList<>();
        slots = new HashMap<>();
        init();
    }

    public void init() {
        Map<Integer, List<CSlot>> map = service.getMap(CommonGame.GameType.MAJIANG_2);
        for (List<CSlot> list : map.values()) {
            for (CSlot slot : list) {
                int k = slot.getSymbol();
                Slot s = slots.getOrDefault(k, new Slot());
                slots.putIfAbsent(k, s);
                BeanUtils.copyProperties(slot, s);
                s.setK(slot.getSymbol());
            }
        }
        allSlots.addAll(slots.values());
    }

    /**
     * 刷新麻将
     */
    public void generate() {
        board.clear();
        for (int i = 0; i < 5; i++) {
            int size = 5;
            if (i == 0 || i == 4) {
                size = 4;
            }
            List<SlotModel<CommonGame.MJ>> list = new ArrayList<>();
            boolean hu = false;
            for (int j = 0; j < size; j++) {
                Slot slot = MJCommon.ins.random(slots, null, null, i, hu);
                CommonGame.MJ mj = CommonGame.MJ.forNumber(slot.getK());
                SlotModel model = SlotModel.builder().type(mj).x(i).y(j).gold(slot.isGold()).build();
                list.add((model));
                if (mj == CommonGame.MJ.HU) {
                    hu = true;
                }
            }
            board.add(list);
        }
    }

    @Override
    public MsgResult<Game.MjBetMsg> bet(long uid, int type, long gold, boolean free) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("type:" + type)
                .add("gold:" + gold).add("id:" + id).add("free:" + free);
        //下注
        MsgResult ret = super.bet(uid, type, gold, free);
        if (!ret.isOk()) {
            log.error(sj.add("bet fail").toString());
            return ret;
        }
        generate();
        log.info("bet下注前:" + id);
        MJCommon.ins.print(board);
        List<List<SlotModel<CommonGame.MJ>>> preBoard = new ArrayList<>(board);
        Map<Integer, MjGoal> map = check();
        //免费次数判断
        int freeC = 0;
        for (MjGoal goal : map.values()) {
            CSlot cSlot = service.get(CommonGame.GameType.MAJIANG_2, goal.getMj().getNumber(), goal.getC());
            if (cSlot == null) continue;
            if (cSlot.getFree() < 1) continue;
            freeC = cSlot.getFree();
            break;
        }
        int index = 0;
        Game.MjBetMsg.Builder builder = Game.MjBetMsg.newBuilder().setRoundId(id);
        long rewardGold = 0L;
        while (!map.isEmpty()) {
            int rate = 0;
            Game.MjOne.Builder b = Game.MjOne.newBuilder();
            for (MjGoal v : map.values()) {
                for (SlotModel<CommonGame.MJ> p : v.getPoints()) {
                    b.addGoals( Game.MjModel.newBuilder().setType(p.getType()).setX(p.getX()).setY(p.getY()).setGold(p.isGold()).build());
                }
                CSlot cslot = service.get(CommonGame.GameType.MAJIANG_2, v.getMj().getNumber(), v.getC());
                rate += cslot.getRate();
            }
            b.setGold((gold / BASE) * rate);
            rewardGold += b.getGold();
            b.addAllMjs(MJCommon.ins.allToModel(preBoard));
            builder.addMjOnes(b.build());
            boolean moveB = move(map, true, free);
            preBoard.clear();
            preBoard.addAll(board);
            log.info("消除后的牌面：");
            MJCommon.ins.print(board);
            if (!moveB) {
                break;
            }
            map.clear();
            map = check();
            index++;
        }
        User user = UserService.ins.get(uid);
        if(rewardGold>0){
            walletService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD,uid,rewardGold, gameType,roomType);
        }
        builder.setFree(freeC > 0);
        builder.setAddFreeC(freeC);
        user.setFree(user.getFree() - 1);
        user.setFree(user.getFree() + freeC);
        builder.setTotalFreeC(user.getFree());
        builder.setGold(rewardGold).addAllMjs(MJCommon.ins.allToModel(board));
        builder.setLeaveGold(WalletService.ins.get(uid).getGold());
        log.info(sj.add("index:" + index).add("success").toString());
        return new MsgResult(builder.build());
    }

    /**
     * 检测
     *
     * @return
     */
    public Map<Integer, MjGoal> check() {
        Map<CommonGame.MJ, List<SlotModel<CommonGame.MJ>>> commons = new HashMap<>();
        Map<CommonGame.MJ, List<SlotModel<CommonGame.MJ>>> specials = new HashMap<>();
        List<SlotModel<CommonGame.MJ>> list = board.getFirst();
        for (SlotModel<CommonGame.MJ> model : list) {
            CommonGame.MJ type = model.getType();
            if (type == CommonGame.MJ.HU) {
                List<SlotModel<CommonGame.MJ>> tmpList = specials.getOrDefault(type, new ArrayList<>());
                specials.putIfAbsent(type, tmpList);
                tmpList.add(model);
            } else {
                List<SlotModel<CommonGame.MJ>> tmpList = commons.getOrDefault(type, new ArrayList<>());
                commons.putIfAbsent(type, tmpList);
                tmpList.add(model);
            }
        }

        Map<Integer, MjGoal> map = checkCommon(commons);
        Map<Integer, MjGoal> map1 = checkSpecial(specials);
        map.putAll(map1);
        checkReward(map);
        return map;
    }
    public Table<Integer,Integer,SlotModel<CommonGame.MJ>> getBaidas() {
        Table<Integer,Integer,SlotModel<CommonGame.MJ>> table = HashBasedTable.create();
        for (int i = 1; i < 5; i++) {
            List<SlotModel<CommonGame.MJ>> tmpList = board.get(i);
            for (SlotModel<CommonGame.MJ> model : tmpList) {
                if(model.getType() == CommonGame.MJ.BAIDA){
                    table.put(model.getX(),model.getY(),model);
                }
            }

        }
        return table;
    }
    /**
     * 检测普通符号
     */
    public Map<Integer, MjGoal> checkCommon(Map<CommonGame.MJ, List<SlotModel<CommonGame.MJ>>> firstMap) {
        Map<Integer, MjGoal> map = new HashMap<>();
        for (CommonGame.MJ type : firstMap.keySet()) {
            List<SlotModel<CommonGame.MJ>> firstList = firstMap.get(type);
            int size1 = firstList.size();
            MjGoal bo = map.getOrDefault(type.getNumber(), new MjGoal(type, 1, 1));
            map.putIfAbsent(type.getNumber(), bo);
            int rate = bo.getRate();
            List<SlotModel<CommonGame.MJ>> tmpList2 = new ArrayList<>(firstList);
            log.info("type------->"+type.getNumber()+"---------->start");
            for (int i = 1; i < 5; i++) {
                log.info("type==>"+type.getNumber()+" col:"+i+"==>start");
                //每列加1
                List<SlotModel<CommonGame.MJ>> tmpList = board.get(i);
                for (SlotModel<CommonGame.MJ> e : tmpList) {
                    if (e.getType() == type || e.getType() == CommonGame.MJ.BAIDA) {//百搭处理
                        bo.addC();
                        break;
                    }
                }
                //每列相同的汇总
                int cout = 0;//每列的乘积因子
                for (SlotModel<CommonGame.MJ> e : tmpList) {
                    if (e.getType() == type || e.getType() == CommonGame.MJ.BAIDA) {//百搭处理
                        cout++;
                        tmpList2.add(e);
                        log.info(e.toString());
                    }
                }
                rate = rate * cout;
                log.info("type==>"+type.getNumber()+" col:"+i+"=>end");
            }
            log.info("type------->"+type.getNumber()+"---------->end");
            if (bo.getC()>=3){ //移除匹配的
                CSlot slot = service.get(CommonGame.GameType.MAJIANG_2, type.getNumber(), bo.getC());
                if(slot != null){
                    for (SlotModel<CommonGame.MJ> e : tmpList2) {
                        for (int i = 1; i < 5; i++) {
                            List<SlotModel<CommonGame.MJ>> tmpList = board.get(i);
                            tmpList.removeIf(model->model.getType() == e.getType()&& model.getX()== e.getX()&& model.getY()== e.getY());
                        }
                    }
                }
            }
            rate = rate * size1;
            bo.setRate(rate);
            bo.addPoint(tmpList2);
        }
        return map;
    }

    /**
     * 检测特殊符号-胡
     */
    public Map<Integer, MjGoal> checkSpecial(Map<CommonGame.MJ, List<SlotModel<CommonGame.MJ>>> firstMap) {
        Map<Integer, MjGoal> map = new HashMap<>();
        int c = 1;
        CommonGame.MJ type = CommonGame.MJ.HU;
        List<SlotModel<CommonGame.MJ>> firstList = firstMap.get(type);
        if(firstList == null || firstList.isEmpty()){
            return map;
        }
        List<SlotModel<CommonGame.MJ>> tmpList2 = new ArrayList<>(firstList);
        for (int i = 1; i < 5; i++) {
            List<SlotModel<CommonGame.MJ>> tmpList = board.get(i);
            for (SlotModel<CommonGame.MJ> e : tmpList) {
                if (e.getType() == type) {
                    c++;
                    tmpList2.add(e);
                }
            }
        }
        if (c < 3) {
            return map;
        }
        MjGoal bo = map.getOrDefault(type.getNumber(), new MjGoal(type, c, 1));
        map.putIfAbsent(type.getNumber(), bo);
        bo.addPoint(tmpList2);
        return map;
    }

    /**
     * 判断中奖
     *
     * @param map
     */
    public void checkReward(Map<Integer, MjGoal> map) {
        Iterator<Map.Entry<Integer, MjGoal>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, MjGoal> entry = iter.next();
            int k = entry.getKey();
            MjGoal bo = entry.getValue();
            CSlot slot = service.get(CommonGame.GameType.MAJIANG_2, k, bo.getC());
            if (slot == null) {
                iter.remove();
            }
        }
        map.forEach((mj, bo) -> {
            log.info("判断消除->" + mj + " :" + bo);
        });
    }

    // 消除符号并让下方的符号前移
    private boolean move(Map<Integer, MjGoal> goals, boolean b_exclude, boolean free) {
        remove(goals);

        Set<CommonGame.MJ> excludes = new HashSet<>();
        boolean b = false;
        for (int i = 0; i < 5; i++) {
            boolean hu = false;//是否有hu
            List<SlotModel<CommonGame.MJ>> models = board.get(i);
            for (SlotModel<CommonGame.MJ> bo : models) {
                if (bo.getType() == CommonGame.MJ.HU) {
                    hu = true;
                    break;
                }
            }
            int maxSize = 5;
            if (i == 0 || i == 4) {
                maxSize = 4;
            }
            int size = maxSize - models.size();
            int initSize = models.size();
            for (int j = 0; j < size; j++) {
                Slot slot;
                if ((i == 1 || i == 2) && b_exclude) {
                    slot = MJCommon.ins.random(slots, excludes, goals.keySet(), i, hu);
                } else {
                    slot = MJCommon.ins.random(slots, null, goals.keySet(), i, hu);
                }
                if (slot.getK() == CommonGame.MJ.HU.getNumber()) {
                    hu = true;
                }
                SlotModel model = SlotModel.builder().type(CommonGame.MJ.forNumber(slot.getK())).x(i).y(initSize+j).gold(slot.isGold()).build();
                models.add(model);
                b = true;
            }
            if (i == 0) {
                for (SlotModel<CommonGame.MJ> bo : board.getFirst()) {
                    excludes.add(bo.getType());
                }
            }
        }
        //免费次数第三列都是金色牌处理
        col3Gold(free);

        //打印日志
        StringJoiner sj = new StringJoiner(",");
        for (CommonGame.MJ exclude : excludes) {
            sj.add(exclude.getNumber() + "");
        }
        log.info("excludes: " + sj);
        return b;
    }

    /**
     * 消除中奖
     * 金色牌变百搭
     * @param goals
     * @return 返回消除的符号
     */
    public void remove(Map<Integer, MjGoal> goals) {
        for (List<SlotModel<CommonGame.MJ>> list : board) {
            Iterator<SlotModel<CommonGame.MJ>> iter = list.iterator();
            while (iter.hasNext()) {
                SlotModel<CommonGame.MJ> bo = iter.next();
                CommonGame.MJ mj = bo.getType();
                MjGoal goal = goals.get(mj.getNumber());
                if (goal == null) continue;
                if (goal.getPoints().isEmpty()) continue;
                for (SlotModel<CommonGame.MJ> p : goal.getPoints()) {
                    if (bo.getX() == p.getX() && bo.getY() == p.getY()) {
                        if(bo.isGold()){
                           bo.setType(CommonGame.MJ.BAIDA);
                           goals.remove(mj.getNumber());
                        }else{
                            iter.remove();
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 第三列金色处理
     */
    public void col3Gold(boolean free) {
        if (!free) return;
        List<SlotModel<CommonGame.MJ>> list3 = board.get(2);
        int size = list3.size();
        for (int i = 0; i < size; i++) {
            SlotModel<CommonGame.MJ> model = list3.get(i);
            if (model.isGold()) continue;
            model.setGold(true);
        }
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
