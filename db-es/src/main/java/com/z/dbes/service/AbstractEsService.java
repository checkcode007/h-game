package com.z.dbes.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonData;
import com.z.model.bean.Pager;
import jakarta.json.Json;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractEsService<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final String TIMEOUT_QUERY = "60S", TIMEOUT_CREATE = "10S";
    private Map<String, Boolean> tableMap = new HashMap<>();

    @Autowired
    @Qualifier("ElasticsearchClient")
    public ElasticsearchClient client;

    /**
     * 索引名称
     *
     * @return String
     */
    protected abstract String getIndexName();

    /**
     * 索引别名
     *
     * @return String
     */
    protected String getIndexAliasName() {
        return getIndexName() + "_all";
    }

    /**
     * 索引搜索用别名
     *
     * @return String
     */
    protected abstract String getIndexSearchName();

    protected String getIndexSearchAliasName() {
        return getIndexSearchName() + "_all";
    }

    public void checkCreate() {
        try {
            if (indexExists()) {
                return;
            }
            createIndex();
        } catch (IOException e) {
            logger.error("checkCreate", e);
        }
    }

    // 创建索引
    private boolean createIndex() throws IOException {
        // 创建索引请求
        String indexName = getIndexName();
        Map<String, Alias> aliasesMap = new HashMap<>();
        aliasesMap.put(getIndexAliasName(), Alias.of(a -> a.isWriteIndex(true))); // 设置写入别名
        aliasesMap.put(getIndexSearchAliasName(), Alias.of(a -> a)); // 设置搜索别名
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(indexName)
                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("1").refreshInterval(Time.of(t -> t.time("3s")))
                ).aliases(aliasesMap)
        );
        CreateIndexResponse createIndexResponse = client.indices().create(request);
        return createIndexResponse.acknowledged();
    }

    // 判断索引是否存在
    public boolean indexExists() {
        try {
            ExistsRequest request = ExistsRequest.of(e -> e.index(getIndexAliasName()));
            return client.indices().exists(request).value();
        } catch (IOException e) {
            logger.error("indexExists", e);
        }
        return false;
    }

    // 删除索引
    public boolean deleteIndex() {
        try {
            DeleteIndexRequest request = DeleteIndexRequest.of(d -> d.index(getIndexAliasName()));
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(request);
            return deleteIndexResponse.acknowledged();
        } catch (IOException e) {
            logger.error("deleteIndex", e);
        }
        return false;
    }

    // 插入文档
    public boolean indexDocument(String id, String jsonString) {
        try {
            checkCreate();
            IndexRequest<JsonData> request = IndexRequest.of(i -> i
                    .index(getIndexAliasName())
                    .id(id)
                    .document(JsonData.of(jsonString))
            );
            IndexResponse indexResponse = client.index(request);
            return indexResponse.result().name().equalsIgnoreCase("created");
        } catch (IOException e) {
            logger.error("indexDocument", e);
        }
        return false;
    }

    // 获取文档
    public String getDocument(String id) {
        try {
            GetRequest request = GetRequest.of(g -> g.index(getIndexAliasName()).id(id));
            GetResponse<JsonData> response = client.get(request, JsonData.class);

            if (response.found()) {
                // 使用响应数据并提供一个 JsonpMapper 实例
                JsonData jsonData = response.source();
                jakarta.json.JsonValue jsonValue = jsonData.toJson(client._jsonpMapper());

                // 将 JsonValue 写入 StringWriter 来生成 JSON 字符串
                StringWriter stringWriter = new StringWriter();
                Json.createWriter(stringWriter).write(jsonValue);

                return stringWriter.toString();  // 返回 JSON 字符串
            } else {
                return null;  // 文档未找到时返回 null
            }
        } catch (IOException e) {
            logger.error("getDocument", e);
        }
        return null;
    }

    // 搜索文档
    public List<Hit<JsonData>> searchDocuments(String field, String value) throws IOException {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(getIndexAliasName())
                    .query(q -> q
                            .match(m -> m
                                    .field(field)
                                    .query(value)
                            )
                    )
            );
            SearchResponse<JsonData> response = client.search(request, JsonData.class);
            return response.hits().hits();
        } catch (Exception e) {
            logger.error("searchDocuments", e);
        }
        return null;
    }

    // 更新文档
    public boolean updateDocument(String id, String jsonString) throws IOException {
        try {
            checkCreate();
            UpdateRequest<JsonData, JsonData> request = UpdateRequest.of(u -> u
                    .index(getIndexAliasName())
                    .id(id)
                    .doc(JsonData.of(jsonString))
            );
            UpdateResponse updateResponse = client.update(request, JsonData.class);
            return updateResponse.result().name().equalsIgnoreCase("updated");
        } catch (Exception e) {
            logger.error("updateDocument", e);
        }
        return false;
    }

    // 删除文档
    public boolean deleteDocument(String id) throws IOException {
        try {
            DeleteRequest request = DeleteRequest.of(d -> d.index(getIndexAliasName()).id(id));
            DeleteResponse deleteResponse = client.delete(request);
            return deleteResponse.result().name().equalsIgnoreCase("deleted");
        } catch (Exception e) {
            logger.error("deleteDocument", e);
        }
        return false;
    }

    protected Pager<T> search(Query query, int page, int size, List<SortOptions> sorts, List<String> excludeFields, List<String> includeFields) {
        Pager<T> pager = new Pager<>();
        pager.setSize(size);
        pager.setPage(page);

        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(getIndexAliasName()).query(query);
        if (size > 0) {
            builder.size(size);
            if (page > 0) {
                builder.from(page * size);
            }
        }
        builder.timeout(TIMEOUT_QUERY);
        if (sorts != null) {
            builder.sort(sorts);
        }
        SourceConfig sourceConfig = null;
        if (excludeFields != null || includeFields != null) {
            // 构建 SourceFilter 以仅返回特定字段
            SourceFilter filter = new SourceFilter.Builder()
                    .includes(includeFields).excludes(excludeFields)
                    .build();
            // 构建 SourceConfig
            sourceConfig = new SourceConfig.Builder()
                    .filter(filter) // 设置过滤条件
                    .build();
        } else {
            sourceConfig = new SourceConfig.Builder()
                    .fetch(true) // 设置过滤条件
                    .build();
        }
        builder.source(sourceConfig);

        SearchRequest searchRequest = builder.build();
        List<T> hitList = new ArrayList<>();
        try {
//            checkCreate();
            SearchResponse searchResponse = client.search(searchRequest, JsonData.class);
            if (searchResponse == null) {
                return pager;
            }
            HitsMetadata hitsMetadata = searchResponse.hits();
            List<Hit<T>> hits = hitsMetadata.hits();
            for (Hit<T> hit : hits) {
                T source = hit.source();
                hitList.add(source);
            }
            pager.setList(hitList);
            pager.setTotal(hitsMetadata.total().value());
            return pager;
        } catch (Exception e) {
            logger.error("Es search err sql:{}, message:{}, track:{}", builder, e.getMessage(), e.getStackTrace());
            return pager;
        } finally {
            logger.debug("Es searchResponse sql:{}, total:{}, resSize:{}, page:{}, size:{}, idListSize:{}", builder, pager.getTotal(), hitList.size(), page, size, hitList.size());
        }
    }

    // 获取文档
    public T get(String id, Class<T> clazz) {
        try {
            GetRequest request = GetRequest.of(g -> g.index(getIndexAliasName()).id(id));
            GetResponse<JsonData> response = client.get(request, JsonData.class);
            if (response.found()) {
                // 使用响应数据并提供一个 JsonpMapper 实例
                JsonData jsonData = response.source();
                return jsonData.to(clazz, client._jsonpMapper());
            } else {
                return null;  // 文档未找到时返回 null
            }
        } catch (IOException e) {
            logger.error("get", e);
        }
        return null;
    }

    // 插入文档
    public boolean add(String id, T t) {
        checkCreate();
        try {

            IndexResponse response = client.index(i -> i
                    .index(getIndexAliasName())
                    .id(id)
                    .document(t)
            );
            logger.info(getIndexAliasName()+":json=====>"+t);
            return response.result().name().equalsIgnoreCase("created");
        } catch (IOException e) {
            logger.error("add", e);
        }
        return false;
    }

    // 更新
    public boolean updateOne(String id, Map<String, Object> updateFields, T t) {
        checkCreate();
        try {
            // 构建更新请求
            UpdateRequest<Object, Object> request = UpdateRequest.of(u -> u
                    .index(getIndexAliasName())
                    .id(id)
                    .doc(updateFields)
            );
            // 执行更新操作
            UpdateResponse<Object> response = client.update(request, Map.class);
            // 处理响应
            if (response.result() == Result.Updated) {
                logger.info("Document updated successfully.");
                return true;
            } else {
                logger.error("Document update failed.");
                return false;
            }
        } catch (IOException e) {
            logger.error("updateOne", e);
        }
        return false;

    }

    public boolean updateOne(String id, Query query, Map<String, Object> updateFields) {
        checkCreate();
        try {
            // 创建更新查询请求
            // 创建更新请求
            UpdateRequest<Map<String, Object>, Map<String, Object>> request = UpdateRequest.of(u -> u
                    .index(getIndexAliasName())
                    .id(id)
                    .doc(updateFields) // 传入更新的字段
            );
            // 执行更新操作
            UpdateResponse<Map<String, Object>> response = client.update(request, Map.class);

            // 处理响应
            if (response.result() == Result.Updated) {

                logger.info("Document updated successfully.");
                return true;
            } else {
                logger.info("Document updated failed.");
                return false;
            }
        } catch (IOException e) {
            logger.error("updateOne", e);
        }
        return false;

    }

    public boolean updateMultipleFields(Query query, String updateField, String updateValue) {
        checkCreate();
        // 创建更新查询请求

        try {
            String source = "ctx._source." + updateField + " = '" + updateValue + "'";
            UpdateByQueryRequest request = UpdateByQueryRequest.of(b -> b
                    .index(getIndexAliasName())
                    .query(query)
                    .script(s -> s
                            .source(source)
                    )
            );
            // 执行更新查询
            UpdateByQueryResponse response = client.updateByQuery(request);
            return true;
        } catch (IOException e) {
            logger.error("updateMultipleFields", e);
        }
        return false;
    }

    public boolean updateMultipleFields(Query query, Map<String, Object> updateFields) {
        checkCreate();
        try {
            // 创建脚本字符串，动态生成更新脚本
            StringBuilder source = new StringBuilder();
            Map<String, JsonData> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                source.append("").append(key).append(" = params.").append(key).append("; ");
                String jsonString = JsonbBuilder.create().toJson(value);
                map.put(entry.getKey(), JsonData.of(jsonString)); // 传递更新参数
            }
            // 创建更新请求
            UpdateByQueryRequest request = UpdateByQueryRequest.of(b -> b
                    .index(getIndexAliasName()) // 设置索引名称
                    .query(query) // 设置查询条件
                    .script(s -> s
                            .source(source.toString()) // 更新脚本
                            .params(map)
                    )
            );
            // 执行更新查询
            UpdateByQueryResponse response = client.updateByQuery(request);
            return true; // 根据需求返回相应的值
        } catch (IOException e) {
            logger.error("updateMultipleFields", e);
        }
        return false;
    }
public T searchById(long id,Class<T> clazz) {

    try {
        SearchResponse<T> response = client.search(s -> s
                        .index(getIndexAliasName())
                        .query(q -> q
                                .match(t -> t
                                        .field("id")
                                        .query(id)
                                )
                        ),
                clazz
        );
        List<T> list = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return list.get(0);
    } catch (Exception e) {
       logger.error("searchById", e);
    }
    return null;
}

}
