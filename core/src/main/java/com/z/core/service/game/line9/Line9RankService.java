package com.z.core.service.game.line9;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.dbmysql.dao.line9.GLine9RankDao;
import com.z.model.common.MsgId;
import com.z.model.mysql.GLine9Rank;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Service
public class Line9RankService {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(Line9RankService.class);

    @Autowired
    GLine9RankDao dao;

    public void add(long uid,long gold) {
        GLine9Rank rank = dao.findById(uid);
        if(rank == null){
            rank = new GLine9Rank();
            rank.setId(uid);
            rank.setGold(gold);
            rank.setLastTime(new Date());
            dao.save(rank);
        }else{
            rank.setGold(rank.getGold()+gold);
            rank.setLastTime(new Date());
            dao.update(rank);
        }
    }
    public List<GLine9Rank> getList(int size){
        return dao.getTop("gold desc", size);
    }

    public AbstractMessageLite getRank(long uid){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_LINE9_RANK).setOk(true);
        Game.S_20302.Builder b = Game.S_20302.newBuilder();
        List<GLine9Rank> list = getList(20);
        if(list!=null){
            for (GLine9Rank e : list) {
                b.addLucks(Game.Line9Luck.newBuilder().setUid(e.getId()).setGold(e.getGold()).setTime(e.getLastTime().getTime()).build());
            }
        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }
}
