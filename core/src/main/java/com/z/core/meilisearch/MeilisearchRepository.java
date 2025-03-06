package com.z.core.meilisearch;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.*;
import com.z.core.meilisearch.json.JsonHandler;
import com.z.model.meilisearch.MSFiled;
import com.z.model.meilisearch.MSIndex;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * MeiliSearch 基本操作实现
 * 2023年9月21日
 */
public class MeilisearchRepository<T> implements InitializingBean, DocumentOperations<T> {

    private Index index;
    private Class<T> tClass;
    private JsonHandler jsonHandler = new JsonHandler();

    @Resource
    private Client client;

    @Override
    public T get(String identifier) {
        T document;
        try {
            document = getIndex().getDocument(identifier, tClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    @Override
    public List<T> list() {
        List<T> documents;
        try {
            documents = Optional.ofNullable(getIndex().getDocuments(tClass))
                    .map(indexDocument -> indexDocument.getResults())
                    .map(result -> Arrays.asList(result))
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return documents;
    }

    @Override
    public List<T> list(int limit) {
        List<T> documents;
        try {
            DocumentsQuery query = new DocumentsQuery();
            query.setLimit(limit);
            documents = Optional.ofNullable(index.getDocuments(query, tClass))
                    .map(indexDocument -> indexDocument.getResults())
                    .map(result -> Arrays.asList(result))
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return documents;
    }

    @Override
    public List<T> list(int offset, int limit) {
        List<T> documents;
        try {
            DocumentsQuery query = new DocumentsQuery();
            query.setLimit(limit);
            query.setOffset(offset);
            documents = Optional.ofNullable(getIndex().getDocuments(query, tClass))
                    .map(indexDocument -> indexDocument.getResults())
                    .map(result -> Arrays.asList(result))
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return documents;
    }

    @Override
    public long add(T document) {
        List<T> list = Collections.singletonList(document);
        return add(list);
    }

    @Override
    public long update(T document) {
        List<T> list = Collections.singletonList(document);
        return update(list);
    }

    @Override
    public long add(List documents) {
        try {
            if (ObjectUtil.isNotNull(documents)) {
                String jsonString = JSON.toJSONString(documents);
                if (ObjectUtil.isNotNull(jsonString)) {
                    TaskInfo taskInfo = getIndex().addDocuments(jsonString);
                    if (ObjectUtil.isNotNull(taskInfo)) {
                        return taskInfo.getTaskUid();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(documents.toString(), e);
        }
        return 0;
    }


    @Override
    public long update(List documents) {
        int updates;
        try {
            updates = getIndex().updateDocuments(JSON.toJSONString(documents)).getTaskUid();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return updates;
    }


    @Override
    public long delete(String identifier) {
        int taskId;
        try {
            taskId = getIndex().deleteDocument(identifier).getTaskUid();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return taskId;
    }

    @Override
    public long deleteBatch(String... documentsIdentifiers) {
        int taskId;
        try {
            taskId = getIndex().deleteDocuments(Arrays.asList(documentsIdentifiers)).getTaskUid();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return taskId;
    }

    @Override
    public long deleteAll() {
        int taskId;
        try {
            taskId = getIndex().deleteAllDocuments().getTaskUid();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return taskId;
    }


    @Override
    public com.z.core.meilisearch.json.SearchResult<T> search(String q) {
        String result;
        try {
            result = JSON.toJSONString(getIndex().search(q));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonHandler.resultDecode(result, tClass);
    }

    @Override
    public com.z.core.meilisearch.json.SearchResult<T> search(String q, int offset, int limit) {
        SearchRequest searchRequest = SearchRequest.builder()
                .q(q)
                .offset(offset)
                .limit(limit)
                .build();
        return search(searchRequest);
    }

    //    @Override
    public com.z.core.meilisearch.json.SearchResult<T> searchPage(String q) {
        SearchRequest searchRequest = SearchRequest.builder()
                .q(q)
                .build();
        return search(searchRequest);
    }

    @Override
    public com.z.core.meilisearch.json.SearchResult<T> search(SearchRequest sr) {
        String result;
        try {
            result = "";
            if (ObjectUtil.isNotNull(sr)) {
                if (ObjectUtil.isNull(getIndex())) {
                    initIndex();
                }
                Searchable search = getIndex().search(sr);
                String jsonString = JSON.toJSONString(search);
                result = jsonString;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonHandler.resultDecode(result, tClass);
    }


    @Override
    public String select(SearchRequest sr) {
        try {
            if (ObjectUtil.isNotNull(sr)) {
                if (ObjectUtil.isNull(getIndex())) {
                    initIndex();
                }
                Searchable search = getIndex().search(sr);
                String jsonString = JSON.toJSONString(search);
                return jsonString;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Settings getSettings() {
        try {
            return getIndex().getSettings();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskInfo updateSettings(Settings settings) {
        try {
            return getIndex().updateSettings(settings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskInfo resetSettings() {
        try {
            return getIndex().resetSettings();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task getUpdate(int updateId) {
        try {
            return getIndex().getTask(updateId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initIndex();
    }

    public Index getIndex() {
        if (ObjectUtil.isNull(index)) {
            try {
                initIndex();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return index;
    }

    /**
     * 初始化索引信息
     *
     * @throws Exception
     */
    private void initIndex() throws Exception {
        Class<? extends MeilisearchRepository> clazz = getClass();
        tClass = (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        MSIndex annoIndex = tClass.getAnnotation(MSIndex.class);
        String uid = annoIndex.uid();
        String primaryKey = annoIndex.primaryKey();
        if (StringUtils.isEmpty(uid)) {
            uid = tClass.getSimpleName().toLowerCase();
        }
        if (StringUtils.isEmpty(primaryKey)) {
            primaryKey = "id";
        }
        int maxTotalHit = 1000;
        int maxValuesPerFacet = 100;
        if (Objects.nonNull(annoIndex.maxTotalHits())) {
            maxTotalHit = annoIndex.maxTotalHits();
        }
        if (Objects.nonNull(annoIndex.maxValuesPerFacet())) {
            maxValuesPerFacet = 100;
        }

        List<String> filterKey = new ArrayList<>();
        List<String> sortKey = new ArrayList<>();
        List<String> noDisPlay = new ArrayList<>();
        //获取类所有属性
        for (Field field : tClass.getDeclaredFields()) {
            //判断是否存在这个注解
            if (field.isAnnotationPresent(MSFiled.class)) {
                MSFiled annotation = field.getAnnotation(MSFiled.class);
                if (annotation.openFilter()) {
                    filterKey.add(annotation.key());
                }

                if (annotation.openSort()) {
                    sortKey.add(annotation.key());
                }
                if (annotation.noDisplayed()) {
                    noDisPlay.add(annotation.key());
                }
            }
        }
        Results<Index> indexes = client.getIndexes();
        Index[] results = indexes.getResults();
        Boolean isHaveIndex = false;
        for (Index result : results) {
            if (uid.equals(result.getUid())) {
                isHaveIndex = true;
                break;
            }
        }

        if (isHaveIndex) {
            client.updateIndex(uid, primaryKey);
        } else {
            client.createIndex(uid, primaryKey);
        }
        this.index = client.getIndex(uid);
        Settings settings = new Settings();
        settings.setDisplayedAttributes(noDisPlay.size() > 0 ? noDisPlay.toArray(new String[noDisPlay.size()]) : new String[]{"*"});
        settings.setFilterableAttributes(filterKey.toArray(new String[filterKey.size()]));
        settings.setSortableAttributes(sortKey.toArray(new String[sortKey.size()]));
        index.updateSettings(settings);
    }

}
 
 