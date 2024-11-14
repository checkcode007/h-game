package com.z.dbmysql.common;

import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Mapper
public interface IMapper<T , ID extends Serializable>  {

    @Transactional(readOnly = true)
    @Select(value = "${sql}")
    List<T> page(@Param("sql") String sql,@Param("wheres") Map<String, Object> wheres);

    @Transactional(readOnly = true)
    @Select(value = "${sql}")
    long countSql(@Param("sql") String sql);

    @Transactional(readOnly = true)
    @SelectProvider(type = SqlProvider.class,method = "countByParam")
    long count(@Param("tableName") String tableName,@Param("wheres") Map<String, Object> wheres);

    @Transactional(readOnly = true)
    @Select(value = "select * from ${tableName} where id=#{id}")
    T findById(@Param("tableName") String tableName, @Param("id") ID id);

    @Transactional(readOnly = true)
    @Select(value = "<script>" +
            "select * from ${tableName} where id in " +
            "<foreach collection='idList' item='id'  open='(' separator=','  close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<T> findByIdList(@Param("tableName") String tableName, @Param("idList") List<ID> idList);

    @Transactional(readOnly = true)
    @SelectProvider(type = SqlProvider.class,method = "findByOneByParam")
    T findByOneByParam(@Param("tableName") String tableName, @Param("wheres") Map<String, Object> wheres);

    @Transactional(readOnly = true)
    @SelectProvider(type = SqlProvider.class,method = "findByMultiByParam")
    List<T> findByMultiByParam(@Param("tableName") String tableName, @Param("wheres") Map<String, Object> wheres, @Param("order")String order,@Param("limit") int limit);

    @Transactional(readOnly = true)
    @Select(value = "select * from ${tableName} where 1=1 ${wheres} limit #{limit}")
    List<T> findByMultiByWhere(@Param("tableName") String tableName, @Param("wheres") String wheres, @Param("limit") int limit);

    @Transactional(readOnly = true)
    @Select(value = "select * from ${tableName} ${order}")
    List<T> getAll(@Param("tableName") String tableName,@Param("order")String order);

    @Transactional
    @InsertProvider(type = SqlProvider.class,method = "insert")
    void save(@Param("tableName") String tableName, @Param("t") T t);

    @Transactional
    @InsertProvider(type = SqlProvider.class,method = "insert")
    @Options(useGeneratedKeys=true,keyProperty = "t.id",keyColumn = "t.id")
    void saveAutoKey(@Param("tableName") String tableName, @Param("t") T t);

    @Transactional
    @UpdateProvider(type = SqlProvider.class,method = "update")
    void update(@Param("tableName") String tableName, @Param("t") T t);

    @Transactional
    @Delete(value = "delete from ${tableName} where  id=#{id}")
    Integer deleteById(@Param("tableName") String tableName, @Param("id") ID id);

    @Transactional
    @Delete("delete from ${tableName} where family_id = #{familyId}")
    Integer deleteByfamilyId(@Param("tableName") String tableName, @Param("familyId") Long familyId);

    @Transactional
    @Update(value = "update ${tableName} ${sets} ${wheres}")
    Integer updateFieldBy(@Param("tableName") String tableName, @Param("sets") String sets, @Param("wheres") String wheres);

    @Transactional
    @UpdateProvider(type = SqlProvider.class,method = "updateFieldById")
    Integer updateFieldById(@Param("tableName") String tableName, @Param("sets") Map<String, Object> sets, @Param("id") ID id);
}
