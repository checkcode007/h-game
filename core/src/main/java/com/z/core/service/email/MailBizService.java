package com.z.core.service.email;


import com.google.protobuf.ByteString;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.util.IdUtil;
import com.z.dbmysql.dao.email.GEmailDao;
import com.z.model.common.MsgId;
import com.z.model.mysql.GEmail;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Service
public class MailBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GEmailDao dao;
    @Autowired
    CCfgBizService cfgBizService;

    public GEmail add(CommonUser.EmailType type,long transferId, long fromId,
                      long targerId,long gold){
        //扣除税率
        long tax = (long)(gold*cfgBizService.getTaxes());
        long gold2 = gold - tax;
        DateTime now = DateTime.now();
        Date d =now.toDate();
        GEmail email = new GEmail();
        email.setId(IdUtil.nextEmailId());                      // 设置 id
        email.setType(type.getNumber());                       // 设置 type
        email.setUid(targerId);                                // 设置 uid
        email.setFromId(fromId);                               // 设置 fromId
        email.setGold(gold);                                   // 设置 gold
        email.setRealGold(gold2);
        email.setTax(tax);
        email.setTransferId(transferId);                       // 设置 transferId
        email.setState(CommonUser.YesNo.YN_N.getNumber());     // 设置 state
        email.setCreateTime(d);                                // 设置 createTime
        email.setUpdateTime(d);                                // 设置 updateTime
        email.setTitle("");
        String content = "您已经成功领取到了一笔"+gold+"金币,来自ID:"+fromId+",请银行查收";
        email.setContent(content);
        return dao.save(email);
    }
    /**
     * 获取邮件列表
     */
    public MyMessage.MyMsgRes list(long uid,int type){
        StringJoiner sj = new StringJoiner(",").add("type:"+type);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_EMAIL_LIST).setOk(true);
        List<GEmail> list = null;
        if(type == 0){//全部
            list = dao.findByUid(uid);
        }else{//未领取
            list = dao.find(uid, CommonUser.YesNo.YN_N);
        }
        int size = 0;
        User.S_10302.Builder b = User.S_10302.newBuilder().setType(type);
        if(list!=null){
            size = list.size();
            for (GEmail gEmail : list) {
                User.Email.Builder builder = User.Email.newBuilder();
                builder.setId(gEmail.getId()).setFromId(gEmail.getFromId()).setTransferId(gEmail.getTransferId()).setGold(gEmail.getGold());
                if(gEmail.getTitle() != null){
                    builder.setTitle(gEmail.getTitle());
                }
                if(gEmail.getContent() != null){
                    builder.setContent(gEmail.getContent());
                }
                builder.setState(gEmail.getState()).setCreateTime(gEmail.getCreateTime().getTime()).setUpdateTime(gEmail.getUpdateTime().getTime());
                b.addEmails(builder.build());
            }
        }
        log.info(sj.add("size:"+size).add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    public GEmail findById(long id) {
        return dao.findById(id);
    }
    public void update(GEmail record){
        dao.update(record);
    }
    public List<GEmail> get(long uid){
        return dao.findByUid(uid);
    }

    public GEmail getLastOne(long uid){
        return dao.findByUidLastOne(uid);
    }

}
