package com.z.core.net.handler;

import com.z.model.proto.Game;
import com.z.model.proto.User;
import com.z.model.type.MsgEnum;
import lombok.Data;

@Data
public class MsgData {
    private MsgEnum.MsgType type;
    private User.UserMsg userMsg;
    private Game.GameMsg gameMsg;
}
