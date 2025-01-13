package com.z.dbmysql.dao.line9;

import com.z.common.type.RedisKey;
import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.dao.walletlog.GBankLogMaper;
import com.z.model.mysql.GLine9Rank;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GLine9RankDao extends AbstractMapperService<GLine9Rank,Long> {
    static final String TABLE_NAME = "g_line9_rank";

    @Autowired
    GLine9RankMaper maper;

    @Override
    public String cacheNamespace() {
        return RedisKey.LINE9_RANK;
    }

    @Override
    protected GLine9RankMaper getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{TABLE_NAME};
    }

    public List<GLine9Rank> getAll(){
        return super.getAll(null);
    }
    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':top' ",unless = "#result == null")
    @Override
    public List<GLine9Rank> getTop(String order, int limit) {
        return super.getTop(order, limit);
    }

    public GLine9Rank findById(long uid){
        return super.findById(uid);
    }

    @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':top' ")
    public GLine9Rank save(GLine9Rank user){
        return super.save(user);
    }

    @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':top' ")
    @Override
    public GLine9Rank update(GLine9Rank gBankLog) {
        return super.update(gBankLog);
    }


}
