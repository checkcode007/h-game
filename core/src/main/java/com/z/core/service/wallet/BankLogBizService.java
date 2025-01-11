package com.z.core.service.wallet;


import com.z.dbmysql.dao.walletlog.GBankLogDao;
import com.z.model.mysql.GBankLog;
import com.z.model.proto.CommonUser;
import com.z.model.proto.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BankLogBizService {
    private static final Log log = LogFactory.getLog(BankLogBizService.class);

//    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GBankLogDao dao;

    public List<User.BankLog> getList(long uid){
        List<GBankLog> list1 = dao.findByUid(uid);
        List<User.BankLog> list = new ArrayList<>();
        if(list1 != null){
            for (GBankLog e : list1) {
                list.add(User.BankLog.newBuilder().setId(e.getId()).setGold(e.getGold()).setGold1(e.getGold1()).setGold2(e.getGold2())
                                .setType(CommonUser.BankType.forNumber(e.getType())).setState(e.isState()).setLastTime(e.getLastTime().getTime())
                                .setMailId(e.getMailId()).setTax(e.getTax())
                        .build());
            }
        }
        return list;
    }

    public void add(CommonUser.BankType type, long uid, long targetId,long transferId,long mailId, long gold1, long gold2, long gold,long tax){
        GBankLog bankLog = new GBankLog();
        bankLog.setType(type.getNumber());     // 设置 type
        bankLog.setUid(uid);                   // 设置 uid
        bankLog.setTargetId(targetId);         // 设置 targetId
        bankLog.setTransferId(transferId);     // 设置 transferId
        bankLog.setMailId(mailId);
        bankLog.setGold1(gold1);               // 设置 gold1
        bankLog.setGold2(gold2);               // 设置 gold2
        bankLog.setGold(gold);                 // 设置 gold
        bankLog.setTax(tax);
        bankLog.setLastTime(new Date());       // 设置 lastTime
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
