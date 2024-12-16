package com.z.dbmysql.dao.mali;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CMali;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CMaliDao extends AbstractMapperService<CMali,Long> {

    @Autowired
    CMaliMaper maper;

    @Override
    public String cacheNamespace() {
        return "c_mali";
    }

    @Override
    protected IMapper<CMali, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return "c_mali";
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{"c_mali"};
    }
    public List<CMali> getAll(){
        return super.getAll(null);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #uid",unless = "#result == null")
    public CMali findById(long uid){
        return super.findById(uid);
    }

    public List<CMali> findRobot(int num){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("robot",1);
        wheres.put("state", UserState.DEFAULT.k);
        return super.findByMultiByParam(wheres,num);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'phone' + ':' +  #phone",unless = "#result == null")
    public CMali findByPhone(String phone){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("phone",phone);
        return super.findByOneByParam(wheres);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'device' + ':' +  #deviceId",unless = "#result == null")
    public List<CMali> findByDeviceId(String deviceId){
        return this.findByMultiByWhere(" and device_id= '"+deviceId+"' and state !="+UserState.DEL.k,100);
    }
}
