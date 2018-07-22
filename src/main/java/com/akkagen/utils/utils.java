package com.akkagen.utils;

import com.google.gson.Gson;

public class utils {

    public static <T extends Object> T getObjectFromJson(String json, Class<T> t){
        return new Gson().fromJson(json, t);
    }
}
