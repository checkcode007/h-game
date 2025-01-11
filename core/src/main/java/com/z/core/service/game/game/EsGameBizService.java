package com.z.core.service.game.game;

import com.z.common.util.DateTimeUtil;
import com.z.common.util.SnowflakeId;
import com.z.core.service.game.card.CardGame;
import com.z.core.service.game.card.CardPlayer;
import com.z.core.service.game.line9.Line9RankService;
import com.z.dbes.service.EsGameService;
import com.z.model.es.EsGame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.StringJoiner;

/**
 *游戏管理类
 */
@Service
public class EsGameBizService {
    private static final Log log = LogFactory.getLog(EsGameBizService.class);

//    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    EsGameService esGameService;
    public EsGame add(CardGame cardGame) {
       long uid = cardGame.getBanker().getUid();
       long roomId = cardGame.getRoomId();
       long id = cardGame.getId();
       long round = cardGame.getRound();
       long bet = cardGame.getBet();
       Collection<CardPlayer> coll = cardGame.getPlayerMap().values();
        StringJoiner players = new StringJoiner(",");
        for (CardPlayer player : coll) {
            players.add(player.getUid()+"");
        }
        return add(uid,roomId,id,round,bet,coll.size(),players.toString());
    }
    //todo 补充 房间配置id，游戏类型
    public EsGame add(long uid,long roomId,long gameId,long roundId,long bet,int cout,String players) {
        DateTime now = DateTime.now();
        long t = now.getMillis();
        String d = DateTimeUtil.format(now);
        long id = SnowflakeId.ins.snowflakeId(1,1);
        String s_id = id+"";
        EsGame gGame = new EsGame();
        gGame.setDealerId(uid);
        gGame.setId(s_id);
        gGame.setGameId(gameId);
        gGame.setRoomId(roomId);
        gGame.setRound(roundId);
        gGame.setCout(cout);
        gGame.setPlayers(players);
        gGame.setRoomId(roomId);
        gGame.setDay(DateTimeUtil.getDateShortInt(now));
        gGame.setBet(bet);
        gGame.setT(t);
        gGame.setD(d);
        esGameService.add(s_id,gGame);
        return gGame;
    }


}
