package com.z.core.service.game.game;

import com.google.common.collect.Table;
import com.z.model.common.MsgResult;

import java.util.Map;

/**
 * 每轮
 */
public interface IRound {

    /**
     * 下注
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     * @return
     */
    MsgResult bet(long uid,int type,long gold,boolean free);

    /**
     * 结束
     * @return
     */
    MsgResult end();

    /**
     * 结算
     * @return
     */
    MsgResult settle();

    /**
     * 获取所有人的下注
     * @return
     */

    Table<Long,Integer,Long>  getAllBet();

    /**
     * 获取一个人的所有下注
     * @param uid
     * @return
     */
    Map<Integer,Long> getBet(long uid);

    /**
     * 获取一个人，一个池子的下注
     * @param uid
     * @param type
     * @return
     */

    long getBet(long uid,int type);

    /**
     * 退出
     */
    void out(long uid);

    long getId();

}
