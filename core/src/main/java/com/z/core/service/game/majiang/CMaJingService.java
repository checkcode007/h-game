package com.z.core.service.game.majiang;

import com.z.dbmysql.dao.mj.CMajiangDao;
import com.z.model.mysql.cfg.CMaJiang;
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
 * 麻将-配置
 */
@Service
public class CMaJingService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CMajiangDao dao;
    Map<CommonGame.MJ, List<CMaJiang>> map = new HashMap<>();
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
        List<CMaJiang> allList = dao.getAll();
        if (allList == null || allList.isEmpty()) return;
        Map<CommonGame.MJ, List<CMaJiang>> map1 = new HashMap<>();
        for (CMaJiang e : allList) {
            CommonGame.MJ symbol = CommonGame.MJ.valueOf(e.getType());
            if (symbol == null) continue;
            List<CMaJiang> list = map1.getOrDefault(symbol, new ArrayList<>());
            map1.putIfAbsent(symbol, list);
            list.add(e);

        }
        map = map1;

    }

    public List<CMaJiang> get(CommonGame.MJ symbol) {
        if (map.isEmpty()) {
            reload();
        }
        return map.get(symbol);
    }

    public CMaJiang get(CommonGame.MJ symbol, int c) {
        List<CMaJiang> list = get(symbol);
        if (list == null || list.isEmpty()) return null;
        for (CMaJiang e : list) {
            if (e.getC() == c) return e;
        }
        return null;
    }

    public Map<CommonGame.MJ, List<CMaJiang>> getMap() {
        return map;
    }
}
