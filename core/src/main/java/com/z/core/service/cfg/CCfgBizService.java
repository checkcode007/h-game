package com.z.core.service.cfg;

import com.z.model.proto.CommonGame;
import com.z.model.type.CfgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CCfgBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    CCfgDataBizService service;

    /**
     * 税率
     * @return
     */
    public float getTaxes(){
       Integer texes = service.get(CfgEnum.TAXES.name,CfgEnum.TAXES.clazz);
       Integer base = service.get(CfgEnum.BASE.name,CfgEnum.BASE.clazz);
       return texes*1.0f/base;
    }
    /**
     * 手机注册上限
     * @return
     */
    public int getRegNum(){
        return service.get(CfgEnum.REGNUN.name,CfgEnum.REGNUN.clazz);
    }

    /**
     * 获取牛牛间隔时间
     * @return
     */
    public int getNiuniuTime(int i){
        return service.get(CfgEnum.NIUNIU_TIME.name+"_"+i,CfgEnum.NIUNIU_TIME.clazz);
    }

    /**
     * 百变小玛丽-下注-最小金额
     * @return
     */
    public int getBB_XML_bet_min(){
        return service.get(CfgEnum.BAIBIAN_XIAOMALI_BET_MIN.name,CfgEnum.BAIBIAN_XIAOMALI_BET_MIN.clazz);
    }
    /**
     * 百变小玛丽-下注-最大金额
     * @return
     */
    public int getBB_XML_bet_max(){
        return service.get(CfgEnum.BAIBIAN_XIAOMALI_BET_MAX.name,CfgEnum.BAIBIAN_XIAOMALI_BET_MAX.clazz);
    }
    /**
     * 百变小玛丽-下注-除以的基数
     * @return
     */
    public int getBB_XML_bet_base(){
        return service.get(CfgEnum.BAIBIAN_XIAOMALI_BET_BASE.name,CfgEnum.BAIBIAN_XIAOMALI_BET_BASE.clazz);
    }
    /**
     * 点卡对应的金币
     * @return
     */
    public int getCodeGold(){
        return service.get(CfgEnum.CODE_GOLD.name,CfgEnum.CODE_GOLD.clazz);
    }

    /**
     * 点卡过期时间
     * @return
     */
    public int getCodeTime(){
        return service.get(CfgEnum.CODE_TIME.name,CfgEnum.CODE_TIME.clazz);
    }


    public  Map<CommonGame.GameState,Integer> getNiuniuTime(){
        Map<CommonGame.GameState,Integer> map = new HashMap<>();
        for (CommonGame.GameState value : CommonGame.GameState.values()) {
            if(value == CommonGame.GameState.UNRECOGNIZED) continue;
            log.debug("========>"+CfgEnum.NIUNIU_TIME.name+"_"+value.getNumber());
            map.put(value,service.get(CfgEnum.NIUNIU_TIME.name+"_"+value.getNumber(),CfgEnum.NIUNIU_TIME.clazz));
        }
        return map;
    }

}
