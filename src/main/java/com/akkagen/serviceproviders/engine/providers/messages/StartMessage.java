package com.akkagen.serviceproviders.engine.providers.messages;

import com.akkagen.models.AbstractEngineDefinition;

public class StartMessage<T extends AbstractEngineDefinition> {

    private T req;

    public StartMessage(T req){
       this.req = req;
    }

    public T getReq() {
        return req;
    }
}
