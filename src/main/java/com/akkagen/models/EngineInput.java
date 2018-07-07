package com.akkagen.models;

public class EngineInput {

    private String path;
    private ActionType action;
    private AbstractEngineDefinition abstractEngineDefinition;

    public EngineInput(){

    }

    public String getPath() {
        return path;
    }

    public ActionType getAction() {
        return action;
    }

    public AbstractEngineDefinition getAbstractEngineDefinition() {
        return abstractEngineDefinition;
    }


    public EngineInput setPath(String path) {
        this.path = path;
        return this;
    }

    public EngineInput setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public EngineInput setAbstractEngineDefinition(AbstractEngineDefinition abstractEngineDefinition) {
        this.abstractEngineDefinition = abstractEngineDefinition;
        return this;
    }
}
