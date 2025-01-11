package com.z.core.service.game.fish;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.dbmysql.dao.fish.CFishDao;
import com.z.dbmysql.dao.fish.CFishFireDao;
import com.z.model.mysql.cfg.CFish;
import com.z.model.mysql.cfg.CFishFire;
import com.z.model.proto.CommonGame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 鱼-配置
 */
@Service
public class CFishService implements InitializingBean {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(CFishService.class);

    @Autowired
    CFishDao dao;
    @Autowired
    CFishFireDao fireDao;

    Table<CommonGame.FishType, CommonGame.RoomType,CFish> table = HashBasedTable.create();

    Table<CommonGame.FishFire, CommonGame.RoomType, CFishFire> fireTable = HashBasedTable.create();

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
        reloadFire();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void exe() {
        try {
            reload();
            reloadFire();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void reload() {
        List<CFish> allList = dao.getAll();
        if (allList == null || allList.isEmpty()) return;
        Table<CommonGame.FishType, CommonGame.RoomType,CFish> table1 = HashBasedTable.create();
        for (CFish e : allList) {
            CommonGame.FishType type = CommonGame.FishType.valueOf(e.getType());
            CommonGame.RoomType roomType = CommonGame.RoomType.valueOf(e.getRoomType());
            table1.put(type, roomType, e);

        }
        table = table1;

    }


    public void reloadFire() {
        List<CFishFire> allList = fireDao.getAll();
        if (allList == null || allList.isEmpty()) return;
        Table<CommonGame.FishFire, CommonGame.RoomType,CFishFire> table1 = HashBasedTable.create();
        for (CFishFire e : allList) {
            CommonGame.FishFire type = CommonGame.FishFire.valueOf(e.getType());
            CommonGame.RoomType roomType = CommonGame.RoomType.valueOf(e.getRoomType());
            table1.put(type, roomType, e);

        }
        fireTable = table1;

    }

    public CFish get(CommonGame.FishType type, CommonGame.RoomType roomType) {
        return table.get(type, roomType);
    }
    public Collection<CFish> getAll() {
        return table.values();
    }
    public CFishFire getFire(CommonGame.FishFire type, CommonGame.RoomType roomType) {
        return fireTable.get(type, roomType);
    }
    public Collection<CFishFire> getAllFire() {
        return fireTable.values();
    }
    public Map<CommonGame.FishFire,CFishFire> getFire(CommonGame.RoomType roomType) {
        return fireTable.column(roomType);
    }

}
