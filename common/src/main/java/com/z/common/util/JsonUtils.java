package com.z.common.util;


import jakarta.json.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    /**
     * 将 JSON 字符串转换为 List<String>
     *
     * @param jsonString JSON 数组字符串
     * @return List<String>
     */
    public static List<String> jsonToList(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonArray jsonArray = reader.readArray();
        reader.close();

        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    /**
     * 将 JSON 字符串转换为 List<Map<String, Object>>
     *
     * @param jsonString JSON 数组对象字符串
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> jsonToListOfMaps(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonArray jsonArray = reader.readArray();
        reader.close();

        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            Map<String, Object> map = new HashMap<>();
            jsonObject.forEach((key, value) -> map.put(key, value.toString()));
            list.add(map);
        }
        return list;
    }

    /**
     * 将 List 转换为 JSON 字符串
     *
     * @param list 要转换的 List
     * @return JSON 字符串
     */
    public static String listToJson(List<String> list) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        list.forEach(arrayBuilder::add);

        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeArray(arrayBuilder.build());
        writer.close();

        return stringWriter.toString();
    }
    /**
     * 将 List<Object> 转换为 JSON 字符串
     *
     * @param list 要转换的 List<Object>
     * @return JSON 字符串
     */
    public static String listObjToJson(List<Object> list) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        list.forEach(item -> arrayBuilder.add(item.toString()));

        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeArray(arrayBuilder.build());
        writer.close();

        return stringWriter.toString();
    }


    /**
     * 将 List<Map<String, Object>> 转换为 JSON 字符串
     *
     * @param list 要转换的 List<Map<String, Object>>
     * @return JSON 字符串
     */
    public static String listOfMapsToJson(List<Map<String, Object>> list) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        list.forEach(map -> {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            map.forEach((key, value) -> objectBuilder.add(key, value.toString()));
            arrayBuilder.add(objectBuilder.build());
        });

        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeArray(arrayBuilder.build());
        writer.close();

        return stringWriter.toString();
    }
    /**
     * 将 JSON 字符串转换为 List<List<Object>>
     *
     * @param jsonString JSON 数组嵌套数组字符串
     * @return List<List<Object>>
     */
    public static List<List<Object>> jsonToListOfLists(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonArray jsonArray = reader.readArray();
        reader.close();

        List<List<Object>> listOfLists = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonArray innerArray = jsonArray.getJsonArray(i);
            List<Object> innerList = new ArrayList<>();
            for (int j = 0; j < innerArray.size(); j++) {
                innerList.add(innerArray.get(j).toString());
            }
            listOfLists.add(innerList);
        }
        return listOfLists;
    }
    /**
     * 将 List<List<Object>> 转换为 JSON 字符串
     *
     * @param list 要转换的 List<List<Object>>
     * @return JSON 字符串
     */
    public static String listOfListsToJson(List<List<Object>> list) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        list.forEach(innerList -> {
            JsonArrayBuilder innerArrayBuilder = Json.createArrayBuilder();
            innerList.forEach(item -> innerArrayBuilder.add(item.toString()));
            arrayBuilder.add(innerArrayBuilder.build());
        });

        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeArray(arrayBuilder.build());
        writer.close();

        return stringWriter.toString();
    }

    public static void main(String[] args) {
        // 示例：JSON 转 List
        String jsonList = "[\"apple\", \"banana\", \"cherry\"]";
        List<String> list = jsonToList(jsonList);
        System.out.println("List: " + list);

        // 示例：JSON 转 List<Map>
        String jsonObjects = "[{\"name\": \"apple\", \"color\": \"red\"}, {\"name\": \"banana\", \"color\": \"yellow\"}]";
        List<Map<String, Object>> listOfMaps = jsonToListOfMaps(jsonObjects);
        System.out.println("List of Maps: " + listOfMaps);

        // 示例：JSON 转 List<List>
        String jsonNestedList = "[[\"apple\", \"banana\"], [\"cherry\", \"date\"]]";
        List<List<Object>> listOfLists = jsonToListOfLists(jsonNestedList);
        System.out.println("List of Lists: " + listOfLists);

        // 示例：List 转 JSON
        String jsonString = listToJson(list);
        System.out.println("JSON String: " + jsonString);

        // 示例：List<Map> 转 JSON
        String jsonFromListOfMaps = listOfMapsToJson(listOfMaps);
        System.out.println("JSON from List of Maps: " + jsonFromListOfMaps);

        // 示例：List<List> 转 JSON
        String jsonFromListOfLists = listOfListsToJson(listOfLists);
        System.out.println("JSON from List of Lists: " + jsonFromListOfLists);
    }
}

