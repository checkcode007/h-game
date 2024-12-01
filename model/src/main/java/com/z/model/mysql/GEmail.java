package com.z.model.mysql;

import lombok.Data;
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
    private long fromId;
    private long transferId;
    private int state;

}
