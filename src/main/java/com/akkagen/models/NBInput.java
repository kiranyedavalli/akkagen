/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.models;

import java.util.HashMap;

public class NBInput<T extends AbstractEngineDefinition> {

    private String path;
    private ActionType action;
    private T engineDefinition;

    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> queryParams = new HashMap<>();

    public NBInput() {
    }

    public ActionType getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    public T getEngineDefinition() {
        return engineDefinition;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HashMap<String, String> getQueryParams() {
        return queryParams;
    }

    public NBInput<T> setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public NBInput<T> setPath(String path) {
        this.path = path;
        return this;
    }

    public NBInput<T> setEngineDefinition(T body) {
        this.engineDefinition = body;
        return this;
    }

    public NBInput<T> addToHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public NBInput<T> addToQueryParams(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }
}
