package com.z.model.bo.majiang;

import com.z.model.proto.CommonGame;
import lombok.Data;

@Data
public class MJBo {
    CommonGame.MJ type;
    int x;
    int y;

    public MJBo(CommonGame.MJ type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "p{" +type.getNumber() + "x" + x + "y" + y + "}";

    }
}
