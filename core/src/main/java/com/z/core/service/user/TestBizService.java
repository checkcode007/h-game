package com.z.core.service.user;


import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.game.game.RoomBizService;
import com.z.core.service.game.room.RoomService;
import com.z.core.service.wallet.WalletBizService;
import com.z.dbmysql.dao.user.GUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GUserDao dao;
    @Autowired
    WalletBizService walletBizService;

    @Autowired
    RoomBizService roomBizService;

    boolean b_test =false;
//    @Scheduled(cron = "*/15 * * * * ?" )
    public void test(){
        if(b_test){
            return;
        }
        b_test =true;
        log.info("b-test-->"+b_test);
        long uid = 50000L;

        RoomService.ins.reloadCfg();

    }
//    @Scheduled(cron = "*/10 * * * * ?" )
//    public void test11(){
//        ProtocolDispatcher dispatcher = SpringContext.getBean(ProtocolDispatcher.class);
//        System.err.println("dispatcher-------->"+dispatcher);
//        System.err.println("redis------->"+RedisUtil.incr("testId",1));
//    }

}
