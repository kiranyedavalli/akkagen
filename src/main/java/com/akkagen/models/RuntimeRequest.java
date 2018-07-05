package com.akkagen.models;

public class RuntimeRequest {

    private String path;
    private ActionType action;
    private AbstractNBRequest abstractNBRequest;

    public RuntimeRequest(){

    }

    public String getPath() {
        return path;
    }

    public ActionType getAction() {
        return action;
    }

    public AbstractNBRequest getAbstractNBRequest() {
        return abstractNBRequest;
    }


    public RuntimeRequest setPath(String path) {
        this.path = path;
        return this;
    }

    public RuntimeRequest setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public RuntimeRequest setAbstractNBRequest(AbstractNBRequest abstractNBRequest) {
        this.abstractNBRequest = abstractNBRequest;
        return this;
    }
}
