package com.z.core.service.game.game;

import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;

/**
 * 房间
 */
public interface IRoom{
    /**
     * 是否人满
     * @param id
     * @return
     */
    boolean isFull(long id);

    /**
     * 是否空了
     * @param id
     * @return
     */
    boolean isIDle(long id);

    /**
     * 初始化
     * @param cRoom 房间配置
     */
    void init(CRoom cRoom);

    /**
     * 进入房间
     * @param uid
     * @return
     */
    MsgResult enter(long uid);

    /**
     * 进入房间后
     * @param uid
     * @return
     */
    MsgResult afterEnter(long uid);

    /**
     * 退出房间
     * @param uid
     * @return
     */
    MsgResult out(long uid);

    /**
     * 检测
     * @param uid
     * @return
     */
    MsgResult check(long uid,long curGold);

    /**
     * 获取一个类型倍率
     * @param type
     * @return
     */
    int getRadio(int type);

    long getId();




}
