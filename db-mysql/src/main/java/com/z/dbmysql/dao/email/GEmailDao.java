package com.z.dbmysql.dao.email;

import com.z.common.util.MysqlTables;
import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.bean.Pager;
import com.z.model.mysql.GBankTransfer;
import com.z.model.mysql.GEmail;
import com.z.model.proto.CommonUser;
import com.z.model.proto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GEmailDao extends AbstractMapperService<GEmail, Long> {

    @Autowired
    GEmailMaper maper;

    static final String TABLE_NAME = "g_email";

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<GEmail, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
//        return MysqlTables.modular_10(aLong, TABLE_NAME);
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
//        String[] ss = new String[10];
//        for (long i = 0; i < 10; i++) {
//            ss[(int) i] = MysqlTables.modular_10(i, TABLE_NAME);
//        }
//        return ss;
        return new String[]{TABLE_NAME};
    }

    public List<GEmail> getAll() {
        return super.getAll(null);
    }

    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #id", unless = "#result == null")
    public GEmail findById(long id) {
        return super.findById(id);
    }

    @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #e.id")
    public GEmail save(GEmail e) {
        return super.save(e);
    }

    @CacheEvict(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #e.id")
    @Override
    public GEmail update(GEmail e) {
        return super.update(e);
    }

    public List<GEmail> findByUid(long uid) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("uid", uid);
        return super.findByMultiByParam(wheres, 100);
    }

    public GEmail findByUidLastOne(long uid) {
        List<GEmail> list = this.findByMultiByWhere(
                " and uid= " + uid + " order by create_time desc", 1);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;

    }

    public List<GEmail> find(long uid, CommonUser.YesNo yesNo) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("uid", uid);
        wheres.put("state", yesNo.getNumber());
        return super.findByMultiByParam(wheres, 100);
    }

    public Pager<GEmail> page(long uid, int page, int pageSize) {
        // 构造查询条件
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("uid", uid);
        Pager<GEmail> pager = new Pager<>(page, pageSize);
        pager.setParams(wheres);
        List<String> orders = new ArrayList<>();
        orders.add("create_time|desc");
        pager.setOrders(orders);
        return super.page(pager);
    }

    public Pager<GEmail> page(long uid, CommonUser.YesNo yesNo, int page, int pageSize) {
        // 构造查询条件
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("uid", uid);
        wheres.put("state", yesNo.getNumber());
        Pager<GEmail> pager = new Pager<>(page, pageSize);
        pager.setParams(wheres);
        List<String> orders = new ArrayList<>();
        orders.add("create_time|desc");
        pager.setOrders(orders);
        return super.page(pager);
    }

//    public List<GEmail> findByDeviceId(String deviceId){
//        return this.findByMultiByWhere(" and deviceId= '"+deviceId+"' and state !="+UserState.DEL.k,100);
//    }
}
