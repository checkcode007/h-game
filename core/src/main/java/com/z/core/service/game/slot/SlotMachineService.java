package com.z.core.service.game.slot;

import com.z.model.bo.slot.Payline;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.slot.SlotModel;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SlotMachineService  implements ApplicationListener<ApplicationReadyEvent> {
    private static final Log log = LogFactory.getLog(SlotMachineService.class);

    @Autowired
    CPaylineService cPaylineService;
    @Autowired
    CSlotService cSlotService;

    Map<CommonGame.GameType, SlotMachine> map = new HashMap<>();
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        init();
    }

    public void init(){
        for (CommonGame.GameType gameType : CommonGame.GameType.values()) {
            Map<Integer, Payline> lineMap = cPaylineService.getMap(gameType);
            if(lineMap == null || lineMap.isEmpty()) continue;
            int COL_SIZE=5,ROW_SIZE=3;
            Map<Integer, List<CSlot>> slotMap = cSlotService.getMap(gameType);
            SlotMachine machine = new SlotMachine(gameType,COL_SIZE,ROW_SIZE);
            machine.initLines(lineMap,slotMap);
            map.put(gameType, machine);
            log.info("machine init:"+gameType +" lines:"+machine.getLineSize());

            if(gameType == CommonGame.GameType.BAIBIAN_XIAOMALI){
                log.info("gameType=======>:"+gameType);
                machine.print();
            } else if (gameType == CommonGame.GameType.JIUXIANLAWANG) {
                log.info("gameType=======>:"+gameType);
                machine.print();
            }else if (gameType == CommonGame.GameType.SHUIHUZHUAN) {
                log.info("gameType=======>:"+gameType);
                machine.print();
            }
        }
    }

    public SlotMachine getMachine(CommonGame.GameType gameType){
        return map.get(gameType);
    }
}
