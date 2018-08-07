/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

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
