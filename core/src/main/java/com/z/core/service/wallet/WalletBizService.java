package com.z.core.service.wallet;


import com.google.protobuf.ByteString;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.email.EmailBizService;
import com.z.core.service.transfer.TransferBizService;
import com.z.dbmysql.dao.wallet.GWalletDao;
import com.z.model.bo.WalletBo;
import com.z.model.common.MsgId;
import com.z.model.mysql.GBankTransfer;
import com.z.model.mysql.GEmail;
import com.z.model.mysql.GWallet;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import com.z.model.type.AddType;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.StringJoiner;

@Log4j2
@Service
public class WalletBizService {
    @Autowired
    GWalletDao dao;
    @Autowired
    CCfgBizService cfgBizService;
    @Autowired
    BankLogBizService bankLogBizService;
    @Autowired
    TransferBizService transferService;
    @Autowired
    private EmailBizService emailBizService;

    public MyMessage.MyMsgRes info(long uid){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_INFO).setOk(true);
        GWallet gWallet = dao.findById(uid);
        User.S_10202.Builder builder = User.S_10202.newBuilder();
        if(gWallet!=null){
            builder.setGold(gWallet.getGold()).setBankGold(gWallet.getBankGold());
        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(builder.build().toByteArray())).build();
    }


    public boolean changeGold(CommonUser.GoldType goldType, AddType addType, long uid, long gold) {
        StringJoiner sj = new StringJoiner(",");
        sj.add("add:" + addType).add("uid:" + uid).add("gold:" + gold);
        log.info(sj.toString());
        if (gold < 1) {
            log.error(sj.add("gold less 1").toString());
            return true;
        }
        boolean create = false;
        DateTime now = DateTime.now();
        Date d = now.toDate();
        GWallet wallet = dao.findById(uid);
        if (wallet == null) {
            create = true;
            wallet = create(uid, d);
        }
        sj.add("create:" + create);
        if (addType == AddType.ADD) {
            wallet.setGold(wallet.getGold() + gold);
        } else {
            if (wallet.getGold() < gold) {
                log.error(sj.add("not full").toString());
                return false;
            }
            wallet.setGold(wallet.getGold() - gold);
        }
        if (create) {
            dao.save(wallet);
        } else {
            dao.update(wallet);
        }
        sendWalletInfo(uid);
        log.info(sj.add("success").toString());
        return true;
    }

    public boolean changeBank(CommonUser.BankType bankType, long uid, long targerId, long gold) {
        StringJoiner sj = new StringJoiner(",");
        sj.add("uid:" + uid).add("targerId:" + targerId).add("gold:" + gold).add("bankType:" + bankType);
        log.info(sj.toString());
        if (gold < 1) {
            log.error(sj.add("gold less 1").toString());
            return true;
        }
        boolean create = false;
        DateTime now = DateTime.now();
        Date d = now.toDate();
        long gold1 =0,gold2 = 0;
        GWallet wallet = dao.findById(uid);
        if (wallet == null) {
            create = true;
            wallet = create(uid, d);
        }else{
            gold1 = wallet.getBankGold();
        }
        sj.add("create:" + create);
        long transferId = 0;
        switch (bankType) {
            case CommonUser.BankType.BT_DEPOSIT://存入
                if (wallet.getGold() < gold) {
                    log.error(sj.add("gold not full").toString());
                    return false;
                }
                wallet.setGold(wallet.getGold() - gold);
                wallet.setBankGold(wallet.getBankGold() + gold);
                break;
            case CommonUser.BankType.BT_WITHDRAW://取出
                if (wallet.getBankGold() < gold) {
                    log.error(sj.add("bank not full").toString());
                    return false;
                }
                wallet.setGold(wallet.getGold() + gold);
                wallet.setBankGold(wallet.getBankGold() - gold);
                break;
            case CommonUser.BankType.BT_TRANSFER:
                if (wallet.getBankGold() < gold) {
                    log.error(sj.add("bank not full").toString());
                    return false;
                }
                wallet.setBankGold(wallet.getBankGold() - gold);
                //转账记录
                GBankTransfer transfer = transferService.add(uid,targerId,gold);
                transferId = transfer.getId();
                //发送邮件
                emailBizService.add(CommonUser.EmailType.ET_DEFAULT,transferId,transfer.getFromId(),transfer.getTargetId(),gold);
                break;
        }
        bankLogBizService.add(bankType,uid,targerId,transferId,gold1,wallet.getBankGold(),gold);
        if (create) {
            dao.save(wallet);
        } else {
            dao.update(wallet);
        }
        sendWalletInfo(uid);
        log.info(sj.add("success").toString());
        return true;
    }

    public GWallet create(long uid, Date d) {
        GWallet wallet = new GWallet();
        wallet.setId(uid);
        wallet.setGold(0);
        wallet.setCreateTime(d);
        return wallet;
    }

    public boolean addCout(WalletBo bo) {
        long uid = bo.getId();
        StringJoiner sj = new StringJoiner(",");
        sj.add("uid:" + uid).add("bo:" + bo);
        log.info(sj.toString());
        boolean create = false;
        DateTime now = DateTime.now();
        Date d = now.toDate();
        GWallet wallet = dao.findById(uid);
        if (wallet == null) {
            create = true;
            wallet = create(uid, d);
        }
        sj.add("create:" + create);

        wallet.setBetGold(wallet.getBetGold() + bo.getBankGold());
        wallet.setWinGold(wallet.getWinGold() + bo.getWinGold());
        wallet.setLosses(wallet.getLosses() + bo.getLosses());
        wallet.setWins(wallet.getWins() + bo.getWins());
        if (create) {
            dao.save(wallet);
        } else {
            dao.update(wallet);
        }
        log.info(sj.add("success").toString());
        return true;
    }

    public GWallet findById(long id) {
        return dao.findById(id);
    }
    /**
     * 领取邮件
     */
    public MyMessage.MyMsgRes receiveMail(long uid, long id){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("id:"+id);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_EMAIL_RECEIVE).setOk(true);
        GEmail gEmail = emailBizService.findById(id);
        if(gEmail==null){
            log.error(sj.add("没有该邮件").toString());
            res.setOk(false).setFailMsg("没有该邮件");
            return res.build();
        }
        if(gEmail.getUid() != uid) {
            log.error(sj.add("数据异常euid:"+gEmail.getUid()).toString());
            res.setOk(false).setFailMsg("数据异常");
            return res.build();
        }
        if(gEmail.getState() == 1) {
            log.error(sj.add("已经领取").toString());
            res.setOk(false).setFailMsg("已经领取");
            return res.build();
        }
        if(!transferService.updateState(gEmail.getTransferId())){
            log.error(sj.add("转账记录数据异常").toString());
            res.setOk(false).setFailMsg("转账记录数据异常");
            return res.build();
        }
        bankLogBizService.update(gEmail.getTransferId());
        long gold1 = gEmail.getGold();
        //扣除税率
        long diff = (long)(gold1*cfgBizService.getTaxes());
        long gold2 = gold1 - diff;
        gEmail.setGold(gold2);
        gEmail.setState(1);

        gEmail.setUpdateTime(new Date());
        emailBizService.update(gEmail);
        changeBank(CommonUser.BankType.BT_EMAIL,gEmail.getFromId(),uid,gold2);

        sj.add("gold1:"+gold1).add("gold2:"+gold2).add("diff:"+diff);
        log.info(sj.add("success").toString());
        sendWalletInfo(uid);
        return res.build();
    }
    @Async
    public void sendWalletInfo(long uid){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_INFO).setOk(true);
        GWallet gWallet = dao.findById(uid);
        User.S_10202.Builder builder = User.S_10202.newBuilder();
        if(gWallet!=null){
            builder.setGold(gWallet.getGold()).setBankGold(gWallet.getBankGold());
        }
        log.info(sj.add("success").toString());
        boolean b = UserChannelManager.sendMsg(uid,res.addMsg(ByteString.copyFrom(builder.build().toByteArray())).build());
        log.info(sj.add("success").toString());
    }
}
