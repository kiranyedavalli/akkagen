package com.akkagen.serviceproviders.engine.providers.messages;

import com.akkagen.models.AbstractEngineDefinition;

public class RunMessage<T extends AbstractEngineDefinition> {

    private T req;

    public RunMessage(T req){
       this.req = req;
    }

    public T getReq() {
        return req;
    }
}
