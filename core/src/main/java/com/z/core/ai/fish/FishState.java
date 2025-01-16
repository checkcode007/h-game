package com.z.core.ai.fish;

import com.z.core.ai.SuperState;
import com.z.model.BetParam;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.StringJoiner;

/*
/*
中奖概率计算公式：

中奖概率 = P_鱼 × P_炮弹 × (1 + 修正因子)

公式解释：
1. P_鱼：鱼的捕获基础概率，不同类型的鱼具有不同概率。
2. P_炮弹：炮弹的基础命中概率，低等级炮弹命中率高，高等级炮弹命中率低但通常对应更高奖励。
3. 修正因子：
   修正因子 = C1 × (loseCount - winCount) + C2 × currentBet + C3

修正因子具体说明：
1. (loseCount - winCount)：输赢次数差，玩家连续输多时修正因子为正，有助于提高中奖概率；
   玩家赢多时修正因子为负，降低中奖概率，达到平衡效果。
2. C1：输赢次数影响的平衡系数，控制输赢对概率修正的影响强度。
3. currentBet：玩家当前投注金额，投注越高可能获得的修正值越高。
4. C2：投注金额影响的平衡系数，控制投注金额对概率修正的影响强度。
5. C3：基础修正因子，确保初始概率有一个合理偏移。

重要调整：
1. 如果 winLoseFactor 为负值可能大幅降低中奖概率，建议对其进行限制：
   - 限制最低值，例如：winLoseFactor = Math.max(winLoseFactor, -0.5)
   - 或使用平滑调整公式，例如：winLoseFactor = C1 × tanh(loseCount - winCount)
2. 修正后概率的最终值需限制在 [0, 1] 范围内，以避免异常行为。
 */
public class FishState extends SuperState {
    private static final Log log = LogFactory.getLog(FishState.class);
    private Random random = new Random();

    // 平衡系数
    protected double C1 = 0.05;  // 输赢影响系数
    protected double C2 = 0.01;  // 投入金额影响系数
    protected double C3 = 0.1;   // 基础修正因子


    public FishState(SlotState k) {
        super(k);
    }

    // 动态调整修正因子
    private double calculateAdjustmentFactor(int winCount, int loseCount) {
        double winLoseFactor = C1 * (loseCount - winCount); // 输赢动态调整
//        double betFactor = C2 * currentBet; // 根据当前投入调整
        log.info("winLoseFactor :"+ winLoseFactor);
        return winLoseFactor + C3; // 总修正
    }

    // 计算单次捕获概率
    public double calculateWinProbability(double bulletProbability, double fishProbability, int winCount, int loseCount) {
        double adjustmentFactor = calculateAdjustmentFactor(winCount, loseCount);
        double radio = Math.min(1.0, fishProbability * bulletProbability * (1 + adjustmentFactor)); // 确保概率不超过1
        log.info("radio :"+ radio+" adjust:"+adjustmentFactor
                +"fish:"+fishProbability+"bullet:"+bulletProbability);
        return radio;
    }

    // 模拟捕获一条鱼
    public boolean catchFish(BetParam param,int fishType,double fish, double bullet) {
        long winC =param.getRoomWinC();
        long totalC =param.getRoomTotalC();
        long lossC = totalC - winC;
        // 计算捕获概率
        double winProbability = calculateWinProbability(bullet, fish, (int)winC,(int)lossC);
        // 根据概率决定是否成功
        double randomNum = random.nextDouble();
        boolean isWin = randomNum < winProbability;
        StringJoiner sj = new StringJoiner(",");
        sj.add("win:"+isWin).add("random:"+randomNum).add("winP:"+winProbability)
                .add("winC:"+winC).add("lossC:"+lossC);
        log.info(sj.toString());
        // 更新输赢次数
        if (isWin) {
            return true;
        } else {
            return false;
        }
    }

}
