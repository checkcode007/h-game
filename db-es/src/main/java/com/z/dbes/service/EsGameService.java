package com.z.dbes.service;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.z.common.util.DateTimeUtil;
import com.z.model.es.EsGame;
import com.z.model.es.EsIndex;
import com.z.model.es.EsUserLog;
import com.z.model.type.AddType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EsGameService extends AbstractEsService<EsGame> {
    @Override
    protected String getIndexName() {
        return EsIndex.GAME + DateTimeUtil.getYearMonthStr();
    }

    @Override
    protected String getIndexSearchName() {
        return EsIndex.GAME;
    }

    @Override
    public EsGame get(String id, Class<EsGame> clazz) {
        return super.get(id, clazz);
    }
    public List<EsGame> findByRoomId(long roomId) {
        try {
            SearchResponse<EsGame> response = client.search(s -> s
                            .index(getIndexAliasName())
                            .query(q -> q
                                    .match(t -> t
                                            .field("roomId")
                                            .query(roomId)
                                    )
                            ),
                    EsGame.class
            );
            List<EsGame> list = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return list;
        } catch (Exception e) {
            logger.error("findByRoomId", e);
        }
        return null;
    }

    @Override
    public boolean add(String id, EsGame esGame) {
        return super.add(id, esGame);
    }
    public void update(EsGame esGame) {
        String id = esGame.getId();
        Map<String,Object> map = new HashMap<>();
        map.put("bet", esGame.getBet());
        map.put("cout",esGame.getCout());
        super.updateOne(id,map);
    }
    @Override
    public boolean updateOne(String id, Map<String, Object> updateFields) {

        return super.updateOne(id, updateFields);
    }

    public void updateTags(String tag, String userId, AddType addType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("tag", tag);
        String script = "if (ctx._source.containsKey('tags') && ctx._source.tags.indexOf(params.tag)!=-1) {ctx._source.tags.remove(ctx._source.tags.indexOf(params.tag)) }";
        if (addType == AddType.ADD) {
            script = "if (ctx._source.containsKey('tags')) { if(ctx._source.tags.indexOf(params.tag)==-1){ctx._source.tags.add(params.tag) }} else { ctx._source.tags = ['" + tag + "'] }";
        }
        Script inline =new Script.Builder().lang("painless").source(script).options(parameters).build();
        super.updateScript(inline, userId);
    }
//
//    public long updateTags(String val, List<String> ids, AddType addType){
//        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids.toArray(new String[ids.size()]));
//        Map<String, Object> parameters = CollectionUtil.buildHashMap("tag", val);
//        String script = "if (ctx._source.containsKey('tags') && ctx._source.tags.indexOf(params.tag)!=-1) {ctx._source.tags.remove(ctx._source.tags.indexOf(params.tag)) }";
//        if (addType == AddType.ADD_TYPE){
//            script = "if (ctx._source.containsKey('tags')) { if(ctx._source.tags.indexOf(params.tag)==-1){ctx._source.tags.add(params.tag) }} else { ctx._source.tags=['"+val+"'] }";
//        }
//        Script inline = new Script(ScriptType.INLINE, "painless", script, parameters);
//        return super.updateScriptByQuery(inline, queryBuilder);
//    }
//
//    /**
//     * 删除分组标签
//     *
//     * @param tag 标签
//     */
//    public void removeTagsByGroupId(String tag) {
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
//        Map<String, Object> parameters = CollectionUtil.buildHashMap("tag", tag);
//        String script = "if (ctx._source.containsKey('tags') ) {ctx._source.tags.remove(ctx._source.tags.indexOf(params.tag)) }";
//        Script inline = new Script(ScriptType.INLINE, "painless", script, parameters);
//        updateScriptByQuery(inline, boolQueryBuilder);
//    }

}
