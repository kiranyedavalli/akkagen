/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
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
