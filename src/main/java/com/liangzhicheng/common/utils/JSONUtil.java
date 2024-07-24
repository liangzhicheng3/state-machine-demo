package com.liangzhicheng.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    public static String toJSONString(Object obj){
        try{
            return DEFAULT_OBJECT_MAPPER.writeValueAsString(obj);
        }catch(JsonProcessingException e){
            throw new RuntimeException("[JSON序列化] 序列化 JSON 失败", e);
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try{
            return DEFAULT_OBJECT_MAPPER.readValue(json, clazz);
        }catch(Exception e){
            throw new RuntimeException("[JSON序列化] 反序列化 JSON 失败", e);
        }
    }

}