/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TxRestEngineDefinition extends AbstractEngineDefinition {

    @JsonProperty
    private String url;
    @JsonProperty
    private String body;
    @JsonProperty
    private ActionType method;
    @JsonProperty
    private Map<String, String> headers = new HashMap<>();
    @JsonProperty
    private Map<String, String> queryParams = new HashMap<>();
    @JsonProperty
    private String expectedResponse;
    @JsonProperty
    private int instances;
    @JsonProperty
    private int periodicity;

    public TxRestEngineDefinition() {
    }

    public String getUrl() {
        return url;
    }
    public String getBody() {
        return body;
    }
    public ActionType getMethod() {
        return method;
    }
    public Map<String, String> getHeaders() { return headers; }
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
    public int getInstances() {
        return instances;
    }
    public int getPeriodicity() {
        return periodicity;
    }
    public String getExpectedResponse() { return expectedResponse; }

    public TxRestEngineDefinition setUrl(String url) {
        this.url = url;
        return this;
    }

    public TxRestEngineDefinition setBody(String body) {
        this.body = body;
        return this;
    }

    public TxRestEngineDefinition setMethod(ActionType method) {
        this.method = method;
        return this;
    }

    public TxRestEngineDefinition setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public TxRestEngineDefinition setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public TxRestEngineDefinition setInstances(int instances) {
        this.instances = instances;
        return this;
    }
    public TxRestEngineDefinition setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
        return this;
    }

    public TxRestEngineDefinition setExpectedResponse(String expectedResponse) {
        this.expectedResponse = expectedResponse;
        return this;
    }

    public String getPrintOut(){
        StringBuilder heads = new StringBuilder();
        if(headers!=null) {
            headers.forEach((k, v) -> heads.append(k + ":" + v));
        }
        StringBuilder queries = new StringBuilder();
        if(queryParams != null) {
            queryParams.forEach((k,v) -> queries.append(k + ":" + v));
        }
        return String.format("%surl: %s\nbody: %s\nmethod: %s\nheaders: %s\nqueryParams: %s\ninstances: %d" +
                        "\nperiodicity: %d\nexpectedResponse: %s",
                super.getPrintOut(), url, body, method, heads, queries, instances, periodicity, expectedResponse);
    }

    // TODO: implement real validators
    public static Predicate<TxRestEngineDefinition> inputDataValidator = i -> true;
    public static Predicate<ActionType> methodValidator = m -> true;
}
