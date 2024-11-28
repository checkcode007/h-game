package com.z.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GEmail {
    private long id;
    private long uid;
    private String title;
    private String content;
    private int type;
    private long gold;
    private long fromId;
    private long transferId;
    private int state;
    private Date createTime;
    private Date updateTime;

}
