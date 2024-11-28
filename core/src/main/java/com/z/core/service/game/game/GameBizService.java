package com.z.core.service.game.game;

import com.z.core.service.game.card.CardGame;
import com.z.core.service.game.card.CardService;
import com.z.core.service.user.UserBizService;
import com.z.model.proto.CommonGame;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

/**
 *游戏管理类
 */
@Log4j2
@Service
public class GameBizService {
    @Autowired
    EsGameBizService esGameService;
    @Autowired
    CardService cardService;
    @Autowired
    UserBizService userService;


    public CardGame into(long uid, long roomId, CommonGame.GameType gameType) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("roomId:"+roomId).add("gameType:"+gameType);
        log.info(sj.toString());
        CardGame game = cardService.getRandomRoom(roomId);
        long gameId = game!=null ?game.getId():0L;
        CardGame cardGame = null;
        if(CommonGame.GameType.BAIREN_NIUNIU == gameType ) {
            cardGame = cardService.into(uid,gameId,roomId);
        }
        log.info(sj.add("success").toString());
        return cardGame;
    }
}
