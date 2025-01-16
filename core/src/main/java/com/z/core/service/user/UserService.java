package com.z.core.service.user;


import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.z.core.ai.PlayerStateController;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.dbmysql.dao.user.GUserDao;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.mysql.GUser;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public enum UserService {
    ins;
    private static final Log log = LogFactory.getLog(UserService.class);
    GUserDao dao;
    UserService() {
        dao = SpringContext.getBean(GUserDao.class);
    }
    LoadingCache<Long, User> cache = Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.HOURS)
            .expireAfterWrite(1, TimeUnit.HOURS).initialCapacity(1000).maximumSize(3000).build(new CacheLoader<Long, User>() {
        @Override
        public @Nullable User load(@NonNull Long id) throws Exception {
            log.info("load user id:"+ id);
            GUser user = dao.findById(id);
            if(user == null) return null;
            User bo = new User();
            initUser(bo, user);
            return bo;
        }
    });

    public void initUser(User bo,GUser user) {
        bo.init(user);
        reloadBetState(bo);

    }

    /**
     * 刷新切换用户下注状态
     *  RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%
     * @param bo
     */
    public void reloadBetState(User bo) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+bo.getId()).add("lock:"+bo.isLock());
        if(bo.isLock()){
            bo.setSlotState(SlotState.LOW_BET);
            offer(bo.getId());

        }else{
            Wallet wallet = WalletService.ins.get(bo.getId());
            PlayerStateController.reload(wallet,bo);
        }
        log.info(sj.add("state:"+bo.getSlotState()).toString());

    }
    Queue<Long> queue = new ConcurrentLinkedQueue<>();

    public void offer(long uid){
        queue.add(uid);
    }

    public void exe(){
        while (true) {
            if(queue.isEmpty()){
                break;
            }
            long uid =  queue.poll();
            User bo = get(uid);
            bo.setChange(true);
        }
        if(cache.estimatedSize()<1) return;
        Map<Long, User> map = cache.asMap();
        for (User bo : map.values()) {
            if(bo.isChange()){
                log.info("==========>"+bo.getUser().toString());
                System.err.println(bo.getUser().toString());
                dao.update(bo.getUser());
                bo.setChange(false);
            }
        }
    }

    public void shutDown(){
        exe();
    }
    public List<GUser> findByDeviceId(String deviceId){
        return dao.findByDeviceId(deviceId);
    }
    public GUser findByPhone(String deviceId){
        GUser gUser  = dao.findByPhone(deviceId);
        if(gUser!=null){
            cache.get(gUser.getId());
        }
        return gUser;
    }
    public User get(long uid){
        return cache.get(uid);
    }
    public List<GUser> findRobot(){
        return dao.findRobot(1000);
    }
    public List<GUser> findRobot(int num){
        return dao.findRobot(num);
    }
    public User get(GUser user){
        User bo = new User();
        BeanUtils.copyProperties(user,bo);
        bo.setId(user.getId());
        bo.setType(CommonUser.UserType.valueOf(user.getType()));
        bo.setState(com.z.model.type.user.UserState.getUserState(user.getState()));
        bo.setUser(user);
        cache.put(user.getId(),bo);
        return bo;
    }
    public GUser add(GUser user){
        user = dao.save(user);
        User bo = new User();
        BeanUtils.copyProperties(user,bo);
        bo.setId(user.getId());
        bo.setType(CommonUser.UserType.valueOf(user.getType()));
        bo.setState(com.z.model.type.user.UserState.getUserState(user.getState()));
        bo.setUser(user);
        cache.put(user.getId(),bo);
        return user;
    }
    public boolean enter(long uid,CommonGame.GameType gameType, CommonGame.RoomType roomType, int cfgId, long roomId){
        User user = cache.get(uid);
        if(user == null) return false;
        user.enter(gameType,roomType,cfgId,roomId);
        offer(user.getId());
        return true;
    }


    public void out(long uid){
        User user = cache.get(uid);
        if(user == null) return;
        user.out();
        offer(user.getId());
    }

}
