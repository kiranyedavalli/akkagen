package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.Predicate;

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

    // TODO: implement real validators
    public static Predicate<RxRestEngineStatsDefinition> inputDataValidator = i -> true;
    public static Predicate<ActionType> methodValidator = m -> m.equals(ActionType.GET);
}
