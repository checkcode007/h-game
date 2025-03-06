package com.z.core.meilisearch.dao;

import com.meilisearch.sdk.SearchRequest;
import com.z.common.util.DateTimeUtil;
import com.z.core.meilisearch.json.SearchResult;
import com.z.core.util.IdUtil;
import com.z.core.util.RedisUtil;
import com.z.model.es.EsUserLog;
import com.z.model.meilisearch.MSUserLog;
import com.z.model.mysql.GUser;
import com.z.model.type.UserAction;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MsUserLogService {
    @Autowired
    MsUserLogMapper mapper;

    public void reg(GUser user) {
        DateTime now = DateTime.now();
        MSUserLog record = new MSUserLog();
        record.setId(IdUtil.nextUserLogId());
        record.setUid(user.getId());
        record.setPhone(user.getPhone());
        record.setT(now.getMillis());
        record.setAction(UserAction.REG.k);
        record.setDay(DateTimeUtil.getDateShortInt(now));
        record.setD(now.toDate());
        record.setName(user.getName());
        record.setRobot(user.getRobot());
        record.setDeviceId(user.getDeviceId());
        record.setIp(user.getIp());
        mapper.add(record);
    }
    public void login(GUser user) {
        DateTime now = DateTime.now();
        MSUserLog record = new MSUserLog();
        record.setId(IdUtil.nextUserLogId());
        record.setUid(user.getId());
        record.setPhone(user.getPhone());
        record.setT(now.getMillis());
        record.setAction(UserAction.LOGIN.k);
        record.setDay(DateTimeUtil.getDateShortInt(now));
        record.setD(now.toDate());
        record.setName(user.getName());
        record.setRobot(user.getRobot());
        record.setDeviceId(user.getDeviceId());
        record.setIp(user.getIp());
        mapper.add(record);
    }

    public void out(GUser user) {
        DateTime now = DateTime.now();
        MSUserLog record = new MSUserLog();
        record.setId(IdUtil.nextUserLogId());
        record.setUid(user.getId());
        record.setPhone(user.getPhone());
        record.setT(now.getMillis());
        record.setAction(UserAction.LOGOUT.k);
        record.setDay(DateTimeUtil.getDateShortInt(now));
        record.setD(now.toDate());
        record.setName(user.getName());
        record.setRobot(user.getRobot());
        record.setDeviceId(user.getDeviceId());
        record.setIp(user.getIp());
        mapper.add(record);
    }


    public ArrayList<HashMap<String, Object>> page(int page,int size){
        //根据标签分页查询
        SearchRequest searchRequest4 = SearchRequest.builder()
                .limit((page + 1) * size)
                .sort(new String[]{"id:desc"})
                .offset(page)
//                .filter(new String[]{"tags.id=" + "10010" + " AND status=1 AND isDelete=0"})
                .build();
        SearchResult<MSUserLog> search = mapper.search(searchRequest4);
        return  search.getHits();
    }



}
