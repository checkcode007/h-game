package com.z.core.service.game;

import com.z.common.util.DateTimeUtil;
import com.z.core.util.SpringContext;
import com.z.dbmysql.dao.pool.GPoolDao;
import com.z.model.bo.slot.Pool;
import com.z.model.mysql.cfg.GPool;
import com.z.model.proto.CommonGame;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PoolService {
    ins;
    GPoolDao dao;
    /**
     * 宝箱奖池金额
     */
    Map<CommonGame.GameType, Pool> pools = new ConcurrentHashMap<>();

    PoolService() {
        dao = SpringContext.getBean(GPoolDao.class);
        init();
    }

    public void init() {
        Map<CommonGame.GameType, Pool> pools1 = new ConcurrentHashMap<>();
        List<GPool> list = dao.getAll();
        if(list == null || list.size() == 0) {
            return;
        }
        for (GPool p : list) {
            Pool pool = new Pool(p);
            pools1.put(CommonGame.GameType.forNumber((int)p.getId()),pool);
        }
        pools = pools1;
    }

    public void add(CommonGame.GameType gameType, long gold) {
        Pool pool =  pools.get(gameType);
        if(pool == null) {
            return;
        }
        pool .addGold(gold/10);
    }

    public long get(CommonGame.GameType gameType) {
        Pool pool =  pools.get(gameType);
        if(pool == null) {
            return 0;
        }
        return  pool.getGold();
    }
    public void exe() {
        long now = System.currentTimeMillis();
        for (Pool p : pools.values()) {
            if(DateTimeUtil.getSunDayEnd(new DateTime(p.getLastDate())).getMillis()<now){
                p.setGold(p.getGold());
                dao.update(p.getPool());
                continue;
            }
            if(!p.isChange()){
                continue;
            }
            dao.update(p.getPool());
            p.setChange(false);
        }
    }
}
