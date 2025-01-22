package com.z.core.service.game.slot;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.model.bo.slot.*;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.*;

public class SlotMachine {
    private static final Log log = LogFactory.getLog(SlotMachine.class);


    private List<String[]> resultPool; // 预存支付线结果池
    private Random random;
    private double roomC3 = 1.0; // 房间内输赢次数的权重
    private double roomC4 = 1.0; // 房间内投注和赢得金额的权重

    // 玩家状态
    private double playerState = 0.5; // 初始状态值，0.5代表中等概率

    //col 第几列 row 第几排
    protected int COL_SIZE=5,ROW_SIZE=3;
    protected CommonGame.GameType gameType;


    /**
     * 预生成支付线
     */
    protected Table<Integer,Integer, CSlot> table = HashBasedTable.create();
    protected List<Rewardline> lines = new ArrayList<>();
    public SlotMachine(CommonGame.GameType gameType, int colsize, int rowsize) {
        random = new Random();
        this.gameType = gameType;
        this.COL_SIZE=colsize;
        this.ROW_SIZE=rowsize;
    }

    public void initLines( Map<Integer, Payline>  map, Map<Integer, List<CSlot>> slotMap){
        if(map.isEmpty()) return;

        for (Payline payline : map.values()) {
            for (int k : slotMap.keySet()) {
                List<CSlot> list = slotMap.get(k);
                for (CSlot slot : list) {
                   if(slot.isBaida() || slot.isBonus() || slot.isScatter()) continue;
                   int c = slot.getC();
                   int rate = slot.getRate();
                    List<SlotModel> points = new ArrayList<>();
                    int i = 0;
                    for (Point p : payline.getPoints()) {
                        SlotModel m = SlotCommon.ins.toModel(slot,p.getX(),p.getY());
                        points.add(m);
                        if(++i>=c){
                           break;
                        }
                    }
                    Rewardline line = new Rewardline(k,c);
                    line.setRate(rate);
                    line.setHadBaida(false);
                    line.setSpecialC(slot.getC1());
//                    Rewardline line = new Rewardline(payline.getLineId(),k,c,rate);
                    lines.add(line);
                    line.addPoints(points);
                }
            }
        }


    }
    // 从结果池中随机选择一个支付线结果
    public Rewardline randomLine(){
        int index = random.nextInt(lines.size());
        return  lines.get(index);
    }



    // 计算房间状态因子
    private double calculateRoomStateFactor(long winCount, long loseCount, double betAmount, double winAmount) {
        // 房间状态因子 = C3 * (loseCount - winCount) + C4 * (betAmount - winAmount)
        return roomC3 * (loseCount - winCount) + roomC4 * (betAmount - winAmount);
    }

    public int getLineSize() {
        return lines.size();
    }

    public void print(){

        for (Rewardline line : lines) {
            StringJoiner sj = new StringJoiner(",");
            for (SlotModel p : line.getPoints()) {
                sj.add(p.getK()+":"+p.getX()+":"+p.getY());
            }
            log.info(sj.toString());
        }
    }
}