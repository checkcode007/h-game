package com.z.core.service.user;


import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.game.card.CardGame;
import com.z.core.service.game.game.GameBizService;
import com.z.core.service.game.room.RoomBizService;
import com.z.core.service.wallet.WalletBizService;
import com.z.dbmysql.dao.user.GUserDao;
import com.z.model.mysql.GRoom;
import com.z.model.proto.CommonGame;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TestBizService {
    @Autowired
    GUserDao dao;
    @Autowired
    CCfgBizService cfgBizService;
    @Autowired
    WalletBizService walletBizService;
    @Autowired
    RoomBizService roomBizService;
    @Autowired
    GameBizService gameBizService;

    boolean b_test =false;
//    @Scheduled(cron = "*/15 * * * * ?" )
    public void test(){
        if(b_test){
            return;
        }
        b_test =true;
        log.info("b-test-->"+b_test);
        long uid = 50000L;
        GRoom gRoom = roomBizService.intoGameRoom(uid, CommonGame.GameType.BAIREN_NIUNIU, CommonGame.RoomType.ONE);

        CardGame game = gameBizService.into(uid,gRoom.getId(),CommonGame.GameType.BAIREN_NIUNIU);

    }
//    @Scheduled(cron = "*/10 * * * * ?" )
//    public void test11(){
//        ProtocolDispatcher dispatcher = SpringContext.getBean(ProtocolDispatcher.class);
//        System.err.println("dispatcher-------->"+dispatcher);
//        System.err.println("redis------->"+RedisUtil.incr("testId",1));
//    }

}
