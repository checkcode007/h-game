package com.z.core.service.transfer;


import com.z.core.net.handler.wallet.TransferLog;
import com.z.core.service.cfg.CCfgBizService;
import com.z.dbes.service.EsBankLogService;
import com.z.dbmysql.dao.banktransfer.GBankTransferDao;
import com.z.dbmysql.dao.wallet.GWalletDao;
import com.z.model.mysql.GBankTransfer;
import com.z.model.proto.CommonUser;
import com.z.model.proto.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * 转账
 */
@Log4j2
@Service
public class TransferBizService {
    @Autowired
    GBankTransferDao dao;
    @Autowired
    GWalletDao walletDao;
    @Autowired
    CCfgBizService cfgBizService;
    @Autowired
    EsBankLogService bankLogService;

    public List<User.TransforLog> getList(int type ,long uid){
        List<GBankTransfer> list = dao.find(uid,type);
        List<User.TransforLog> retlist = new ArrayList<>();
        if(list!=null){
            for (GBankTransfer e : list) {
                retlist.add(User.TransforLog.newBuilder().setId(e.getId())
                                .setType(CommonUser.TransferLogType.forNumber(e.getType())).setState(e.isState()).setGold(e.getGold())
                                .setFromId(e.getFromId()).setTargetId(e.getTargetId()).setTime(e.getCreateTime().getTime())
                        .build());
            }
        }
        return retlist;
    }

    public GBankTransfer add(long fromId, long targerId,long gold) {
        StringJoiner sj = new StringJoiner(",").add("fromId:" + fromId).add("targerId:" + targerId).add("gold:" + gold);
        log.info(sj.toString());

        GBankTransfer record = create(fromId, targerId, gold);
        record = dao.save(record);
        log.info(sj.add("success").toString());
        return record;
    }


    public GBankTransfer create(long fromId,long targetId,long gold) {
        Date d = new Date();
        GBankTransfer record = GBankTransfer.builder().type(CommonUser.TransferLogType.TT_TRANSFER.getNumber())
                .fromId(fromId).targetId(targetId).gold(gold).state(false).build();
        record.setCreateTime(d);
        record.setUpdateTime(d);
        return record;
    }

    public boolean updateState(long id){
        StringJoiner sj = new StringJoiner(",").add("id:" + id);
        log.info(sj.toString());
        GBankTransfer record = dao.findById(id);
        if(record == null) {
            log.error(sj.add("record null").toString());
            return false;
        }
        if(record.isState()){
            log.error(sj.add("state fail").toString());
            return false;
        }
        record.setState(true);
        dao.save(record);
        log.error(sj.add("sucess").toString());
        return true;
    }

}
