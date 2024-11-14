package com.z.dbmysql.common;

import com.z.model.bean.Pager;
import org.apache.ibatis.jdbc.SQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class AbstractMapperService<T, ID extends Serializable> {
    public abstract String cacheNamespace();

    protected abstract IMapper getMapper();

    protected abstract String getTableName(ID id);

    protected abstract String[] getAllTableName();

    protected T findByOneByParam(Map<String,Object> wheres){
        String[] tableNames = getAllTableName();
        T t = null;
        for (int i = 0; i < tableNames.length; i++) {
           t = (T)getMapper().findByOneByParam(tableNames[i],wheres);
           if (t != null){
               return t;
           }
        }
        return null;
    }

    /**
     *
     * @param wheres
     * @param orderColumns 如 price desc
     * @param limit
     * @return
     */
    protected List<T> findByMultiByParam( Map<String,Object> wheres,String orderColumns,int limit){
        String[] tableNames = getAllTableName();
       List<T> list = new ArrayList<T>();
        for (int i = 0; i < tableNames.length; i++) {
            List<T>  l = (List<T>) getMapper().findByMultiByParam(tableNames[i],wheres,orderColumns,limit);
            if (l != null && l.size()>0){
                list.addAll(l);
                if (list.size()>=limit){
                    break;
                }
            }
        }
        return list;
    }

    protected List<T> findByMultiByParam( Map<String,Object> wheres,int limit) {
        return this.findByMultiByParam(wheres,null,limit);
    }

    protected List<T> findByMultiByWhere(String wheres,int limit){
        String[] tableNames = getAllTableName();
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < tableNames.length; i++) {
            List<T>  l = (List<T>) getMapper().findByMultiByWhere(tableNames[i],wheres,limit);
            if (l != null && l.size()>0){
                list.addAll(l);
                if (list.size()>=limit){
                    break;
                }
            }
        }
        return list;
    }

    public List<T> getAll(String order){
        if (order != null)
            return getMapper().getAll(getAllTableName()[0]," order by "+order);
        else
            return getMapper().getAll(getAllTableName()[0],"");
    }

    protected T get(ID id) {
        return findById(id);
    }

    protected T save(T t) {
       ID id= (ID) SqlProvider.getFieldID(t);
       if (id == null){
           getMapper().saveAutoKey(getAllTableName()[0],t);
       }else {
           getMapper().save(getTableName(id),t);
       }
        return t;
    }


    protected T update(T t) {
         getMapper().update(getTableName((ID) SqlProvider.getFieldID(t)),t);
         return t;
    }

    protected Integer updateFieldById(Map<String,Object> sets,ID id) {
      return getMapper().updateFieldById(getTableName(id),sets,id);
    }

    protected Integer updateFieldBy(String sets,ID id,String wheres) {
        return getMapper().updateFieldBy(getTableName(id)," set "+sets+" "," where "+wheres+" ");
    }

    protected T findById(ID id) {
          return (T)getMapper().findById(getTableName(id),id);
    }

    protected Integer deleteById(ID id) {
       return getMapper().deleteById(getTableName(id),id);
    }



    protected List<T> saveAll(List<T> list) {
        list.forEach(it->{
            this.save(it);
        });
        return list;
    }

    protected Pager<T> page(Pager<T> pager, ID id){
        SQL sql = new SQL();
        String tName = null;
        if (id == null){
            tName = getAllTableName()[0];
        }else {
            tName = getTableName(id);
        }
        sql.SELECT("*").FROM(tName);
        addQueryParams(sql,pager.getParams());
        List<String> orders = pager.getOrders();
        if (orders != null && orders.size()>0){
            orders.forEach(it->{
                sql.ORDER_BY(SqlProvider.humpToLine(it.replace("|"," ")));
            });
        }
        sql.OFFSET(pager.getOffset());
        sql.LIMIT(pager.getSize());
        List<T> list = getMapper().page(sql.toString(),pager.getParams());
        pager.setList(list);
        return pager;
    }

    protected Pager<T> page(Pager<T> pager){
        return this.page(pager,null);
    }

    protected void addQueryParams(SQL sql, Map<String, Object> params) {
        if (params!=null && params.size() > 0){
            params.keySet().forEach(it->{
                sql.WHERE(SqlProvider.humpToLine(it)+"=#{wheres["+it+"]}");
            });
        }
    }

//    public Pager<T> page(Pager<T> pager) {
//        Page<T> page = getMapper().findAll(getWhereClause(pager.getQueryParams()), pager.getPageable());
//        pager.setTotal(page.getTotalElements());
//        pager.setList(page.getContent());
//        return pager;
//    }

}
