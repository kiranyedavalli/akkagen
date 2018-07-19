package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TxRestEngineDefinition extends AbstractEngineDefinition {

    @JsonProperty
    private String url;
    @JsonProperty
    private String body;
    @JsonProperty
    private String method;
    @JsonProperty
    private Map<String, String> headers;
    @JsonProperty
    private Map<String, String> queryParams;

    public TxRestEngineDefinition() {
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public TxRestEngineDefinition setUrl(String url) {
        this.url = url;
        return this;
    }

    public TxRestEngineDefinition setBody(String body) {
        this.body = body;
        return this;
    }

    public TxRestEngineDefinition setMethod(String method) {
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

    public String getPrintOut(){
        StringBuilder heads = new StringBuilder();
        if(headers!=null) {
            headers.forEach((k, v) -> heads.append(k + ":" + v));
        }
        StringBuilder queries = new StringBuilder();
        if(queryParams != null) {
            queryParams.forEach((k,v) -> queries.append(k + ":" + v));
        }
        return String.format("%surl: %s\nbody: %s\nmethod: %s\nheaders: %s\nqueryParams: %s", super.getPrintOut(),
                url, body, method, heads, queries);
    }
}
