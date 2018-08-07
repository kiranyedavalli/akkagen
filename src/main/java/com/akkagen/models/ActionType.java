/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.models;

import java.lang.reflect.Array;

public enum ActionType {
    POST, PUT, DELETE, GET, GETALL;

    public static ActionType getActionType(String method){
        for(ActionType type: ActionType.values()){
            if(method.equals(type.name())){
                return type;
            }
        }
        return null;
    }
}
