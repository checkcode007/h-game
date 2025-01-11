package com.z.core.service.game.slot;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.game.wm.WMRoom;
import com.z.dbmysql.dao.slot.CPaylineDao;
import com.z.model.bo.slot.Payline;
import com.z.model.bo.slot.Point;
import com.z.model.mysql.cfg.CPayline;
import com.z.model.proto.CommonGame;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * slot-配置
 */
@Service
public class CPaylineService {
    private static final Log log = LogFactory.getLog(CPaylineService.class);

//    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CPaylineDao dao;

    Table<CommonGame.GameType, Integer, Payline> table = HashBasedTable.create();

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
        List<CPayline> allList = dao.getAll();
        if (allList == null || allList.isEmpty()) return;
        Table<CommonGame.GameType, Integer, Payline> table1 = HashBasedTable.create();
        for (CPayline e : allList) {
            CommonGame.GameType gameType = CommonGame.GameType.valueOf(e.getType());
            if (gameType == null) continue;
            Payline payline = new Payline(e.getId(), CommonGame.GameType.forNumber(e.getType()),e.getLineId());
            table1.put(gameType, e.getLineId(),payline);
            // 将 JSON 字符串转换为 List<Point>
//            log.info(e.getId()+"------>"+e.getPoints());
            List<Point> points =parsePointsFromJson(e.getPoints());
            payline.addPoints(points);
        }
        table = table1;

    }


    public Map<Integer, Payline> getMap(CommonGame.GameType gameType) {
        return table.row(gameType);
    }

    public Payline get(CommonGame.GameType gameType, int lineId) {
        return table.get(gameType, lineId);
    }

    /**
     * 解析 JSON 字符串并将其转化为 Point 对象列表
     *
     * @param jsonString JSON 格式的字符串
     * @return List<Point> 转化后的 Point 对象列表
     */
    public static List<Point> parsePointsFromJson(String jsonString) {
        // 创建一个 JsonReader 来解析 JSON 字符串
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));

        // 读取 JSON 数组
        JsonArray jsonArray = jsonReader.readArray();

        // 存储解析后的 Point 对象
        List<Point> points = new ArrayList<>();

        // 遍历 JSON 数组，解析每个 JSON 对象并转换为 Point 对象
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            int x = jsonObject.getInt("x");
            int y = jsonObject.getInt("y");

            // 创建 Point 对象并添加到列表中
            points.add(new Point(x, y));
        }

        return points;
    }
}
