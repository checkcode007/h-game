package com.z.core.service.game.mali;

import com.z.common.util.SpringContext;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.game.game.SuperRound;
import com.z.core.service.game.slot.CSlotService;
import com.z.model.bo.mali.*;
import com.z.model.common.MsgResult;
//import com.z.model.mysql.cfg.CMali;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MaliRound extends SuperRound {
    protected Logger log = LoggerFactory.getLogger(getClass());
    List<Payline> paylines;
    List<Reel> reels;
    int reelSize = 5;           //滚轮个数
    int symbolSize = 3;         //每个滚轮上显示的个数
    CSlotService service;

    public MaliRound(long id ,CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        super(id,gameType,roomType);
        service = SpringContext.getBean(CSlotService.class);
    }

    public void init(List<Payline> paylines,List<Reel> reels,int symbolSize) {
        this.paylines = paylines;
        this.reels = reels;
        reelSize = reels.size();
        this.symbolSize = symbolSize;
    }

    @Override
    public MsgResult<BetResult> bet(long uid, int type, long gold,boolean free) {
        //下注
        MsgResult ret =  super.bet(uid,type, gold,free);
        if(!ret.isOk()){
            return ret;
        }
        //滚动
        List<List<CommonGame.Symbol>> list = spin();
        //检测结果
        WinResult winRet = checklines(list);
        //结算
        int rate = 0;
        for (WinOneLine win : winRet.getWins()) {
            CSlot slot = service.get(CommonGame.GameType.BAIBIAN_XIAOMALI,
                    win.getSymbol().getNumber(),win.getC());
            if(slot == null) continue;
            rate+= slot.getRate();
        }
        long bet = getBet(uid,0);
        bet = bet/ SpringContext.getBean(CCfgBizService.class).getBB_XML_bet_base();
        long rewardGold = bet*rate;
//        BetResult betRet = BetResult.builder().reels(winRet.getReels()).wins(winRet.getWins()).rate(rate).gold(rewardGold).build();
        BetResult betRet = new BetResult();
        betRet.setReels(winRet.getReels());
        betRet.setRate(rate);
        betRet.setGold(rewardGold);
        betRet.setWins(winRet.getWins());
        ret.ok(betRet);
        return ret;
    }
    public List<List<CommonGame.Symbol>> spin() {
        List<List<CommonGame.Symbol>> reelDisplays = new ArrayList<>();
        for (Reel reelTest : reels) {
            List<CommonGame.Symbol> list = new ArrayList<>(reelSize);
            for (int i = 0; i < symbolSize; i++) {
                list.add(reelTest.spin());
            }
            reelDisplays.add(list);
        }
        return reelDisplays;
    }
    // 检查支付线是否中奖
    public WinResult checklines(List<List<CommonGame.Symbol>> reelDisplays) {
        WinResult ret = new WinResult(reelDisplays);
        for (Payline payline: paylines) {
            int[] lines = payline.getPos();
            List<CommonGame.Symbol> lineSymbols = new ArrayList<>();
            for (int j = 0; j < lines.length; j++) {
                lineSymbols.add(reelDisplays.get(j).get(lines[j])); // 根据支付线位置取符号
            }
            log.info("line---->"+lineSymbols);
            int winC = lineWinCount(lineSymbols);
            if(winC<2){
                continue;
            }
            CommonGame.Symbol symbol = lineSymbols.get(0);
            WinOneLine oneLine =new WinOneLine(payline.getIndex(),symbol,winC);
            ret.addWin(oneLine);
        }
        return ret;
    }
    /**
     * 每条线上中奖的个数
     * @param lineSymbols
     * @return
     */
    private int lineWinCount(List<CommonGame.Symbol> lineSymbols) {
        CommonGame.Symbol first = lineSymbols.get(0);
        int index = 0;
        for (CommonGame.Symbol symbol : lineSymbols) {
            if (symbol != first) {
                return index;
            }
            index++;
        }
        return index;
    }
}
