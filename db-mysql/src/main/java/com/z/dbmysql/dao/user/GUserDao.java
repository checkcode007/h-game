package com.z.dbmysql.dao.user;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GUser;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GUserDao extends AbstractMapperService<GUser,Long> {

    @Autowired
    GUserMaper maper;

    @Override
    public String cacheNamespace() {
        return "g_user";
    }

    @Override
    protected IMapper<GUser, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return "g_user";
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{"g_user"};
    }
    public List<GUser> getAll(){
        return super.getAll(null);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #uid",unless = "#result == null")
    public GUser findById(long uid){
        return super.findById(uid);
    }


    public GUser save(GUser user){
        return super.save(user);
    }
//    @Caching(evict = {
//            @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + #user.id"),
//            @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'phone' + ':' + #user.phone"),
//            @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'device' + ':' + #user.deviceId")
//    })
    @Override
    public GUser update(GUser gUser) {
        return super.update(gUser);
    }
    public List<GUser> findRobot(int num){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("robot",1);
        wheres.put("state", UserState.DEFAULT.k);
        return super.findByMultiByParam(wheres,num);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'phone' + ':' +  #phone",unless = "#result == null")
    public GUser findByPhone(String phone){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("phone",phone);
        return super.findByOneByParam(wheres);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'device' + ':' +  #deviceId",unless = "#result == null")
    public List<GUser> findByDeviceId(String deviceId){
        return this.findByMultiByWhere(" and device_id= '"+deviceId+"' and state !="+UserState.DEL.k,100);
    }
}
