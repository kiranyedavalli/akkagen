/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
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
