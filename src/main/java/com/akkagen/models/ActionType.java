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
