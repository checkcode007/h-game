package com.z.core.test1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

//"送礼请求"

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiveGiftsBo implements Serializable {
    private static final long serialVersionUID = 8196260214622690232L;
    //送礼用户ID
    long userId;
    String nickName;
   //"接收人列表"
    List<Long> toUserIds;

    Map<Long,Boolean> realIds;

    //原生name 多语言key
    String gName;
    long giftId;
    int gDiamondPrice;
    /**金币价格**/
    int gCoinPrice;

    /**
     * 送礼物数量
     */
    int gCount;

    /**
     * 送礼物，获得个人经验值
     */
    int gExp;

    /**用户赠送获得财富值*/
    int gwExp;

    /**
     * 真人认证获得P币
     */
    long baoxiangrealPocket;
    /**
     * 真人认证获得P币
     */
    long realPocket;
    /**
     * 非真人认证获得P币
     */
    long noRealPocket;

    /**
     * 赠送礼物，获得魅力值
     */
    long glamour;

    /**攻击力*/
    int gAttack;
    /**礼物utl*/
    String giftUrl;
    /**
     * 礼物动效显示层级（大的在上面）1\2\3
     */
    int gHierarchy;
    /***
     * 礼物记录ID userId,giftRecordId
     */
    Map<Long,String> giftRecordIds;
    /**
     * 1: 送礼物类型
     * 2:房间大礼物映射关系 0-普通 1宝箱送礼-礼物，2宝箱送礼-装备 3-GreedyBaby 4-普通多人(v2版)
     *  0-普通 => 4-普通多人(v2) 在业务逻辑中重新映射
     */
     int type;
     //背包礼物
     boolean bagGift = false;
    //客户端版本
     String clientVer;
    //"在房间送礼的房间ID"
    long roomId;
    //发送大礼物消息临时用
    String roomName;
    /**真实攻击力*/
     int realAttack;
   /**
    * 对应排行榜的值
    */
   int rankingListVal;


    //是否全服广播 1是
    int serverBroadcast;
    /**
     * 装备稀有度  1  粗糙   2  普通  3 优秀  4  精良 5  史诗  6  传说 7  神话
     */
    private int gQuality;
   // "来源0 默认 1背包礼物  2合并攻击"
    int source;
   /**
    * 宝箱id
    */
   long baoxiangId = 0L;
    /**
     * 宝箱id
     */
    long bxId = 0L;
    /**
     * 宝箱
     */
    int bxDiamondP = 0;
    /**
     * 宝箱
     */
    int bxCoinP = 0;
    /**
     * 是否装备，是则不走奖励  DivideIntoPocketBizService#receivedGetPocket
     */
    boolean isKnapsack = false;



   /** "1v1语音通话ID"*/
    String callId;
    long lastTime;
    String ipCC;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<Long> getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(List<Long> toUserIds) {
        this.toUserIds = toUserIds;
    }

    public Map<Long, Boolean> getRealIds() {
        return realIds;
    }

    public void setRealIds(Map<Long, Boolean> realIds) {
        this.realIds = realIds;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public int getgDiamondPrice() {
        return gDiamondPrice;
    }

    public void setgDiamondPrice(int gDiamondPrice) {
        this.gDiamondPrice = gDiamondPrice;
    }

    public int getgCoinPrice() {
        return gCoinPrice;
    }

    public void setgCoinPrice(int gCoinPrice) {
        this.gCoinPrice = gCoinPrice;
    }

    public int getgCount() {
        return gCount;
    }

    public void setgCount(int gCount) {
        this.gCount = gCount;
    }

    public int getgExp() {
        return gExp;
    }

    public void setgExp(int gExp) {
        this.gExp = gExp;
    }

    public int getGwExp() {
        return gwExp;
    }

    public void setGwExp(int gwExp) {
        this.gwExp = gwExp;
    }

    public long getBaoxiangrealPocket() {
        return baoxiangrealPocket;
    }

    public void setBaoxiangrealPocket(long baoxiangrealPocket) {
        this.baoxiangrealPocket = baoxiangrealPocket;
    }

    public long getRealPocket() {
        return realPocket;
    }

    public void setRealPocket(long realPocket) {
        this.realPocket = realPocket;
    }

    public long getNoRealPocket() {
        return noRealPocket;
    }

    public void setNoRealPocket(long noRealPocket) {
        this.noRealPocket = noRealPocket;
    }

    public long getGlamour() {
        return glamour;
    }

    public void setGlamour(long glamour) {
        this.glamour = glamour;
    }

    public int getgAttack() {
        return gAttack;
    }

    public void setgAttack(int gAttack) {
        this.gAttack = gAttack;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl) {
        this.giftUrl = giftUrl;
    }

    public int getgHierarchy() {
        return gHierarchy;
    }

    public void setgHierarchy(int gHierarchy) {
        this.gHierarchy = gHierarchy;
    }

    public Map<Long, String> getGiftRecordIds() {
        return giftRecordIds;
    }

    public void setGiftRecordIds(Map<Long, String> giftRecordIds) {
        this.giftRecordIds = giftRecordIds;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isBagGift() {
        return bagGift;
    }

    public void setBagGift(boolean bagGift) {
        this.bagGift = bagGift;
    }

    public String getClientVer() {
        return clientVer;
    }

    public void setClientVer(String clientVer) {
        this.clientVer = clientVer;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRealAttack() {
        return realAttack;
    }

    public void setRealAttack(int realAttack) {
        this.realAttack = realAttack;
    }

    public int getRankingListVal() {
        return rankingListVal;
    }

    public void setRankingListVal(int rankingListVal) {
        this.rankingListVal = rankingListVal;
    }

    public int getServerBroadcast() {
        return serverBroadcast;
    }

    public void setServerBroadcast(int serverBroadcast) {
        this.serverBroadcast = serverBroadcast;
    }

    public int getgQuality() {
        return gQuality;
    }

    public void setgQuality(int gQuality) {
        this.gQuality = gQuality;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public long getBaoxiangId() {
        return baoxiangId;
    }

    public void setBaoxiangId(long baoxiangId) {
        this.baoxiangId = baoxiangId;
    }

    public long getBxId() {
        return bxId;
    }

    public void setBxId(long bxId) {
        this.bxId = bxId;
    }

    public int getBxDiamondP() {
        return bxDiamondP;
    }

    public void setBxDiamondP(int bxDiamondP) {
        this.bxDiamondP = bxDiamondP;
    }

    public int getBxCoinP() {
        return bxCoinP;
    }

    public void setBxCoinP(int bxCoinP) {
        this.bxCoinP = bxCoinP;
    }

    public boolean isKnapsack() {
        return isKnapsack;
    }

    public void setKnapsack(boolean knapsack) {
        isKnapsack = knapsack;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getIpCC() {
        return ipCC;
    }

    public void setIpCC(String ipCC) {
        this.ipCC = ipCC;
    }
}
