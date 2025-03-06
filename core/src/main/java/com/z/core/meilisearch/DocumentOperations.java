package com.z.core.meilisearch;

import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.Task;
import com.meilisearch.sdk.model.TaskInfo;

import java.util.List;

/**
 * MeiliSearch 基础接口
 * 2023年9月21日
 */
interface DocumentOperations<T> {

    T get(String identifier);

    List<T> list();

    List<T> list(int limit);

    List<T> list(int offset, int limit);

    long add(T document);

    long update(T document);

    long add(List<T> documents);

    long update(List<T> documents);

    long delete(String identifier);

    long deleteBatch(String... documentsIdentifiers);

    long deleteAll();

    com.z.core.meilisearch.json.SearchResult<T> search(String q);

    SearchResult search(String q, int offset, int limit);

    com.z.core.meilisearch.json.SearchResult<T> search(SearchRequest sr);

    String select(SearchRequest sr);

    Settings getSettings();

    TaskInfo updateSettings(Settings settings);

    TaskInfo resetSettings();

    Task getUpdate(int updateId);

//    UpdateStatus updateSettings(Settings settings);
//
//    UpdateStatus resetSettings();
//
//    UpdateStatus getUpdate(int updateId);
//
//    UpdateStatus[] getUpdates();
}