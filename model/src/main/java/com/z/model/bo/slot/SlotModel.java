package com.z.model.bo.slot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每一个slot对象
 * @param
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotModel {
    int type; //符号
    int x; // 第几列
    int y; //列的第几个
    boolean gold;//是否是金色牌
    int changeType;//变化的类型

}
