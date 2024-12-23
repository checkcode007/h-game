package com.z.dbmysql.common;

import com.z.dbmysql.dao.pool.GPoolDao;
import com.z.model.common.ExcludeCreateTime;
import com.z.model.common.ExcludeUpdateTime;
import com.z.model.mysql.cfg.GPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;

import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql适配器
 */
//@Log4j2
public class SqlProvider {
    protected Logger log = LoggerFactory.getLogger(getClass());


    /**
     * 插入
     * @param tableName
     * @param t
     * @return
     */
    public String insert(@Param("tableName") String tableName, @Param("t")Object t){
        List<Field> fields = getField(t);
        SQL sql = new SQL();
        sql.INSERT_INTO(tableName);
        for (Field field:fields) {
           sql.VALUES(humpToLine(field.getName()),"#{t."+field.getName()+"}");
        }
       log.info("insert sql="+sql.toString());
        return sql.toString();
    }

    /**
     * 修改所有
     * @param tableName
     * @param t
     * @return
     */
    public String update(@Param("tableName") String tableName, @Param("t")Object t){
        List<Field> fields = getField(t);
        SQL sql = new SQL();
        sql.UPDATE(tableName);
        for (Field field:fields) {
            if (field.getName().equals("id")){
                continue;
            }
             sql.SET(humpToLine(field.getName())+"=#{t."+field.getName()+"}");
        }
        sql.WHERE("id=#{t.id}");
        log.debug("update sql={}",sql.toString());
        return sql.toString();
    }

    /**
     * 修改某些字段 根据where
     * @param tableName
     * @param sets
     * @param wheres
     * @return
     */
    public String  updateFieldBy(@Param("tableName") String tableName, @Param("sets")Map<String,Object> sets, @Param("wheres")Map<String,Object> wheres){
        SQL sql = new SQL();
        sql.UPDATE(tableName);
        sets.keySet().forEach(it->{
            sql.SET(humpToLine(it)+"=#{sets["+it+"]}");
        });
        wheres.keySet().forEach(it->{
            sql.WHERE(humpToLine(it)+"=#{wheres["+it+"]}");
        });
        log.debug("updateFieldBy sql={}",sql.toString());
        return sql.toString();
    }
    /**
     * 修改某些字段 根据ID
     * @param tableName
     * @param sets
     * @param id
     * @return
     */
    public String  updateFieldById(@Param("tableName") String tableName, @Param("sets")Map<String,Object> sets, @Param("id")Serializable id){
        SQL sql = new SQL();
        sql.UPDATE(tableName);
        sets.keySet().forEach(it->{
            sql.SET(humpToLine(it)+"=#{sets["+it+"]}");
        });
        sql.WHERE("id=#{id}");
        log.debug("updateFieldBy sql={}",sql.toString());
        return sql.toString();
    }

    /**
     * 查询多条
     * @param tableName
     * @param wheres
     * @return
     */
    public String findByMultiByParam(@Param("tableName") String tableName, @Param("wheres")Map<String,Object> wheres,@Param("order")String order,@Param("limit")int limit){
        SQL sql = new SQL();
        sql.SELECT("* ");
        sql.FROM(tableName);
        wheres.keySet().forEach(it->{
            sql.WHERE(humpToLine(it)+"=#{wheres["+it+"]}");
        });
        if (StringUtils.isNotBlank(order)){
            sql.ORDER_BY(humpToLine(order));
        }
        sql.LIMIT(limit);
        log.debug("findByMultiByParam sql={}",sql.toString());
        return sql.toString();
    }

    /**
     * 查询多条
     * @param tableName
     * @param wheres
     * @return
     */
    public String findByOneByParam(@Param("tableName") String tableName, @Param("wheres")Map<String,Object> wheres){
        SQL sql = new SQL();
        sql.SELECT("* ");
        sql.FROM(tableName);
        wheres.keySet().forEach(it->{
            sql.WHERE(humpToLine(it)+"=#{wheres["+it+"]}");
        });
        sql.LIMIT(1);
        log.debug("findByOneByParam sql={}",sql.toString());
        return sql.toString();
    }

    /**
     * 数量
     * @param tableName
     * @param wheres
     * @return
     */
    public String countByParam(@Param("tableName") String tableName,@Param("wheres") Map<String, Object> wheres){
        SQL sql = new SQL();
        sql.SELECT(" count(*) ");
        sql.FROM(tableName);
        wheres.keySet().forEach(it->{
            sql.WHERE(humpToLine(it)+"=#{wheres["+it+"]}");
        });
        sql.LIMIT(1);
        log.debug("countByParam sql={}",sql.toString());
        return sql.toString();
    }



    private  List<Field> getField(Object model) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();
        Field[] superFields = null;
        Class superclass = model.getClass().getSuperclass();
        log.debug("superclass={},{}",superclass.getName(),superclass.getSimpleName());
        if (superclass.getSimpleName().equals("BaseModel")){
            superFields = superclass.getDeclaredFields();
        }else if(superclass.getName().equals("io.pocket.base.framework.Model")){
            superFields = superclass.getSuperclass().getDeclaredFields();
        }
        boolean  hasUpdateTime = model.getClass().isAnnotationPresent(ExcludeUpdateTime.class);
        boolean  hasCreateTime = model.getClass().isAnnotationPresent(ExcludeCreateTime.class);
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            boolean  hasAnnotation = f.isAnnotationPresent(Transient.class);
            if (!hasAnnotation && !Modifier.isStatic(f.getModifiers())) {
                fieldList.add(f);
            }
        }
        if (superFields == null) return fieldList;
        for (int i = 0; i < superFields.length; i++) {
            Field f = superFields[i];
            try {
                if (f.getName().equals("updateTime") && !hasUpdateTime){
                    f.setAccessible(true);
                    f.set(model,new Date());
                    fieldList.add(f);
                }else    if (f.getName().equals("createTime") && !hasCreateTime){
                    f.setAccessible(true);
                    if (f.get(model) == null){
                        f.set(model,new Date());
                    }
                    fieldList.add(f);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
         return fieldList;
    }
    public static Long getFieldID(Object model) {
        try {
            Field field = model.getClass().getDeclaredField("id");
            if (field == null){
                return null;
            }
            field.setAccessible(true);
            return  Long.valueOf(field.get(model).toString());
        } catch (NoSuchFieldException e) {
            System.err.println("NoSuchFieldException={},auto id"+e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        GPool pool = new GPool();
//        pool.setId(100);
        getFieldID(pool);
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    /** 驼峰转下划线*/
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
