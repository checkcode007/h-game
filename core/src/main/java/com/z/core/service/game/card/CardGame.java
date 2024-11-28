package com.z.core.service.game.card;

import com.google.protobuf.ByteString;
import com.z.core.net.channel.UserChannelManager;
import com.z.model.bo.card.CardNN;
import com.z.model.bo.card.NiuResult;
import com.z.model.common.MsgId;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import com.z.model.type.PlayerState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardGame {
    private static final Logger logger = LogManager.getLogger(CardGame.class);
    private long id;
    private long roomId;
    private long round;
    private Deck deck;//牌组
    private CardPlayer banker; // 庄家
    private CommonGame.GameState state; // 当前游戏状态
    private Map<CommonGame.GameState, Integer> timeMap = new HashMap<>();
    private long time;
    private Map<Long, CardPlayer> playerMap = new ConcurrentHashMap<>();

    private Map<Long, Long> readyMap = new ConcurrentHashMap<>();

    private Game.SuitCard bankerCard;
    private Map<CommonGame.CardSuit, Game.SuitCard> cardPool = new ConcurrentHashMap<>();

    long bet=0;
    AtomicBoolean stop = new AtomicBoolean(false);

    /**
     * 每个玩家进来要等待下一场
     */
    public CardGame(long id, long roomId, CardPlayer banker, Map<CommonGame.GameState, Integer> timeMap) {
        this.id = id;
        this.roomId = roomId;
        this.banker = banker;
        this.deck = new Deck();
        this.state = CommonGame.GameState.WAITING_FOR_PLAYE;
        this.timeMap = timeMap;
        this.time = System.currentTimeMillis() + timeMap.getOrDefault(state, 0) * 1000;
        this.stop.getAndSet(false);
    }
    public void nextRound(){
        round++;
        round = round<0?1:round;
    }
    public void addReady(long uid) {
        readyMap.put(uid, System.currentTimeMillis());
        if(stop.get()){
            stop.getAndSet(false);
            this.time = System.currentTimeMillis() + timeMap.getOrDefault(state, 0) * 1000;
        }

    }

    public void removeReady(long uid) {
        readyMap.remove(uid);
    }

    public void addPlayer(long uid) {
        CardPlayer player = new CardPlayer(uid, 1000, true,false);
        playerMap.put(player.getUid(), player);
    }

    public void removePlayer(long uid) {
        CardPlayer player = playerMap.get(uid);
        if (player == null) return;
        player.setPlayerState(PlayerState.QUIT);
    }


    public void addBet(long uid, CommonGame.CardSuit suit, long gold) {
        CardPlayer player = playerMap.get(uid);
        player.placeBet(suit, gold);
        bet+=gold;
    }

    public void exe(long now) {
        try {
            exeDo(now);
        } catch (Exception e) {
            logger.error("id:{} roomId:{}", id, roomId, e);
        }
    }

    public void exeDo(long now) {
        if (now < time) return;
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("state1" + state).add("stop:"+stop);
        logger.info(sj.add("start").toString());
        if(stop.get()){
            return;
        }
        state = CommonGame.GameState.forNumber(state.getNumber() + 1);
        if (state == null) {
            state = CommonGame.GameState.WAITING_FOR_PLAYE;
        }
        time = now + timeMap.getOrDefault(state, 0) * 1000;

        sj.add("state2:" + state +" players--->"+playerMap.size());
        logger.info(sj.toString());
        Game.S_20006 stateMsg = Game.S_20006.newBuilder().setState(state).setGameId(id).setRoomId(roomId).setLeaveTime(getLeaveTime()).build();
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_GAME_STATE).setOk(true);
        res.addMsg(ByteString.copyFrom(stateMsg.toByteArray()));
        for (CardPlayer player : playerMap.values()) {
            UserChannelManager.sendMsg(player.getUid(), res.build());
        }
        switch (state) {
            case CommonGame.GameState.WAITING_FOR_PLAYE:
                ready();//准备玩家加入游戏
                break;
            case CommonGame.GameState.BETTING:
                bet();//下注
                break;
            case CommonGame.GameState.DEALING:
                deal();//发牌
                break;
            case CommonGame.GameState.RESULT:
                calculateResults();//计算结果
                gameOver();//计算结果
                break;

        }
        logger.info(sj.add("sucess").toString());
    }
    /**
     * 获取下一局的时间
     * @return
     */
    public int getNextTime(){
        long diff = 0l;

//        if(CommonGame.GameState.WAITING_FOR_PLAYE == state){
//            diff =time - System.currentTimeMillis();
//        }else{
            diff =time - System.currentTimeMillis();
            for (CommonGame.GameState value : CommonGame.GameState.values()) {
                if(value == CommonGame.GameState.UNRECOGNIZED) continue;
                if(value.getNumber()>state.getNumber()){
                    diff+=timeMap.getOrDefault(value.getNumber(),0)*1000;
                }
            }
//        }
        diff = diff<0?0:diff;
        return  (int) (diff / 1000);
    }
    public int getLeaveTime() {
        long diff =time - System.currentTimeMillis();
        diff = diff<0?0:diff;
        return  (int) (diff / 1000);
    }
    public void ready() {
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("round:"+round);;
        logger.info(sj.add("readys:"+readyMap.size()).add("start").toString());
        for (Long uid : readyMap.keySet()) {
            addPlayer(uid);
        }
        List<Long> delList = new ArrayList<>();
        for (CardPlayer player : playerMap.values()) {
            if (player.getPlayerState() == PlayerState.QUIT) {
                delList.add(player.getUid());
            } else {
                player.setPlayerState(PlayerState.INGAME);
            }
        }
        for (Long id : delList) {
            playerMap.remove(id);
        }
        logger.info(sj.add("players:"+playerMap.size()).add("end").toString());
    }

    public void bet() {
        //通知玩家下注
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("round:"+round);;
        logger.info(sj.add("start").toString());
        logger.info(sj.add("end").toString());
    }

    public void deal() {
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("round:"+round);;
        logger.info(sj.add("start").toString());
        deck.shuffle(); // 洗牌
        dealCards(); // 发牌
        logger.info(sj.add("end").toString());

    }

    //todo 发牌逻辑
    private void dealCards() {
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("round:"+round);;
        logger.info(sj.add("start").toString());
        //庄家发牌
        Game.SuitCard.Builder b = Game.SuitCard.newBuilder().setSuit(CommonGame.CardSuit.Hearts);
        for (int i = 0; i < 5; i++) {
            CommonGame.Card card = deck.dealCard();
            b.addCards(card);
        }
        bankerCard = b.build();
        //玩家发牌
        for (CommonGame.CardSuit suit : CommonGame.CardSuit.values()) {
            if (suit == CommonGame.CardSuit.BigJoker || suit == CommonGame.CardSuit.SmallJoker || suit == CommonGame.CardSuit.UNRECOGNIZED)
                continue;
            Game.SuitCard.Builder bb = Game.SuitCard.newBuilder().setSuit(suit);
            for (int i = 0; i < 5; i++) {
                bb.addCards(deck.dealCard());
            }
            cardPool.put(suit, bb.build());
        }
        logger.info(sj.add("end").toString());
    }

    //todo 输赢的概率，倍数，每个池子的倍率
    private void calculateResults() {
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("round:"+round);;
        logger.info(sj.add("start").toString());
        //庄家
        List<CardNN> list = new ArrayList<>();
        for (CommonGame.Card card : bankerCard.getCardsList()) {
            list.add(new CardNN(card));
        }
        NiuResult bankerRes = CardUtils.calculateNiuNiu(list);
        CommonGame.NiuType bankerType = bankerRes.getType();

        //玩家
        Map<CommonGame.CardSuit, Game.SuitResult.Builder> winMap = new HashMap<>();
        Iterator<Map.Entry<CommonGame.CardSuit, Game.SuitCard>> iter = cardPool.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<CommonGame.CardSuit, Game.SuitCard> entry = iter.next();
            CommonGame.CardSuit suit = entry.getKey();
            Game.SuitCard suitCard = entry.getValue();
            List<CardNN> list1 = new ArrayList<>();
            for (CommonGame.Card card : suitCard.getCardsList()) {
                list1.add(new CardNN(card));
            }
            NiuResult res = CardUtils.calculateNiuNiu(list1);
            CommonGame.NiuType niuType = res.getType();
            Game.SuitResult.Builder builder = Game.SuitResult.newBuilder().setSuit(suit).setNiu(niuType).addAllCards(suitCard.getCardsList());
            if (niuType.getNumber() > bankerType.getNumber()) {
                builder.setState(CommonGame.WinState.WIN);
            } else if (niuType.getNumber() < bankerType.getNumber()) {
                builder.setState(CommonGame.WinState.FAIL);
            } else {
                builder.setState(CommonGame.WinState.DRAW);
            }
            winMap.put(suit, builder);
        }
        for (CardPlayer player : playerMap.values()) {
            player.getBetMap().forEach((suit, gold) -> {
                Game.SuitResult.Builder b = winMap.get(suit);
                b.setGold(b.getGold() + gold);
            });
        }
        //组织结果
        Game.SuitResult.Builder bankerResultBuilder = Game.SuitResult.newBuilder().addAllCards(bankerCard.getCardsList()).setNiu(bankerType);

        //广播
        for (CardPlayer player : playerMap.values()) {
            Game.S_20012.Builder builder = Game.S_20012.newBuilder().setRoomId(roomId).setGameId(id).setGold(player.getGold()).setBankerCards(bankerResultBuilder.build());
            for (Game.SuitResult.Builder b : winMap.values()) {
                builder.addHardCards(b.build());
            }
            MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_GAME_STATE).setOk(true);
            res.addMsg(ByteString.copyFrom(builder.build().toByteArray()));
            UserChannelManager.sendMsg(player.getUid(), res.build());
        }
        logger.info(sj.add("end").toString());
    }

    private int getMultiplier(int playerValue) {
        // 根据牌型返回不同的赔率倍数
        // ...
        return 1; // 示例
    }

    private void gameOver() {
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roomId:" + roomId).add("round:"+round);
        logger.info(sj.add("start").toString());

        for (CardPlayer player : playerMap.values()) {
            if (player.getPlayerState() == PlayerState.QUIT) continue;
            player.reset();
        }
        bankerCard = null;
        cardPool.clear();
        deck = new Deck();
        bet = 0;
        nextRound();
        if(isEmpty()){
            stop.getAndSet(true);
        }
        logger.info(sj.add("end").toString());
    }

    public boolean isEmpty(){
        boolean isEmpty1 = playerMap.isEmpty();
        boolean isEmpty2 = readyMap.isEmpty();
        if(isEmpty2){
            for (CardPlayer p : playerMap.values()) {
                if(!p.isRobot()){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public Deck getDeck() {
        return deck;
    }

    public CardPlayer getBanker() {
        return banker;
    }

    public CommonGame.GameState getState() {
        return state;
    }

    public long getTime() {
        return time;
    }

    public long getRoomId() {
        return roomId;
    }

    public long getRound() {
        return round;
    }

    public long getBet() {
        return bet;
    }

    public Map<Long, CardPlayer> getPlayerMap() {
        return playerMap;
    }
}
