package com.z.model.common;

/**
 *消息id
 */
public class MsgId {
    //-----------用户------
    //注册
    public static final int C_REG=10001;
    public static final int S_REG=10002;
    //登录
    public static final int C_LOGIN=10003;
    public static final int S_LOGIN=10004;
    //编辑用户信息
    public static final int C_EDIT=10005;
    public static final int S_EDIT=10006;
    //修改密码
    public static final int C_PWD=10007;
    public static final int S_PWD=10008;
    //绑定手机
    public static final int C_PHONE=10009;
    public static final int S_PHONE=10010;
    //重新连接
    public static final int C_RECONNECT=10011;
    public static final int S_RECONNECT=10012;
    //点卡生成-列表
    public static final int C_CODE_CREATE_LIST=10401;
    public static final int S_CODE_CREATE_LIST=10402;

    //点卡生成-生成
    public static final int C_CODE_CREATE_CREATE=10403;
    public static final int S_CODE_CREATE_CREATE=10404;

    //点卡查询
    public static final int C_CODE_QUERY=10405;
    public static final int S_CODE_QUERY=10406;


    //点卡分发-列表
    public static final int C_CODE_SEND_LIST=10407;
    public static final int S_CODE_SEND_LIST=10408;

    //点卡分发-分发
    public static final int C_CODE_SEND_SEND=10409;
    public static final int S_CODE_SEND_SEND=10410;

    //查询
    public static final int C_MGR_QUERY_QUERY =10411;
    public static final int S_MGR_QUERY_QUERY =10412;

    //查询 最近转出
    public static final int C_MGR_QUERY_OUT =10413;
    public static final int S_MGR_QUERY_OUT =10414;

    //查询 锁用户
    public static final int C_CODE_QUERY_LOCK=10415;
    public static final int S_CODE_QUERY_LOCK=10416;


    //银行显示界面
    public static final int C_BANK_INFO=10201;
    public static final int S_BANK_INFO=10202;

    //银行取出
    public static final int C_BANK_WITHDRAW=10203;
    public static final int S_BANK_WITHDRAW=10204;

    //银行存入
    public static final int C_BANK_DEPOSIT=10205;
    public static final int S_BANK_DEPOSIT=10206;

    //转账
    public static final int C_BANK_TRANSFER=10207;
    public static final int S_BANK_TRANSFER=10208;

    //转账
    public static final int C_BANK_TRANSFER_LOG=10211;
    public static final int S_BANK_TRANSFER_LOG=10212;

    //明细
    public static final int C_BANK_LOG=10213;
    public static final int S_BANK_LOG=10214;

    //邮件
    public static final int C_EMAIL_LIST=10301;
    public static final int S_EMAIL_LIST=10302;

    public static final int C_EMAIL_RECEIVE=10303;
    public static final int S_EMAIL_RECEIVE=10304;


    public static final int C_GM_ADDGOLD=10321;
    public static final int S_GM_ADDGOLD=10322;

    //-----------游戏-----
    //进入游戏
    public static final int C_INTOGAME=20001;
    public static final int S_INTOGAME=20002;

    //进入房间
    public static final int C_INTOROOM=20003;
    public static final int S_INTOROOM=20004;
    //游戏状态切换
    public static final int S_GAME_STATE=20006;

    //下注
    public static final int C_BET=20007;
    public static final int S_BET=20008;


    //-------进入游戏后的初始化显示
    public static final int C_ENTER_INIT=20101;
    public static final int S_ENTER_INIT=20102;


    //下注
    public static final int C_SLOT_BET=20103;
    public static final int S_SLOT_BET=20104;
    //退出房间
    public static final int C_OUT_ROON=20105;
    public static final int S_OUT_ROON=20106;

    //----捕鱼----
    //读取指定房间的捕鱼火力的配置
    public static final int C_FISH_INIT=20201;
    public static final int S_FISH_INIT=20202;

    //捕鱼-捕获到鱼
    public static final int C_FISH_GET=20203;
    public static final int S_FISH_GET=20204;

    //捕鱼-开火
    public static final int C_FISH_FIRE=20205;
    public static final int S_FISH_FIRE=20206;

    //九线拉王-幸运玩家
    public static final int C_LINE9_RANK=20301;
    public static final int S_LINE9_RANK=20302;


    //九线拉王-推送宝箱奖池数据
    public static final int S_LINE9_BOX_POOL=20304;


    //水浒传-比倍
    public static final int C_WM_DICE=20311;
    public static final int S_WM_DICE=20312;


    //僵尸新娘-抓鬼游戏-开始
    public static final int C_CORPSE_START=20321;
    public static final int S_CORPSE_START=20322;

    //僵尸新娘-抓鬼游戏-猜灯笼
    public static final int C_CORPSE_CATCH=20323;
    public static final int S_CORPSE_CATCH=20324;


}
