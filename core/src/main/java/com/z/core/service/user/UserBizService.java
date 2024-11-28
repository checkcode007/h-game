package com.z.core.service.user;


import com.google.protobuf.ByteString;
import com.z.common.util.NameUtils;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.service.cfg.CCfgBizService;
import com.z.core.service.wallet.WalletBizService;
import com.z.dbmysql.dao.agent.GAgentDao;
import com.z.dbmysql.dao.user.GUserDao;
import com.z.model.common.MsgId;
import com.z.model.common.MsgResult;
import com.z.model.mysql.GAgent;
import com.z.model.mysql.GUser;
import com.z.model.mysql.GWallet;
import com.z.model.proto.CommonUser;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import com.z.model.type.AddType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

@Log4j2
@Service
public class UserBizService {
    @Autowired
    GUserDao dao;
    @Autowired
    GAgentDao agentDao;
    @Autowired
    EsUserLogBizService esUserLogBizService;
    @Autowired
    CCfgBizService cfgBizService;
    @Autowired
    WalletBizService walletBizService;

    /**
     * 注册
     *
     * @param pwd
     */
    public MyMessage.MyMsgRes reg(ChannelHandlerContext ctx, String phone, String pwd, String deviceId) {
        StringJoiner sj = new StringJoiner(",").add("reg").add("phone:" + phone).add("pwd:" + pwd).add("deviceId:" + deviceId);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_REG).setOk(true);
        List<GUser> list = dao.findByDeviceId(deviceId);
        if (list != null && list.size() > cfgBizService.getRegNum()) {
            log.error(sj.add("已达手机注册上限").toString());
            res.setOk(false).setFailMsg("已达手机注册上限");
            return res.build();
        }
        GUser user = dao.findByPhone(phone);
        if (user != null) {
            log.error(sj.add("该用户已经注册").toString());
            res.setOk(false).setFailMsg("该用户已经注册");
            return res.build();
        }
        Date date = new Date();
        user = new GUser();
        user.setPhone(phone);
        user.setPassword(pwd);
        user.setCreateTime(date);
        user.setUpdateTime(date);
        user.setDeviceId(deviceId);
        user.setName(NameUtils.generateRandomString(8));
        user = dao.save(user);
        addChannel(ctx, user.getId());
        User.S_10002.Builder b = User.S_10002.newBuilder();
        b.setUid(user.getId()).setName(user.getName()).setPhone(user.getPhone() == null ? "" : user.getPhone());
        log.info(sj.add("success").toString());
        esUserLogBizService.reg(user);
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    public void addChannel(ChannelHandlerContext ctx, long uid) {
        ctx.channel().attr(ChannelAttributes.USER_ID).set(uid);
        UserChannelManager.bindUser(uid, ctx.channel());
    }

    public MyMessage.MyMsgRes login(ChannelHandlerContext ctx, int type, long uid, String phone, String pwd) {
        StringJoiner sj = new StringJoiner(",").add("login").add("type:" + type).add("uid:" + uid).add("phone:" + phone).add("pwd:" + pwd);
        log.info(sj.toString());

        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_LOGIN).setOk(true);
        GUser user = null;
        if (type == 1) {
            user = dao.findById(uid);
        } else if (type == 2) {
            user = dao.findByPhone(phone);
        }
        if (user == null) {
            log.error(sj.add("没有该用户").toString());
            res.setOk(false).setFailMsg("没有该用户");
            return res.build();
        }
        if (!user.getPassword().equals(pwd)) {
            log.error(sj.add("密码不正确").toString());
            res.setOk(false).setFailMsg("密码不正确");
            return res.build();
        }
        esUserLogBizService.login(user);
        addChannel(ctx, user.getId());
        User.S_10004.Builder b = User.S_10004.newBuilder().setVisitor(StringUtils.isEmpty(user.getPhone())).setType(CommonUser.UserType.forNumber(user.getType()));
        log.info(sj.add("success").toString());
        return res.addMsg(ByteString.copyFrom(b.build().toByteArray())).build();
    }

    public MyMessage.MyMsgRes bindPhone(long uid, String phone) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("phone:" + phone);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_PHONE).setOk(true);
        GUser user = dao.findById(uid);
        if (user == null) {
            log.error(sj.add("没有该用户").toString());
            res.setOk(false).setFailMsg("没有该用户");
            return res.build();
        }
        if (StringUtils.isNoneEmpty(user.getPhone())) {
            log.error(sj.add("已经绑定手机:" + user.getPhone()).toString());
            res.setOk(false).setFailMsg("已经绑定手机");
            return res.build();
        }
        user.setPhone(phone);
        dao.update(user);
        log.info(sj.add("success").toString());
        return res.build();
    }

    public MyMessage.MyMsgRes edit(long uid, CommonUser.ModifyUserType type, String name, int icon, String phone) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("name:" + name).add("icon:" + icon).add("phone:" + phone);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_EDIT).setOk(true);
        GUser user = dao.findById(uid);
        if (user == null) {
            log.error(sj.add("没有该用户").toString());
            res.setOk(false).setFailMsg("没有该用户");
            return res.build();
        }
        switch (type) {
            case CommonUser.ModifyUserType.MUT_NAME:
                if (StringUtils.isEmpty(name)) {
                    log.error(sj.add("名字不能为空").toString());
                    res.setOk(false).setFailMsg("名字不能为空");
                    return res.build();
                }
                user.setName(name);
                break;
            case CommonUser.ModifyUserType.MUT_PHONE:
                if (StringUtils.isEmpty(phone)) {
                    log.error(sj.add("手机号不能为空").toString());
                    res.setOk(false).setFailMsg("手机号不能为空");
                    return res.build();
                }
                if (StringUtils.isNoneEmpty(user.getPhone())) {
                    log.error(sj.add("已经绑定手机:" + user.getPhone()).toString());
                    res.setOk(false).setFailMsg("已经绑定手机");
                    return res.build();
                }
                user.setPhone(phone);
                break;
            case CommonUser.ModifyUserType.MUT_ICON:
                if (icon < 1) {
                    log.error(sj.add("头像不能为空").toString());
                    res.setOk(false).setFailMsg("头像不能为空");
                    return res.build();
                }

                user.setIcon(icon);
                break;
            default:
                log.error(sj.add("数据异常").toString());
                res.setOk(false).setFailMsg("数据异常");
                return res.build();
        }
        dao.update(user);
        log.info(sj.add("success").toString());
        return res.build();
    }

    public MyMessage.MyMsgRes modifyPwd(long uid, String oldPwd, String newPwd) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("old:" + oldPwd).add("new:" + newPwd);
        log.info(sj.toString());
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_PWD).setOk(true);
        GUser user = dao.findById(uid);
        if (user == null) {
            log.error(sj.add("没有该用户").toString());
            res.setOk(false).setFailMsg("没有该用户");
            return res.build();
        }
        if (!user.getPassword().equals(oldPwd)) {
            log.error(sj.add("老密码不正确:" + user.getPassword()).add(" old:" + oldPwd).toString());
            res.setOk(false).setFailMsg("老密码不正确");
            return res.build();
        }
        user.setPassword(newPwd);
        dao.update(user);
        log.info(sj.add("success").toString());
        return res.build();
    }


    public MsgResult changeCodeCout(long  fromId, long targetId, int cout){
        StringJoiner sj = new StringJoiner(",").add("from:" + fromId).add("target:" + targetId).add("cout:" + cout);
        log.info(sj.toString());
        MsgResult ret = new MsgResult(true);
        GUser from =dao.findById(fromId);
        GUser target =dao.findById(targetId);
        if(from == null){
            log.error(sj.add("from null").toString());
            ret.failMsg("用户数据异常");
            return ret;
        }
        if(target == null){
            log.error(sj.add("target null").toString());
            ret.failMsg("该用户不存在");
            return ret;
        }

        boolean b_manager = from.getType() == CommonUser.UserType.MANAGER.getNumber();
        sj.add("type1:"+from.getType()).add("type2:"+target.getType()).add("mgr:"+b_manager);
        if(!b_manager){
            if(from.getCodeCount()<cout){
                log.error(sj.add("cout less").toString());
                ret.failMsg("点卡数量不足");
                return ret;
            }
            GAgent agent = agentDao.findById(targetId);
            if(agent!=null){
                log.error(sj.add("agent fail").toString());
                ret.failMsg("该用户已经被代理");
                return ret;
            }
            agentDao.add(fromId,targetId);
            from.setCodeCount(from.getCodeCount()-cout);
            dao.update(from);
        }
        target.setCodeCount(target.getCodeCount()+cout);
        dao.update(target);
        log.info(sj.add("success").toString());
        return  ret;
    }
    /**
     * 修改点卡数量
     *
     * @param addType
     * @param uid
     * @param count
     * @return
     */
    public MsgResult changeCodeCount(AddType addType, long uid, int count) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("count:" + count);
        log.info(sj.toString());
        GUser gUser = findUser(uid);
        MsgResult ret = new MsgResult(true);
        if (AddType.ADD == addType) {
            gUser.setCodeCount(count);
        } else {
            if (gUser.getCodeCount() > count) {
                gUser.setCodeCount(gUser.getCodeCount() - count);
            } else {
                log.error(sj.add("count less:" + gUser.getCodeCount()).toString());
                ret.failMsg("点卡数量不足");
                return ret;
            }
        }
        gUser.setUpdateTime(new Date());
        dao.update(gUser);
        log.info(sj.add("success").toString());
        return ret;
    }

    private boolean b = false;

    //    @Scheduled(cron = "*/10 * * * * ?" )
    public void createRobot() {
        log.info("b======>" + b);
        if (b) {
            return;
        }
        b = true;
        log.info("b------->" + b);
        Date date = new Date();
        for (int i = 0; i < 100; i++) {
            log.info("i======>" + i);
            GUser user = new GUser();
            user.setPhone("");
            user.setPassword("666666");
            user.setRobot(1);
            user.setType(CommonUser.UserType.COMMON.getNumber());
            user.setName(NameUtils.generateRandomName());
            user.setCreateTime(date);
            user.setUpdateTime(date);
            user = dao.save(user);
            long gold = 90000000000L;
            walletBizService.changeBank(CommonUser.BankType.BT_AI, user.getId(), 0, gold);
        }
    }

    @Scheduled(cron = "0 0 6 ? * ?")
    public void resetRobot() {
        List<GUser> list = dao.findRobot(10000);
        Random random = new Random();
        for (GUser gUser : list) {
            long uid = gUser.getId();
            gUser.setRobot(0);
            GWallet wallet = walletBizService.findById(uid);
            long gold = 0L;
            if (wallet.getGold() < 1000000L) {
                gold = random.nextLong(900000000L, 9000000000L);
            } else if (wallet.getGold() < 10000000L) {
                gold = random.nextLong(90000000L, 900000000L);
            } else if (wallet.getGold() < 100000000L) {
                gold = random.nextLong(90000000L, 9000000000L);
            } else if (wallet.getGold() < 1000000000L) {
                gold = random.nextLong(9000000L, 900000000L);
            } else {
                continue;
            }
            walletBizService.changeGold(CommonUser.GoldType.GT_AI, AddType.ADD, uid, gold);
        }
    }

    public List<GUser> getRobot(int num) {
        return dao.findRobot(num);
    }

    public GUser findUser(long uid) {
        return dao.findById(uid);
    }

}
