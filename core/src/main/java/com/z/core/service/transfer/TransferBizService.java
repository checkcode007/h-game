package com.z.core.service.transfer;


import com.google.protobuf.ByteString;
import com.z.core.service.cfg.CCfgBizService;
import com.z.dbes.service.EsBankLogService;
import com.z.dbmysql.dao.banktransfer.GBankTransferDao;
import com.z.dbmysql.dao.wallet.GWalletDao;
import com.z.model.common.MsgId;
import com.z.model.mysql.GBankTransfer;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * 转账
 */
@Service
public class TransferBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GBankTransferDao dao;
    @Autowired
    GWalletDao walletDao;
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
        StringJoiner sj = new StringJoiner(",").add("fromId:" + fromId).add("targerId:" + targerId)
                .add("gold:" + gold);
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
    public boolean updateMail(long id,long mailId,long tax,long realGold){
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("mailId:" + mailId).add("tax:" + tax).add("realGold:" + realGold);
        log.info(sj.toString());
        GBankTransfer record = dao.findById(id);
        if(record == null) {
            log.error(sj.add("record null").toString());
            return false;
        }
        record.setMailId(mailId);
        record.setTax(tax);
        record.setRealGold(realGold);
        dao.update(record);
        log.error(sj.add("sucess").toString());
        return true;
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
        dao.update(record);
        log.error(sj.add("sucess").toString());
        return true;
    }

    /**
     * 管理-转出详情
     */
    public MyMessage.MyMsgRes outList(long uid, long targetId) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("target:" + targetId);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_MGR_QUERY_OUT).setOk(true);
        List<GBankTransfer> list  = dao.findByTarget(targetId);
        User.S_10414.Builder b = User.S_10414.newBuilder();
        if (list != null && !list.isEmpty()) {
            for (GBankTransfer e : list) {
                b.addTransferLogs(User.TransforLog.newBuilder().setId(e.getId()).setType(CommonUser.TransferLogType.forNumber(e.getType()))
                        .setFromId(e.getFromId()).setTargetId(e.getTargetId()).setState(e.isState()).setGold(e.getGold())
                        .setTime(e.getCreateTime().getTime())
                        .build());
            }
        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

}
