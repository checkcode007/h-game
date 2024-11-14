package com.z.model.mysql;

import lombok.Data;

@Data
public class CEmail {
    private long id;
    private int type;
    private String title;
    private String content ;
    private String reward;
    private long gold;
    private int state;

}
