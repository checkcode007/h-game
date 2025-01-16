package com.z.core.ai;

import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.type.SlotState;

/*
玩家状态切换逻辑设计（适用于 Slot 游戏通用设计）

1. 状态分类：
   - 高概率状态（High）：玩家长期处于输多赢少，或投注金额远高于赢取金额的情况。
   - 中概率状态（Medium）：玩家状态默认值，输赢次数和金额较为平衡。
   - 低概率状态（Low）：玩家连续赢多输少，或赢取金额显著高于投注金额时。

2. 状态判定规则：
   通过玩家输赢次数和输赢金额切换状态。状态计算公式如下：
   状态值 = C1 × (loseCount - winCount) + C2 × (betAmount - winAmount)

   参数说明：
   - loseCount：玩家的总输次数。
   - winCount：玩家的总赢次数。
   - betAmount：玩家的总投注金额。
   - winAmount：玩家的总赢取金额。
   - C1：控制输赢次数差对状态影响的权重系数。
   - C2：控制输赢金额差对状态影响的权重系数。

   状态切换阈值：
   - 当 状态值 > T1：切换至高概率状态（High）。
   - 当 T2 <= 状态值 <= T1：保持在中概率状态（Medium）。
   - 当 状态值 < T2：切换至低概率状态（Low）。

   - T1 和 T2 为状态切换的阈值，根据业务需求设定，例如：
     T1 = 50.0，T2 = -50.0。

3. 状态倍率设计：
   每种状态对应不同的中奖概率倍率，用于动态调整玩家的中奖概率或奖励：
   - 高概率状态：倍率因子 = 1.2（更高中奖概率或奖励）。
   - 中概率状态：倍率因子 = 1.0（默认概率或奖励）。
   - 低概率状态：倍率因子 = 0.8（更低中奖概率或奖励）。

4. 状态设计的主要目标：
   - 动态平衡用户体验：保证长期输赢的玩家可以获得适当的调整，以增加留存率和参与度。
   - 控制整体 RTP（理论返回率）：通过调整状态的阈值、倍率等参数，实现系统 RTP 的合理控制。
   - 状态通用性：所有类型的 Slot 游戏共享同一套用户状态逻辑，可适配不同玩法。

5. 实现逻辑建议：
   - 每次游戏后，根据玩家最新的输赢次数和金额实时计算状态值，并检查是否需要切换状态。
   - 将状态、输赢次数和金额持久化存储，确保断线重连后仍可基于玩家历史数据计算状态。
   - 在计算最终中奖概率时，综合玩家状态倍率因子进行调整，例如：
     最终中奖概率 = 基础中奖概率 × 状态倍率因子。

6. 代码扩展建议：
   - 增加日志记录，监控不同玩家状态的切换频率与 RTP 数据。
   - 配置化管理状态参数（如 T1、T2、C1、C2 等），通过配置中心实现实时动态调整。
   - 在高概率状态下，引入额外奖励机制（如 Jackpot）以提高玩家兴奋感和参与度。
*/
public class PlayerStateController {

    // 状态阈值和倍率因子
    private static final double T1 = 50.0;  // 高概率状态阈值
    private static final double T2 = -50.0; // 低概率状态阈值

    // 平衡系数
    private static final double C1 = 0.5;  // 输赢次数差的权重
    private static final double C2 = 10.0;  // 输赢金额差的权重

    // 计算状态值
    private static double calculateStateValue(long winCount, long loseCount, double betAmount, double winAmount) {
        return C1 * (loseCount - winCount) + C2 * (betAmount - winAmount);
    }

    // 判定玩家状态
    public static SlotState determineState(long winCount, long loseCount, double betAmount, double winAmount) {
        double stateValue = calculateStateValue(winCount, loseCount, betAmount, winAmount);

        if (stateValue > T1) {
            return SlotState.HIGH_BET;
        } else if (stateValue >= T2) {
            return SlotState.MEDIUM_BET;
        } else {
            return SlotState.LOW_BET;
        }
    }
    public static void reload(Wallet wallet,User user) {
        SlotState state  = SlotState.MEDIUM_BET;
        if(wallet != null){
            long winC = wallet.getWins();
            long loss = wallet.getBetC() - wallet.getWins();
            long betAmount = wallet.getBetGold();
            long winAmount = wallet.getWinGold();
            state = determineState(winC,loss,betAmount,winAmount);
        }
        user.setSlotState(state);
    }


    public static void main(String[] args) {
        PlayerStateController controller = new PlayerStateController();

        // 测试参数
        int winCount = 10;
        int loseCount = 100000;
        double betAmount = 1000.0;
        double winAmount = 2000.0;

        // 计算中奖概率
        SlotState state  = controller.determineState(winCount, loseCount, betAmount, winAmount);

        System.err.println("Player State: " + state);
    }

}
