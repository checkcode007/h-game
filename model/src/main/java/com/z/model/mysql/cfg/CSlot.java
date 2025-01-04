package com.z.model.mysql.cfg;

import lombok.Data;

@Data
public class CSlot {

    int id;
    /**
     * 游戏类型
     * @see com.z.model.proto.CommonGame.GameType
     */
    int type;
    /**
     * 符号类型
     * @see com.z.model.proto.CommonGame.Mali
     * @see com.z.model.proto.CommonGame.MJ
     * @see com.z.model.proto.CommonGame.LINE9
     */
    int symbol;

    int c;//相同的个数
    int rate; // 倍率
    int free; //免费次数
    int freeMax; //免费次数
    int w1;//权重1
    int w2;//权重2
    int c1;//高级玩法次数
    /**
     * @see com.z.model.type.PosType
     */
    int posType;//位置类型
    String pos;//位置
    boolean baida;
    boolean full;//是否全屏幕显示
    boolean only;//每轴只能有一个
    boolean bonus;
    boolean scatter;
    boolean quit;//是否是退出图标
}
