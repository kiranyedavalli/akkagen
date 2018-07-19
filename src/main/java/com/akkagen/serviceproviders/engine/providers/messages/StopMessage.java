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
