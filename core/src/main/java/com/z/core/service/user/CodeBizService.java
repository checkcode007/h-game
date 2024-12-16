package com.z.core.service.user;


import com.google.protobuf.ByteString;
import com.z.common.util.CodeUtil;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.email.MailBizService;
import com.z.core.service.wallet.WalletBizService;
import com.z.dbmysql.dao.code.GCodeDao;
import com.z.dbmysql.dao.code.GCodeSendLogDao;
import com.z.model.bo.user.User;
import com.z.model.common.MsgId;
import com.z.model.common.MsgResult;
import com.z.model.mysql.GCode;
import com.z.model.mysql.GCodeSendLog;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.type.AddType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * 兑换码
 */
@Service
public class CodeBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GCodeDao dao;
    @Autowired
    GCodeSendLogDao logDao;
    @Autowired
    UserBizService userBizService;
    @Autowired
    CCfgBizService cfgBizService;
    @Autowired
    MailBizService mailBizService;
    @Autowired
    WalletBizService walletBizService;
    /**
     * 点卡生成-查询
     */
    public MyMessage.MyMsgRes codeCreateList(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CODE_CREATE_LIST).setOk(true);
        List<GCode> list = dao.findByFrom(uid);
        User user = UserService.ins.get(uid);
        com.z.model.proto.User.S_10402.Builder b = com.z.model.proto.User.S_10402.newBuilder().setLeaveCount(user.getUser().getCodeCout());
        if (list != null && !list.isEmpty()) {
            for (GCode e : list) {
                b.addCodes(com.z.model.proto.User.BindCode.newBuilder().setUid(e.getTargetId())
                                .setTime(e.getCreateTime().getTime()).setCode(e.getCode())
                        .build());
            }

        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }
    /**
     * 点卡生成
     */
    public MyMessage.MyMsgRes codeCreate(long uid,long bindUid) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("bindUid:" + bindUid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CODE_CREATE_CREATE).setOk(true);
        List<GCode> list = dao.findByFrom(uid);
        MsgResult changeRet = userBizService.changeCodeCount(AddType.SUB,uid,1);
        if(!changeRet.isOk()){
            log.error(sj.add("changeCount fail").toString());
            res.setOk(false).setFailMsg(changeRet.getMessage());
            return res.build();
        }
        GCode exchangeCode = create(uid,bindUid);
        dao.save(exchangeCode);
        User user = UserService.ins.get(uid);
        com.z.model.proto.User.S_10404.Builder b = com.z.model.proto.User.S_10404.newBuilder().setLeaveCount(user.getUser().getCodeCout());
        if (list != null && !list.isEmpty()) {
            for (GCode e : list) {
                b.addCodes(com.z.model.proto.User.BindCode.newBuilder().setUid(e.getTargetId())
                        .setTime(e.getCreateTime().getTime()).setCode(e.getCode())
                        .build());
            }

        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }
    /**
     * 点卡查询
     */
    public MyMessage.MyMsgRes codeQuery(long uid,int type,long  bindUid,String code) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("type").add("bindUid:" + bindUid).add("code:" + code);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CODE_QUERY).setOk(true);
        List<GCode> list;
        if(type == 1 &&  bindUid>0 ){
            list = dao.findByTarget(bindUid);
        }else if (type == 2 && StringUtils.isNotEmpty(code)){
            list = dao.findByCode(code);
        }else{
            list = dao.getAll("id desc");
        }
        User user = UserService.ins.get(uid);
        com.z.model.proto.User.S_10406.Builder b = com.z.model.proto.User.S_10406.newBuilder().setLeaveCount(user.getUser().getCodeCout());
        if (list != null && !list.isEmpty()) {
            for (GCode e : list) {
                b.addCodes(com.z.model.proto.User.UserCode.newBuilder().setUid(e.getTargetId()).setGold(e.getGold()).setState(CommonUser.CodeState.forNumber(e.getState()))
                        .setExpireTime(e.getLastTime().getTime()).setUseTime(e.getUseTime()==null?0:e.getUseTime().getTime()).setCode(e.getCode())
                        .build());
            }

        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }


    /**
     * 点卡分发-列表
     */
    public MyMessage.MyMsgRes codeSendList(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CODE_SEND_LIST).setOk(true);
        List<GCodeSendLog> list = logDao.findByFrom(uid);
        User user = UserService.ins.get(uid);
        com.z.model.proto.User.S_10408.Builder b = com.z.model.proto.User.S_10408.newBuilder().setLeaveCount(user.getUser().getCodeCout());
        if (list != null && !list.isEmpty()) {
            for (GCodeSendLog e : list) {
                b.addCodes(com.z.model.proto.User.GiveCode.newBuilder().setUid(e.getTargetId()).setCount(e.getCout()).setTime(e.getLastTime().getTime())
                        .build());
            }
        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    /**
     * 点卡分发-分发
     */
    public MyMessage.MyMsgRes codeSend(long uid,long targetId,int cout) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("bindUid:" + targetId).add("cout:" + cout);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_CODE_SEND_SEND).setOk(true);
        List<GCodeSendLog> list = logDao.findByFrom(uid);
        User user = UserService.ins.get(uid);
        MsgResult changeRet = userBizService.changeCodeCout(uid,targetId,1);
        if(!changeRet.isOk()){
            log.error(sj.add("changeCount fail").toString());
            res.setOk(false).setFailMsg(changeRet.getMessage());
            return res.build();
        }
        com.z.model.proto.User.S_10408.Builder b = com.z.model.proto.User.S_10408.newBuilder().setLeaveCount(user.getUser().getCodeCout());
        if (list != null && !list.isEmpty()) {
            for (GCodeSendLog e : list) {
                b.addCodes(com.z.model.proto.User.GiveCode.newBuilder().setUid(e.getTargetId()).setCount(e.getCout()).setTime(e.getLastTime().getTime())
                        .build());
            }
        }
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }





    public GCode create(long uid, long targetId){
        DateTime now = DateTime.now();
        Date d = now.toDate();
        long t= d.getTime();
        long time = new Date().getTime();
        String code = CodeUtil.idToCode(time,15);
        GCode record = new GCode();
        record.setCreateTime(d);
        record.setTargetId(targetId);
        record.setCode(code);
        record.setCreateTime(d);
        record.setUpdateTime(d);
        record.setState(CommonUser.CodeState.CS_NOUSE.getNumber());
        int timeout = cfgBizService.getCodeTime();
        record.setLastTime(now.plusSeconds(timeout).toDate());
        return record;
    }


}
