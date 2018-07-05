package com.akkagen.models;

public enum ActionType {
    CREATE, UPDATE, DELETE, GET, GETALL;

    public static ActionType getActionType(String method){
        for(ActionType type: ActionType.values()){
            if(method.equals(type.name())){
                return type;
            }
        }
        return null;
    }
}