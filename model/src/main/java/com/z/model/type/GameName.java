package com.z.model.type;

import com.z.model.proto.CommonGame;

public enum GameName {
    JINGDIAN_XIAOMALI(CommonGame.GameType.JINGDIAN_XIAOMALI,"经典小玛丽"),
    BAIBIAN_XIAOMALI(CommonGame.GameType.BAIBIAN_XIAOMALI,"百变小玛丽"),
    MAJIANG_2(CommonGame.GameType.MAJIANG_2,"麻将2"),
    FISH(CommonGame.GameType.FISH,"捕鱼"),
    JIUXIANLAWANG(CommonGame.GameType.JIUXIANLAWANG,"九线拉王"),
    SHUIHUZHUAN(CommonGame.GameType.SHUIHUZHUAN,"水浒传"),
    ALADING(CommonGame.GameType.ALADING,"阿拉丁"),
    BAIREN_NIUNIU(CommonGame.GameType.BAIREN_NIUNIU,"百人牛牛"),
    BINGQIUTUPO(CommonGame.GameType.BINGQIUTUPO,"冰球突破"),
    SHAOLIN_ZUQIU(CommonGame.GameType.SHAOLIN_ZUQIU,"少林足球"),
    JIANGSHIXINNIANG(CommonGame.GameType.JIANGSHIXINNIANG,"僵尸新娘"),

    JINZHUSONGFU(CommonGame.GameType.JINZHUSONGFU,"金猪送福"),
    BAIBIAN_XIAOMALI_HIGHER(CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER,"小玛丽玩法"),
    SHUIHUZHUAN_HIGHER(CommonGame.GameType.SHUIHUZHUAN_HIGHER,"小玛丽玩法"),

    ;
    CommonGame.GameType type;
    String name;

    GameName(CommonGame.GameType type, String name) {
        this.type = type;
        this.name = name;
    }
    public static GameName getType(CommonGame.GameType k) {
        for (GameName type : values()) {
            if (type.getType() == k) {
                return type;
            }
        }
        return null;
    }
    public CommonGame.GameType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
