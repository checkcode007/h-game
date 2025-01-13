package com.z.core;

public class RankingBo {
    public long uid;
    public long roomId;
    public String name;
    public int type;
    public double score;
    public long itemId;
    public long lastTime;
    public long pkId;
    public long cfgId;
    public int hallStage;
    public int hallGroup;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getPkId() {
        return pkId;
    }

    public void setPkId(long pkId) {
        this.pkId = pkId;
    }

    public long getCfgId() {
        return cfgId;
    }

    public void setCfgId(long cfgId) {
        this.cfgId = cfgId;
    }

    public int getHallStage() {
        return hallStage;
    }

    public void setHallStage(int hallStage) {
        this.hallStage = hallStage;
    }

    public int getHallGroup() {
        return hallGroup;
    }

    public void setHallGroup(int hallGroup) {
        this.hallGroup = hallGroup;
    }
// Getters and Setters

    @Override
    public String toString() {
        return "RankingBo{" +
                "uid=" + uid +
                ", roomId=" + roomId +
                ", name=" + name +
                ", type=" + type +
                ", score=" + score +
                ", itemId=" + itemId +
                ", lastTime=" + lastTime +
                ", pkId=" + pkId +
                ", cfgId=" + cfgId +
                ", hallStage=" + hallStage +
                ", hallGroup=" + hallGroup +
                '}';
    }
}
