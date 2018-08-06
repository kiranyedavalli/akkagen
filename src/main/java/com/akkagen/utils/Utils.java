package com.akkagen.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.stream.IntStream;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static <T> T getObjectFromJson(String json, Class<T> t){
        return new Gson().fromJson(json, t);
    }

    public static String getMapAsString(Map<String, String> map) {
        if(map == null){
            return StringUtils.EMPTY;
        }
        StringBuilder str = new StringBuilder();
        map.forEach((k,v) -> str.append(k).append(":").append(v));
        return str.toString();
    }

    public static String getJSONStringDiff(String json1, String json2){
        String diff = StringUtils.EMPTY;
        try {
            ObjectMapper jackson = new ObjectMapper();
            JsonNode beforeNode = jackson.readTree(json1);
            JsonNode afterNode = jackson.readTree(json2);
            JsonNode patchNode = JsonDiff.asJson(beforeNode, afterNode);
            diff = patchNode.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return diff;
    }

    public static JsonObject getJsonObjectFromJsonString(String json){
        JsonElement jelement = new JsonParser().parse(json);
        return jelement.getAsJsonObject();
    }
}


