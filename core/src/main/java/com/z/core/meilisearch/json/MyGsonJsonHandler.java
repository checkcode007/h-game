package com.z.core.meilisearch.json;

import com.alibaba.fastjson.JSON;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.meilisearch.sdk.exceptions.JsonDecodingException;
import com.meilisearch.sdk.exceptions.JsonEncodingException;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Key;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyGsonJsonHandler implements com.meilisearch.sdk.json.JsonHandler {
    private Gson gson;

    public MyGsonJsonHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_DATE_TIME));
//        gsonBuilder.registerTypeAdapter(Long.class, (JsonDeserializer<Long>) (json, typeOfT, context) -> {
//            Long parse = Long.parseLong(json.getAsJsonPrimitive().getAsString());
//            雪花id科学计数后转支付串会丢失进度小1啥的
//            return parse;
//        });
        this.gson = gsonBuilder.create();
    }

    public String encode(Object o) throws MeilisearchException {
        if (o != null && o.getClass() == String.class) {
            return (String) o;
        } else {
            if (o != null && o.getClass() == Key.class) {
                Key key = (Key) o;
                if (key.getExpiresAt() == null) {
                    JsonElement jsonElement = this.gson.toJsonTree(o);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    jsonObject.add("expiresAt", JsonNull.INSTANCE);
                    o = jsonObject;
                }
            }
            try {
                return JSON.toJSONString(o);
            } catch (Exception var6) {
                throw new JsonEncodingException(var6);
            }
        }
    }

    @Override
    public <T> T decode(Object o, Class<T> targetClass, Class<?>... parameters) throws MeilisearchException {
        if (o == null) {
            throw new JsonDecodingException("Response to deserialize is null");
        } else if (targetClass == String.class) {
            return (T) o;
        } else {
            try {
                if (parameters != null && parameters.length != 0) {
                    TypeToken<?> parameterized = TypeToken.getParameterized(targetClass, parameters);
                    Type type = parameterized.getType();
                    String json = this.gson.toJson(o);
                    json = json.replace("\\", "");
                    //去除json首位字符串
                    json = json.substring(1, json.length() - 1);
                    String string = o.toString();
                    return this.gson.fromJson(string, type);
//                    return  JSON.parseObject(string, type);
                } else {
                    return (T) JSON.parseObject((String) o, targetClass);
                }
            } catch (JsonSyntaxException var5) {
                throw new JsonDecodingException(var5);
            }
        }
    }
}
