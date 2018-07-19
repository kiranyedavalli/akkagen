package com.akkagen.serviceproviders.engine.providers.messages;

import com.akkagen.models.AbstractEngineDefinition;

public class RunMessage {

    private AbstractEngineDefinition req;

    public RunMessage(AbstractEngineDefinition req){
       this.req = req;
    }

    public AbstractEngineDefinition getReq() {
        return req;
    }
}
