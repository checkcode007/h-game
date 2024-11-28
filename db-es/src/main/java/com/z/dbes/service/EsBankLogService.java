package com.z.dbes.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.z.common.util.DateTimeUtil;
import com.z.model.es.EsIndex;
import com.z.model.es.EsBankLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsBankLogService extends AbstractEsService<EsBankLog> {
    @Override
    protected String getIndexName() {
        return EsIndex.BANK_LOG + DateTimeUtil.getYearMonthStr();
    }

    @Override
    protected String getIndexSearchName() {
        return EsIndex.BANK_LOG;
    }

    @Override
    public EsBankLog get(String id, Class<EsBankLog> clazz) {
        return super.get(id, clazz);
    }

    public List<EsBankLog> findByUid(long uid) {
        try {
            SearchResponse<EsBankLog> response = client.search(s -> s
                            .index(getIndexAliasName())
                            .query(q -> q
                                    .match(t -> t
                                            .field("uid")
                                            .query(uid)
                                    )
                            ),
                    EsBankLog.class
            );
            List<EsBankLog> list = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return list;
        } catch (Exception e) {
            logger.error("findByRoomId", e);
        }
        return null;
    }

    @Override
    public boolean add(String id, EsBankLog record) {
        return super.add(id, record);
    }


}
