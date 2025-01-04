package com.z.core.service.game.slot;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.dbmysql.dao.slot.CSlotDao;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * slot-配置
 */
@Service
public class CSlotService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CSlotDao dao;

    Table<CommonGame.GameType, Integer, List<CSlot>> table = HashBasedTable.create();

    Map<CommonGame.GameType,CSlot> wild = new HashMap<>();

    Map<CommonGame.GameType,CSlot> scatter =new HashMap<>();

    Map<CommonGame.GameType,CSlot> bonus= new HashMap<>();

    Map<CommonGame.GameType,CSlot> quit= new HashMap<>();


    @PostConstruct
    public void init() {
        reload();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void exe() {
        try {
            reload();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void reload() {
        List<CSlot> allList = dao.getAllCommon();
        if (allList == null || allList.isEmpty()) return;
        Table<CommonGame.GameType, Integer, List<CSlot>> table1 = HashBasedTable.create();

        Map<CommonGame.GameType,CSlot> wild1 = new HashMap<>();
        Map<CommonGame.GameType,CSlot> scatter1 =new HashMap<>();
        Map<CommonGame.GameType,CSlot> bonus1= new HashMap<>();

        Map<CommonGame.GameType,CSlot> quit1= new HashMap<>();

        for (CSlot e : allList) {
            CommonGame.GameType gameType = CommonGame.GameType.valueOf(e.getType());
            if (gameType == null) continue;
            List<CSlot> list = table1.get(gameType, e.getSymbol());
            if(list == null){
                list = new ArrayList<>();
                table1.put(gameType, e.getSymbol(), list);
            }
            list.add(e);

            if (e.isBaida()){
                wild1.put(gameType, e);
            }
            if (e.isScatter()){
                scatter1.put(gameType,e);
            }
            if (e.isBonus()){
                bonus1.put(gameType,e);
            }
            if (e.isQuit()){
                quit1.put(gameType,e);
            }

        }
        table = table1;
        wild = wild1;
        scatter = scatter1;
        bonus = bonus1;
        quit = quit1;

    }

    public Map<Integer, List<CSlot>> getMap(CommonGame.GameType gameType) {
        return table.row(gameType);
    }

    public List<CSlot> get(CommonGame.GameType gameType, int symbol) {
        return table.get(gameType, symbol);
    }

    public CSlot get(CommonGame.GameType gameType, int symbol, int c) {
        List<CSlot> list = table.get(gameType, symbol);
        if (list == null || list.isEmpty()) return null;
        for (CSlot e : list) {
            if (e.getC() == c) return e;
        }
        return null;
    }
    public CSlot getFull(CommonGame.GameType gameType, int symbol) {
        List<CSlot> list = table.get(gameType, symbol);
        if (list == null || list.isEmpty()) return null;
        for (CSlot e : list) {
            if (e.isFull()) return e;
        }
        return null;
    }

    public CSlot getWild(CommonGame.GameType gameType) {
        return  wild.get(gameType);
    }
    public CSlot getScatter(CommonGame.GameType gameType) {
        return  scatter.get(gameType);
    }
    public CSlot getBonus(CommonGame.GameType gameType) {
        return  bonus.get(gameType);
    }
    public CSlot getQuit(CommonGame.GameType gameType) {
        return  quit.get(gameType);
    }

    public Map<Integer, List<CSlot>> getSubMap(CommonGame.GameType gameType) {
        return table.row(gameType);
    }

    public List<CSlot> getSub(CommonGame.GameType gameType, int symbol) {
        return table.get(gameType, symbol);
    }

    public CSlot getSub(CommonGame.GameType gameType, int symbol, int c) {
        List<CSlot> list = table.get(gameType, symbol);
        if (list == null || list.isEmpty()) return null;
        for (CSlot e : list) {
            if (e.getC() == c) return e;
        }
        return null;
    }

}
