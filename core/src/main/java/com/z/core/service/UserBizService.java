package com.z.core.service;


import com.z.dbmysql.dao.user.GUserDao;
import com.z.model.mysql.GUser;
import com.z.model.proto.CommonUser;
import com.z.model.proto.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.StringJoiner;

@Log4j2
@Service
public class UserBizService {

    @Autowired
    GUserDao dao;
    @Autowired
    EsUserLogBizService esUserLogBizService;

    /**
     * 注册
     * @param pwd
     */
    public User.User_Res_10002 reg(String phone, String pwd){
        StringJoiner sj = new StringJoiner(",").add("reg").add("phone:"+phone).add("pwd:"+pwd);
        log.info(sj.toString());
        GUser user = dao.findByPhone(phone);
        User.User_Res_10002.Builder builder = User.User_Res_10002.newBuilder().setCode(200);
        if(user!= null){
            builder = builder.setUid(user.getId()).setCode(500).setFailMsg("该用户已经注册");
            log.error(sj.add("该用户已经注册").toString());
            builder.setFailMsg("该用户已经注册");
            return builder.build();
        }
        Date date = new Date();
        user = new GUser();
        user.setPhone(phone);
        user.setPassword(pwd);
        user.setCreateTime(date);
        user.setUpdateTime(date);
        user = dao.save(user);
        builder = builder.setUid(user.getId()).setFailMsg("注册成功");
        builder.setFailMsg("注册成功");
        log.info(sj.add("success").toString());
        esUserLogBizService.reg(user);
        return builder.build();
    }
    public User.User_Res_10004 login(int type,long uid,String phone, String pwd){
        StringJoiner sj = new StringJoiner(",").add("login").add("type:"+type).add("uid:"+uid).add("pwd:"+pwd);
        log.info(sj.toString());
        User.User_Res_10004.Builder builder = User.User_Res_10004.newBuilder().setCode(500);
        GUser user = null;
        if(type == 1){
            user = dao.findById(uid);
        } else if (type ==2) {
            user = dao.findByPhone(phone);
        }
        if(user == null){
            log.error(sj.add("没有该用户").toString());
            return builder.setFailMsg("没有该用户").build();
        }
        if(!user.getPassword().equals(pwd)){
            log.error(sj.add("密码不正确").toString());
            return builder.setFailMsg("密码不正确").build();
        }
        esUserLogBizService.login(user);
        log.info(sj.add("success").toString());
        return builder.setCode(200).setFailMsg("登录成功").setType(CommonUser.UserType.forNumber(user.getType())).build();
    }

    public void createRobot(){
        Date date = new Date();
        for (int i = 0; i < 2000; i++) {
            GUser user = new GUser();
            user.setPhone("666666");
            user.setPassword("666666");
            user.setRobot(1);
            user.setType(CommonUser.UserType.COMMON.getNumber());
            user.setName("机器人"+i);
            user.setCreateTime(date);
            user.setUpdateTime(date);
            dao.save(user);
        }
    }
}
