package com.akkagen.serviceproviders.engine.providers.messages;

import com.akkagen.models.AbstractEngineDefinition;

public class UpdateMessage<T extends AbstractEngineDefinition>{

    private T t;

    public UpdateMessage(T t){
        this.t = t;
    }

    public T getReq() {
        return t;
    }
}
