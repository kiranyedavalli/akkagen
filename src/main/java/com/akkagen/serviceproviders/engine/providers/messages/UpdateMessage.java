/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

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
