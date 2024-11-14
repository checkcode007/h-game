package com.z.dbmysql.dao.user;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GUser;
import org.springframework.beans.factory.annotation.Autowired;
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

    public GUser findById(long uid){
        return super.findById(uid);
    }

    public GUser findByPhone(String phone){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("phone",phone);
        return super.findByOneByParam(wheres);
    }
    public GUser save(GUser user){
        return super.save(user);
    }

}
