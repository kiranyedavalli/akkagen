package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RxRestEngineStatsDefinition extends AbstractEngineDefinition {

    @JsonProperty
    private String url;
    @JsonProperty
    private long count;

    public RxRestEngineStatsDefinition(){}

    public String getUrl() {
        return url;
    }

    public long getCount() {
        return count;
    }

    public RxRestEngineStatsDefinition setUrl(String url) {
        this.url = url;
        return this;
    }

    public RxRestEngineStatsDefinition setCount(long count) {
        this.count = count;
        return this;
    }
}
