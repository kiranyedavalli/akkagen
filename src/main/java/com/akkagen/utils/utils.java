package com.akkagen.utils;

import com.google.gson.Gson;

public class utils {

    public static Object getObjectFromJsonString(String json, Class klass){
        return new Gson().fromJson(json, klass);
    }
}
