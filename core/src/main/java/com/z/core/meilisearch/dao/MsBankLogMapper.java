package com.z.core.meilisearch.dao;
import com.z.core.meilisearch.MeilisearchRepository;
import com.z.model.meilisearch.MainDO;
import com.z.model.meilisearch.MsBankLog;
import org.springframework.stereotype.Repository;

@Repository
public class MsBankLogMapper extends MeilisearchRepository<MsBankLog> {
}
