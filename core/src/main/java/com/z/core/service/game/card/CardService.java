package com.z.core.service.game.card;

import com.z.common.util.SnowflakeId;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.game.game.EsGameBizService;
import com.z.core.service.user.UserBizService;
import com.z.core.service.user.UserService;
import com.z.core.util.SpringContext;
import com.z.model.mysql.GUser;
import com.z.model.proto.CommonGame;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CardService {
    protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
//    private static final Logger log = LogManager.getLogger(CardService.class);
    @Autowired
    UserBizService userBizService;
    @Autowired
    EsGameBizService esGameBizService;

    Map<Long,CardGame> map = new ConcurrentHashMap<>();
    Map<Long,List<CardGame>> roomMap = new ConcurrentHashMap<>();

    public CardGame createGame(long gameId, long roomId, CommonGame.RoomType roomType, CommonGame.GameType gameType) {
        List<GUser> robots = UserService.ins.findRobot(1);
        GUser robot = robots.get(0);
        CardPlayer banker = new CardPlayer(robot.getId(),1000,true,false);
        long id = SnowflakeId.ins.gameId();
        CardGame cardGame = new CardGame(id,roomId,roomType,gameType);

        cardGame.init(banker,CCfgBizService.ins.getNiuniuTime());
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
    public CardGame enter(long uid, long gameId, long roomId, CommonGame.RoomType roomType, CommonGame.GameType gameType) {
        CardGame game = map.get(gameId);
        if(game == null){
            game = createGame(gameId,roomId,roomType,gameType);
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
