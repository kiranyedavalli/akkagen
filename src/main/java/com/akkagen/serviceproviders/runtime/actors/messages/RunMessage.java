package com.akkagen.serviceproviders.runtime.actors.messages;

import com.akkagen.models.AbstractNBRequest;

public class RunMessage {

    private AbstractNBRequest req;

    public RunMessage(AbstractNBRequest req){
       this.req = req;
    }

    public AbstractNBRequest getReq() {
        return req;
    }
}
