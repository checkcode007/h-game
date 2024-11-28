package com.z.core.service.email;


import com.google.protobuf.ByteString;
import com.z.common.util.IdUtil;
import com.z.core.service.cfg.CCfgBizService;
import com.z.dbmysql.dao.email.GEmailDao;
import com.z.model.common.MsgId;
import com.z.model.mysql.GEmail;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Log4j2
@Service
public class EmailBizService {
    @Autowired
    GEmailDao dao;
    @Autowired
    CCfgBizService cfgBizService;

    public boolean add(CommonUser.EmailType type,long transferId, long fromId, long targerId,long gold){
        DateTime now = DateTime.now();
        long t = now.getMillis();
        Date d =now.toDate();
        GEmail email = GEmail.builder().id(IdUtil.nextEmailId()).type(type.getNumber()).uid(targerId)
                .fromId(fromId).gold(gold).transferId(transferId).state(CommonUser.YesNo.YN_N.getNumber())
                .createTime(d).updateTime(d).build();
        dao.save(email);
        return true;
    }
    /**
     * 获取邮件列表
     */
    public MyMessage.MyMsgRes list(long uid,int type){
        StringJoiner sj = new StringJoiner(",").add("type:"+type);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_EMAIL_LIST).setOk(true);
        List<GEmail> list = null;
        int tatal = 0;
        if(type == 1){//全部
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
                builder.setId(gEmail.getId()).setFromId(gEmail.getFromId()).setTransferId(gEmail.getTransferId()).setTitle(gEmail.getTitle()).setContent(gEmail.getContent()).setGold(gEmail.getGold());
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
        dao.save(record);
    }

}
