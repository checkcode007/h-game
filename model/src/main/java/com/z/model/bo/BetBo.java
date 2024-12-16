package com.z.model.bo;

import lombok.Data;

@Data
public class BetBo {
    /**
     * 下注类型
     */
    int type;
    /**
     * 下注金额
     */
    long gold;
}
