package com.z.core.service.user;


import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.z.core.util.SpringContext;
import com.z.dbmysql.dao.agent.GAgentDao;
import com.z.model.bo.user.AgentBo;
import com.z.model.mysql.GAgent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.TimeUnit;

//@Log4j2
public enum AgentService {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());

    GAgentDao dao;
    AgentService() {
        dao = SpringContext.getBean(GAgentDao.class);

    }
    LoadingCache<Long, AgentBo> cache = Caffeine.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES).initialCapacity(1000).maximumSize(3000).build(new CacheLoader<Long, AgentBo>() {
                @Override
                public @Nullable AgentBo load(@NonNull Long id) throws Exception {
                    GAgent agent = dao.findById(id);
                    if(agent == null) return null;
                    AgentBo bo = new AgentBo();
                    BeanUtils.copyProperties(agent,bo);
                    bo.setId(agent.getId());
                    bo.setAgent(agent);
                    return bo;
                }
            });


    public AgentBo get(long uid){
        return cache.get(uid);
    }
    public void add(long fromId, long targetId){
        dao.add(fromId,targetId);
        cache.put(fromId,new AgentBo());
    }

}
