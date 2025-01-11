package com.z.core.service.cfg;

import com.z.core.util.SpringContext;
import com.z.model.proto.CommonGame;
import com.z.model.type.CfgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum CCfgBizService {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());
    CCfgDataBizService service;

    CCfgBizService() {
        this.service = SpringContext.getBean(CCfgDataBizService.class);
    }

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

    /**
     * 用户切换状态值1
     * @return
     */
    public int getUV1(){
        return service.get(CfgEnum.U_V1.name,CfgEnum.U_V1.clazz);
    }


    /**
     * 用户切换状态值2
     * @return
     */
    public int getUV2(){
        return service.get(CfgEnum.U_V2.name,CfgEnum.U_V2.clazz);
    }

    /**
     * 用户切换状态值3
     * @return
     */
    public int getUV3(){
        return service.get(CfgEnum.U_V3.name,CfgEnum.U_V3.clazz);
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
