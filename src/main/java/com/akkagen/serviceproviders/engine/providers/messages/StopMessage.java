/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.engine.providers.messages;

public class StopMessage {

    private String id;

    public StopMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
