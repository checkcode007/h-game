package com.z.core.service.wallet;


import com.z.core.service.cfg.CCfgBizService;
import com.z.dbmysql.dao.walletlog.GBankLogDao;
import com.z.model.mysql.GBankLog;
import com.z.model.proto.CommonUser;
import com.z.model.proto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BankLogBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GBankLogDao dao;
    @Autowired
    CCfgBizService cfgBizService;

    public List<User.BankLog> getList(long uid){
        List<GBankLog> list1 = dao.findByUid(uid);
        List<User.BankLog> list = new ArrayList<>();
        if(list1 != null){
            for (GBankLog e : list1) {
                list.add(User.BankLog.newBuilder().setId(e.getId()).setGold(e.getGold()).setGold1(e.getGold1()).setGold2(e.getGold2())
                                .setType(CommonUser.BankType.forNumber(e.getType())).setState(e.isState()).setLastTime(e.getLastTime().getTime())
                        .build());
            }
        }
        return list;
    }

    public void add(CommonUser.BankType type, long uid, long targetId,long transferId, long gold1, long gold2, long gold){
        GBankLog bankLog = GBankLog.builder().type(type.getNumber()).uid(uid).targetId(targetId).transferId(transferId).gold1(gold1).gold2(gold2).gold(gold).build();
        bankLog.setLastTime(new Date());
        if(type  == CommonUser.BankType.BT_EMAIL){
            bankLog.setState(false);
        }else{
            bankLog.setState(true);
        }
        dao.save(bankLog);
    }
    public void update(long transferId){
        GBankLog gBankLog = dao.findByTransfer(transferId);
        if(gBankLog == null){
            return;
        }
        gBankLog.setState(true);
        dao.update(gBankLog);

    }
}
