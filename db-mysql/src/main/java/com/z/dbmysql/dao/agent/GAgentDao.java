package com.z.dbmysql.dao.agent;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GAgentDao extends AbstractMapperService<GAgent,Long> {

    @Autowired
    GAgentMaper maper;

    @Override
    public String cacheNamespace() {
        return "g_agent";
    }

    @Override
    protected IMapper<GAgent, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return "g_agent";
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{"g_agent"};
    }

    public List<GAgent> getAll(){
        return super.getAll(null);
    }

    public GAgent findById(long uid){
        return super.findById(uid);
    }

    public List<GAgent> findByAgent(long agentId){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("agent_id",agentId);
        return super.findByMultiByParam(wheres,2000);
    }
    public void add(long uid ,long agentId){
        GAgent agent = new GAgent();
        agent.setAgentId(agentId);
        agent.setId(uid);
        agent.setLastTime(new Date());
        save(agent);
    }


}
