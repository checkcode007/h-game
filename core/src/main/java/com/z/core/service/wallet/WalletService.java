package com.z.core.service.wallet;


import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.z.common.util.SpringContext;
import com.z.dbmysql.dao.wallet.GWalletDao;
import com.z.model.bo.user.Wallet;
import com.z.model.mysql.GWallet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public enum WalletService {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());

    GWalletDao dao;

    WalletService() {
        dao = SpringContext.getBean(GWalletDao.class);
    }

    Queue<Long> queue = new ConcurrentLinkedQueue<>();

    public void offer(long uid){
        queue.add(uid);
    }

    LoadingCache<Long, Wallet> cache = Caffeine.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES).initialCapacity(1000).maximumSize(3000).build(new CacheLoader<Long, Wallet>() {
                @Override
                public @Nullable Wallet load(@NonNull Long id) throws Exception {
                    GWallet wallet = dao.findById(id);
                    if (wallet == null) return null;
                    Wallet bo = new Wallet();
                    bo.setWallet(wallet);
                    return bo;
                }
            });

    public void exe(){
        while (true) {
            if(queue.isEmpty()){
                break;
            }
           long uid =  queue.poll();
            Wallet bo = get(uid);
            bo.setChange(true);
        }
        if(cache.estimatedSize()<1) return;
        Map<Long, Wallet> map = cache.asMap();
        for (Wallet bo : map.values()) {
            if(bo.isChange()){
                dao.update(bo.getWallet());
                bo.setChange(false);
            }
        }
    }

    public void shutDown(){
        exe();
    }

    public Wallet get(GWallet wallet){
        Wallet bo = new Wallet();
        bo.setWallet(wallet);
        cache.put(wallet.getId(),bo);
        return bo;
    }
    public GWallet add(GWallet wallet){
        wallet = dao.save(wallet);
        Wallet bo = new Wallet();
        bo.setWallet(wallet);
        cache.put(wallet.getId(),bo);
        return wallet;
    }

    public Wallet get(long uid){
        return cache.get(uid);
    }
}
