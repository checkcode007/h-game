package com.z.core.service.game.card;

import com.z.common.util.SnowflakeId;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.game.game.EsGameBizService;
import com.z.core.service.user.UserBizService;
import com.z.model.mysql.GUser;
import com.z.model.proto.CommonGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CardService {
    private static final Logger log = LogManager.getLogger(CardService.class);
    @Autowired
    UserBizService userBizService;
    @Autowired
    CCfgBizService cfgBizService;
    @Autowired
    EsGameBizService esGameBizService;

    Map<Long,CardGame> map = new ConcurrentHashMap<>();
    Map<Long,List<CardGame>> roomMap = new ConcurrentHashMap<>();

    public CardGame createGame(long gameId,long roomId){
        List<GUser> robots = userBizService.getRobot(1);
        GUser robot = robots.get(0);
        CardPlayer banker = new CardPlayer(robot.getId(),1000,true,false);
        long id = SnowflakeId.ins.gameId();
        CardGame cardGame = new CardGame(id,roomId,banker,cfgBizService.getNiuniuTime());
        map.put(id,cardGame);
        List<CardGame> list = roomMap.getOrDefault(roomId,new ArrayList<>());
        list.add(cardGame);
        roomMap.putIfAbsent(roomId,list);
        return cardGame;
    }
    public  void remove(long id){
       CardGame cardGame =  map.remove(id);
       roomMap.remove(cardGame.getRoomId());
    }
    public CardGame into(long uid,long gameId,long roomId){
        CardGame game = map.get(gameId);
        if(game == null){
            game = createGame(gameId,roomId);
            map.put(game.getId(),game);
        }
        game.addReady(uid);
        return game;
    }
    public void out(long uid,long gameId){
        CardGame game = map.get(gameId);
        if(game!=null){
            game.removeReady(uid);
        }
        for (CardGame g : map.values()) {
            g.removeReady(uid);
        }
    }
    public boolean bet(long uid, long roomId, long gameId, CommonGame.CardSuit suit, long gold){
        CardGame cardGame = map.get(gameId);
        if(cardGame == null){
            log.error("cardGame is null");
            return false;
        }
        cardGame.addBet(uid,suit,gold);
        return true;
    }
    public void exe(){
        if(map.isEmpty()){
            return;
        }
        long  now = System.currentTimeMillis();
        Iterator<CardGame> iter = map.values().iterator();
        while(iter.hasNext()){
            CardGame game = iter.next();
            try {
                game.exe(now);
                gameOver(game);
            } catch (Throwable e) {
               log.error("exe fail",e);
            }
        }
    }
    public void gameOver(CardGame game){
        if(game.getState() != CommonGame.GameState.RESULT) return;
        esGameBizService.add(game);
    }
    public CardGame get(long id){
        return map.get(id);
    }
    public List<CardGame> getByRoom(long roomId){
        return roomMap.get(roomId);
    }
    public CardGame getRandomRoom(long roomId){
        List<CardGame> list = roomMap.get(roomId);
        if(list == null|| list.isEmpty()) return null;
        Collections.shuffle(list);
        return list.get(0);
    }

}
