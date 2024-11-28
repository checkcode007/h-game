package com.z.core.net.handler;

import com.z.model.common.MsgId;
import com.z.model.proto.Game;
import com.z.model.proto.User;

import java.util.HashMap;
import java.util.Map;

public enum MsgManger {
    ins;
    Map<Integer,Class> map = new HashMap<>();
    public void init(){
        if(!map.isEmpty()) return;
        map.put(MsgId.C_REG, User.C_10001.class);
        map.put(MsgId.S_REG, User.S_10002.class);
        map.put(MsgId.C_LOGIN, User.C_10003.class);
        map.put(MsgId.S_LOGIN,User.S_10004.class);
        map.put(MsgId.C_EDIT, User.C_10005.class);
        map.put(MsgId.S_EDIT,User.S_10006.class);
        map.put(MsgId.C_INTOGAME, Game.C_20001.class);
        map.put(MsgId.S_INTOGAME, Game.S_20002.class);
        map.put(MsgId.C_INTOROOM, Game.C_20003.class);
        map.put(MsgId.S_INTOROOM, Game.S_20004.class);
        map.put(MsgId.C_BET, Game.C_20007.class);
        map.put(MsgId.S_BET, Game.S_20008.class);
    }

    public  Class get(int msgId){
        init();
        return map.get(msgId);
    }

    public static void main(String[] args) {
        System.err.println(User.C_10001.class);
    }
}
