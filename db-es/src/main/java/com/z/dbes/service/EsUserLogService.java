package com.z.dbes.service;

import com.z.common.util.DateTimeUtil;
import com.z.model.es.EsIndex;
import com.z.model.es.EsUserLog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EsUserLogService extends AbstractEsService<EsUserLog> {
    @Override
    protected String getIndexName() {
        return EsIndex.USER_LOG + DateTimeUtil.getYearMonthStr();
    }

    @Override
    protected String getIndexSearchName() {
        return EsIndex.USER_LOG;
    }



}
