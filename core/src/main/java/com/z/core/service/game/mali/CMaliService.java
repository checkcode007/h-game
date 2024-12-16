package com.z.core.service.game.mali;

import com.z.dbmysql.dao.mali.CMaliDao;
import com.z.model.mysql.cfg.CMali;
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
 *百变小玛丽-配置
 */
@Service
public class CMaliService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CMaliDao dao;
    Map<CommonGame.Symbol, List<CMali>> map = new HashMap<>();
    @PostConstruct
    public void init() {
        reload();
    }

    @Scheduled(cron = "0 0/5 * * * ?" )
    public void exe(){
        try {
            reload();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    public void reload(){
        List<CMali> allList =  dao.getAll();
        if(allList == null || allList.isEmpty()) return;
        Map<CommonGame.Symbol, List<CMali>> map1 = new HashMap<>();
        for (CMali cMali : allList) {
            CommonGame.Symbol symbol = CommonGame.Symbol.valueOf(cMali.getType());
            if(symbol == null) continue;
            List<CMali> list = map1.getOrDefault(symbol,new ArrayList<>());
            map1.putIfAbsent(symbol,list);
            list.add(cMali);

        }
        map = map1;

    }

    public List<CMali> get(CommonGame.Symbol symbol) {
        if(map.isEmpty()){
            reload();
        }
        return map.get(symbol);
    }

    public CMali get(CommonGame.Symbol symbol, int c) {
        List<CMali> list= get(symbol);
        if(list == null || list.isEmpty()) return null;
        for (CMali e : list) {
            if(e.getC() == c) return e;
        }
        return null;
    }


}
