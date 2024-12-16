package com.z.model.mysql.cfg;

import com.z.model.common.ExcludeCreateTime;
import com.z.model.common.ExcludeUpdateTime;
import com.z.model.mysql.Model;

@ExcludeUpdateTime
@ExcludeCreateTime
public class CMaJiang extends Model {
    /**
     * 水果类型
     * @see com.z.model.proto.CommonGame.MJ
     */
    private int type;

    private int c;//相同的个数
    private int rate; // 倍率
    private int free; //免费次数

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }
}
