package com.z.core.meilisearch.json;

import java.util.List;

/**
 * MeiliSearch json解析类
 * 2023年9月21日
 */
public class JsonHandler {

    private com.meilisearch.sdk.json.JsonHandler jsonHandler = new MyGsonJsonHandler();

    public <T> SearchResult<T> resultDecode(String o, Class<T> clazz) {
        Object result = null;
        try {
            result = jsonHandler.decode(o, SearchResult.class, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result == null ? null : (SearchResult<T>) result;
    }

    public <T> List<T> listDecode(Object o, Class<T> clazz) {
        Object list = null;
        try {
            list = jsonHandler.decode(o, List.class, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list == null ? null : (List<T>) list;
    }

    public String encode(Object o) {
        try {
            return jsonHandler.encode(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T decode(Object o, Class<T> clazz) {
        T t = null;
        try {
            t = jsonHandler.decode(o, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}