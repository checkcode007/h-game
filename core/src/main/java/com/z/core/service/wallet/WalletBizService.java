package com.z.core.service.wallet;


import com.google.protobuf.ByteString;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.service.email.MailBizService;
import com.z.core.service.transfer.TransferBizService;
import com.z.core.service.user.UserService;
import com.z.model.bo.WalletBo;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgId;
import com.z.model.mysql.GBankTransfer;
import com.z.model.mysql.GEmail;
import com.z.model.mysql.GWallet;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import com.z.model.type.AddType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.StringJoiner;

@Service
public class WalletBizService {
    private static final Log log = LogFactory.getLog(WalletBizService.class);
//    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    BankLogBizService bankLogBizService;
    @Autowired
    TransferBizService transferService;
    @Autowired
    private MailBizService emailBizService;

    public MyMessage.MyMsgRes info(long uid){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_INFO).setOk(true);
        Wallet wallet = WalletService.ins.get(uid);
        User.S_10202.Builder builder = User.S_10202.newBuilder();
        if(wallet!=null){
            builder.setGold(wallet.getGold()).setBankGold(wallet.getBankGold());
        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(builder.build().toByteArray())).build();
    }

    public boolean changeGold(CommonUser.GoldType goldType, AddType addType, long uid, long gold) {
        return changeGold(goldType,addType,uid,gold);
    }
    public boolean changeGold(CommonUser.GoldType goldType, AddType addType,
                              long uid, long gold, CommonGame.GameType gameType,
                              CommonGame.RoomType roomType) {
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
        Wallet wallet = WalletService.ins.get(uid);
        if (wallet == null) {
            create = true;
            GWallet w = create(uid, d);
            wallet = new Wallet();
            wallet.setWallet(w);
        }
        sj.add("create:" + create);
        if (addType == AddType.ADD) {
            wallet.addGold(gold);
            if(goldType == CommonUser.GoldType.GT_GAME){
                wallet.addWinGold(gold);
                wallet.addWins();

                UserChannelManager.broadReward(gameType,uid,gold);
            }
        } else {
            if (wallet.getGold() < gold) {
                log.error(sj.add("not full").toString());
                return false;
            }
            wallet.subGold(gold);
            if(goldType == CommonUser.GoldType.GT_GAME){
                wallet.addBetGold(gold);
                wallet.addBetC();
            }
        }

        if (create) {
            WalletService.ins.add(wallet.getWallet());
        } else {
            WalletService.ins.offer(wallet.getId());
        }
        com.z.model.bo.user.User user = UserService.ins.get(uid);
        UserService.ins.reloadBetState(user);

        //todo 添加入库金额变动记录

        //todo 出场入场的金额纪录

        sendWalletInfo(uid);
        log.info(sj.add("success").toString());
        return true;
    }
    public boolean changeBank(CommonUser.BankType bankType, long uid, long targerId, long gold) {
        return changeBank(bankType,uid,targerId,gold,null);
    }
    public boolean changeBank(CommonUser.BankType bankType, long uid, long targerId, long gold,GEmail mail) {
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
        Wallet wallet = WalletService.ins.get(uid);
        if (wallet == null) {
            create = true;
            GWallet w = create(uid, d);
            wallet = new Wallet();
            wallet.setWallet(w);
        }else{
            gold1 = wallet.getBankGold();
        }
        sj.add("create:" + create);
        long transferId = 0;
        long mailId=0,tax= 0,realGold=0;
        switch (bankType) {
            case CommonUser.BankType.BT_DEPOSIT://存入
                if (wallet.getGold() < gold) {
                    log.error(sj.add("gold not full").toString());
                    return false;
                }
                wallet.subGold( gold);
                wallet.addBankGold(gold);
                break;
            case CommonUser.BankType.BT_WITHDRAW://取出
                if (wallet.getBankGold() < gold) {
                    log.error(sj.add("bank not full").toString());
                    return false;
                }
                wallet.addGold( gold);
                wallet.subBankGold(gold);
                break;
            case CommonUser.BankType.BT_TRANSFER:
                if (wallet.getBankGold() < gold) {
                    log.error(sj.add("bank not full").toString());
                    return false;
                }
                wallet.subBankGold(gold);
                //转账记录
                GBankTransfer transfer = transferService.add(uid,targerId,gold);
                transferId = transfer.getId();
                //发送邮件
                GEmail gEmail = emailBizService.add(CommonUser.EmailType.ET_DEFAULT,transferId,transfer.getFromId(),transfer.getTargetId(),gold);
                mailId = gEmail.getId();
                tax= gEmail.getTax();
                realGold = gEmail.getRealGold();
                transferService.updateMail(transferId,mailId,tax,realGold);
                break;
            case CommonUser.BankType.BT_EMAIL:
                wallet.addBankGold(gold);
//                //转账记录更新状态
//                transferService.updateState(mail.getTransferId());
                transferId = mail.getTransferId();
                tax= mail.getTax();
                break;
        }
        bankLogBizService.add(bankType,uid,targerId,transferId,mailId,gold1,wallet.getBankGold(),gold,tax);
        if (create) {
            WalletService.ins.add(wallet.getWallet());
        } else {
            WalletService.ins.offer(wallet.getId());
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

    /**
     * 领取邮件
     */
    public MyMessage.MyMsgRes receiveMail(long uid, long id){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("id:"+id);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_EMAIL_RECEIVE).setOk(true);
        GEmail mail = emailBizService.findById(id);
        if(mail==null){
            log.error(sj.add("没有该邮件").toString());
            res.setOk(false).setFailMsg("没有该邮件");
            return res.build();
        }
        if(mail.getUid() != uid) {
            log.error(sj.add("数据异常euid:"+mail.getUid()).toString());
            res.setOk(false).setFailMsg("数据异常");
            return res.build();
        }
        if(mail.getState() == 1) {
            log.error(sj.add("已经领取").toString());
            res.setOk(false).setFailMsg("已经领取");
            return res.build();
        }
        long gold1 = mail.getGold();

        if(!transferService.updateState(mail.getTransferId())){
            log.error(sj.add("转账记录数据异常").toString());
            res.setOk(false).setFailMsg("转账记录数据异常");
            return res.build();
        }
        bankLogBizService.update(mail.getTransferId());
        mail.setState(1);
        mail.setUpdateTime(new Date());
        emailBizService.update(mail);
        changeBank(CommonUser.BankType.BT_EMAIL,uid,mail.getFromId(),mail.getRealGold(),mail);
        Wallet wallet = WalletService.ins.get(uid);
        bankLogBizService.add(CommonUser.BankType.BT_EMAIL,mail.getFromId(),mail.getUid(),mail.getTransferId(),
                mail.getId(),gold1,wallet.getBankGold(),mail.getRealGold(),mail.getTax());
        sj.add("gold1:"+gold1).add("realGold:"+mail.getRealGold()).add("tax:"+mail.getTax());
        log.info(sj.add("success").toString());
        sendWalletInfo(uid);
        return res.build();
    }
    @Async
    public void sendWalletInfo(long uid){
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_BANK_INFO).setOk(true);
        Wallet gWallet =  WalletService.ins.get(uid);
        User.S_10202.Builder builder = User.S_10202.newBuilder();
        if(gWallet!=null){
            builder.setGold(gWallet.getGold()).setBankGold(gWallet.getBankGold());
        }
        log.info(sj.add("success").toString());
        boolean b = UserChannelManager.sendMsg(uid,res.addMsg(ByteString.copyFrom(builder.build().toByteArray())).build());
        log.info(sj.add("success").toString());
    }

}
