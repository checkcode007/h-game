package com.z.model.mysql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GEmail extends Model {
    private long uid;
    private String title;
    private String content;
    private int type;
    private long gold;
    //税
    private long tax;
    private long realGold;
    private long fromId;
    private long transferId;
    private int state;

}
