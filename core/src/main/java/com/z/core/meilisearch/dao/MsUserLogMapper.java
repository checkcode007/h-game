package com.z.core.meilisearch.dao;
import com.z.core.meilisearch.MeilisearchRepository;
import com.z.model.meilisearch.MSUserLog;
import org.springframework.stereotype.Repository;

@Repository
public class MsUserLogMapper extends MeilisearchRepository<MSUserLog> {
}
