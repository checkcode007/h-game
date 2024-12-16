package com.z.dbmysql.dao.mj;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CMaJiang;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CMajiangDao extends AbstractMapperService<CMaJiang,Long> {

    static final String TABLE_NAME = "c_majiang";
    @Autowired
    CMajiangMapper maper;

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<CMaJiang, Long> getMapper() {
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
    public List<CMaJiang> getAll(){
        return super.getAll(null);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #uid",unless = "#result == null")
    public CMaJiang findById(long uid){
        return super.findById(uid);
    }

    public List<CMaJiang> findRobot(int num){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("robot",1);
        wheres.put("state", UserState.DEFAULT.k);
        return super.findByMultiByParam(wheres,num);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'phone' + ':' +  #phone",unless = "#result == null")
    public CMaJiang findByPhone(String phone){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("phone",phone);
        return super.findByOneByParam(wheres);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + 'device' + ':' +  #deviceId",unless = "#result == null")
    public List<CMaJiang> findByDeviceId(String deviceId){
        return this.findByMultiByWhere(" and device_id= '"+deviceId+"' and state !="+UserState.DEL.k,100);
    }
}
