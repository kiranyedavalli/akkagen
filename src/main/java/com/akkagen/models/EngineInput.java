/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineInput<T extends AbstractEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(EngineInput.class);
    private String path;
    private ActionType action;
    private T engineDefinition;

    public EngineInput(){

    }

    public String getPath() {
        return path;
    }

    public ActionType getAction() {
        return action;
    }

    public T getEngineDefinition() {
        return engineDefinition;
    }


    public EngineInput<T> setPath(String path) {
        this.path = path;
        return this;
    }

    public EngineInput<T> setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public EngineInput<T> setEngineDefinition(T engineDefinition) {
        this.engineDefinition = engineDefinition;
        return this;
    }

    public String getPrintOut(){
        return String.format("path: %s\naction: %s\nEngineDefinition: %s", path, action.name(), engineDefinition);
    }
}
