package com.z.dbmysql.dao.fish;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CFish;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CFishDao extends AbstractMapperService<CFish,Integer> {

    static final String TABLE_NAME = "c_fish";

    @Autowired
    CFishMaper maper;

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<CFish, Integer> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Integer aLong) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{TABLE_NAME};
    }
    public List<CFish> getAll(){
        return super.getAll(null);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #uid",unless = "#result == null")
    public CFish findById(int uid){
        return super.findById(uid);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'phone' + ':' +  #phone",unless = "#result == null")
    public CFish findByPhone(String phone){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("phone",phone);
        return super.findByOneByParam(wheres);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'device' + ':' +  #deviceId",unless = "#result == null")
    public List<CFish> findByDeviceId(String deviceId){
        return this.findByMultiByWhere(" and device_id= '"+deviceId+"' and state !="+UserState.DEL.k,100);
    }
}
