package com.z.model.meilisearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MSIndex(uid = "com_baidu_main", primaryKey = "idToString")
public class MainDO {
    private Long id;
    @MSFiled(openFilter = true, key = "idToString", openSort = true)
    private String idToString;
    private String seedsName;
    @MSFiled(openFilter = true, key = "isDelete")
    private Integer isDelete;
    @MSFiled(openFilter = true, key = "status")
    private Integer status;

    @MSFiled(openFilter = true, key = "classFiledId")
    private Integer classFiledId;
    private String classFiledName;

    @MSFiled(openFilter = true,key = "createTime",openSort = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createTime;
    @MSFiled(openFilter = true,key = "createTimeToLong",openSort = true)
    private LocalDateTime createTimeToLong;

    @MSFiled(openFilter = true,key = "date1",openSort = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date1;
}