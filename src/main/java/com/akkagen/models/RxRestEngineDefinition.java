package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RxRestEngineDefinition extends AbstractEngineDefinition {

    @JsonProperty
    private String uri;
    @JsonProperty
    private String requestBody; // For POST and PUT
    @JsonProperty
    private String method;
    @JsonProperty
    private Map<String, String> headers;
    @JsonProperty
    private String responseBody; // For Get

    // On Successful receipt of the request - Response will always be the requestBody

    public RxRestEngineDefinition() {
    }

    public String getUri() {
        return uri;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getResponseBody() { return responseBody; }

    public RxRestEngineDefinition setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RxRestEngineDefinition setRequestBody(String body) {
        this.requestBody = body;
        return this;
    }

    public RxRestEngineDefinition setMethod(String method) {
        this.method = method;
        return this;
    }

    public RxRestEngineDefinition setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RxRestEngineDefinition setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public boolean equals(RxRestEngineDefinition def){
        if(def.getUri().equals(this.uri) &&
                def.getMethod().equals(this.method) &&
                def.getRequestBody().equals(this.requestBody) &&
                def.getHeaders().entrySet().containsAll(this.headers.entrySet())){
            return true;
        }
        return false;
    }

    public String getPrintOut(){

        StringBuilder heads = new StringBuilder();
        if(headers!=null) {
            headers.forEach((k, v) -> heads.append(k + ":" + v));
        }

        return new StringBuilder().append(super.getPrintOut() + "uri: "+ uri + "\nrequestBody: " + requestBody
                + "\nmethod: " + method + "\nheaders: " + heads
                + "\nresponseBody: " + responseBody).toString();

    }
}
