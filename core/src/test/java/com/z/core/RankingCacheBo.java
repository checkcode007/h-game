package com.z.core;

public class RankingCacheBo {
    private int type;
    private long id;
    private long uid;
    private long roomId;
    private long cfgId;
    private RankScore rankScore = new RankScore(0, 0, 0, 0);
    public void addScore(int type,double score) {
        if(type == RankingScoreType.SCORE.getCode()) {
            rankScore.setScore(score+rankScore.getScore());
        } else if (type == RankingScoreType.DIAMOND.getCode()) {
            rankScore.setDiamond(rankScore.getDiamond() + score);
        } else if (type == RankingScoreType.PT.getCode()) {
            rankScore.setPt(rankScore.getPt() + score);
        }else if (type == RankingScoreType.PAY.getCode()) {
            rankScore.setPay(rankScore.getPay() + score);
        }
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getCfgId() {
        return cfgId;
    }

    public void setCfgId(long cfgId) {
        this.cfgId = cfgId;
    }

    public RankScore getRankScore() {
        return rankScore;
    }

    public void setRankScore(RankScore rankScore) {
        this.rankScore = rankScore;
    }

    @Override
    public String toString() {
        return "RankingCacheBo{" +
                "type=" + type +
                ", id=" + id +
                ", uid=" + uid +
                ", roomId=" + roomId +
                ", cfgId=" + cfgId +
                ", rankScore=" + rankScore +
                '}';
    }
}
