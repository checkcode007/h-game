package com.z.model.bo.slot;

import com.z.model.mysql.cfg.GPool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Pool {
    GPool pool;
    boolean change;

    public Pool(GPool pool) {
        this.pool = pool;
    }

    public long getId(){
        return pool.getId();
    }
    public long getInitGold(){
        return pool.getInitGold();
    }
    public long getGold(){
        return pool.getGold();
    }
    public void setGold(long gold){
        pool.setGold(gold);
    }
    public void addGold(long gold){
        pool.setGold(pool.getGold() + gold);
        change = true;
        pool.setLastTime(new Date());
    }
    public Date getLastDate(){
        return pool.getLastTime();
    }
    public void setLastDate(Date lastDate){
        pool.setLastTime(lastDate);
    }
}
