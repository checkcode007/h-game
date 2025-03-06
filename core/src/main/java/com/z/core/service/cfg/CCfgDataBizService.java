package com.z.core.service.cfg;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.dbmysql.dao.cfg.CCfgDao;
import com.z.model.bo.CfgBo;
import com.z.model.mysql.cfg.CCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CCfgDataBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CCfgDao dao;

    Table<String, Class<?>, CfgBo> table = HashBasedTable.create();

    public void reload(){
        List<CCfg> list =  dao.findAll();
        if(list == null || list.isEmpty()) return;

        Table<String, Class<?>, CfgBo> table1 = HashBasedTable.create();
        for (CCfg cfg : list) {
            String type = cfg.getType();
            String name = cfg.getName();
            String v = cfg.getV();
            switch (type){
                case "Integer":
                    table1.put(name,Integer.class,new CfgBo<Integer>((Integer.valueOf(v))));
                    break;
                case "Long":
                    table1.put(name,Long.class,new CfgBo<Long>((Long.valueOf(v))));
                    break;
                case "Float":
                    table1.put(name,Long.class,new CfgBo<Float>(Float.valueOf(v)));
                    break;
                case "Double":
                    table1.put(name,Double.class,new CfgBo<Double>(Double.valueOf(v)));
                    break;
                case "String":
                    table1.put(name,String.class,new CfgBo<String>(v));
                    break;
                default:
                    table1.put(name,String.class,new CfgBo<String>(v));

            }
        }
        table =table1;
    }


//    @Scheduled(cron = "*/5 * * * * ?" )

    @Scheduled(cron = "0 0/5 * * * ?" )
    public void exe(){
//        log.info("----------->start");
        reload();
        print();
//        log.info("----------->end");
    }
    public <T> T get(String name,Class<?> clazz){
        if(table.isEmpty()) reload();
        CfgBo cfgBo = table.get(name,clazz);
        if(cfgBo == null) return null;
        return (T)cfgBo.getV();
    }
    public void print(){
        for (Table.Cell<String, Class<?>, CfgBo> cell : table.cellSet()) {
            String rowKey = cell.getRowKey();
            Class<?> columnKey = cell.getColumnKey();
            CfgBo<?> value = cell.getValue();
//           log.debug("RowKey: {}, ColumnKey: {}, Value: {}", rowKey, columnKey.getSimpleName(), value.getV());
        }
    }

}
