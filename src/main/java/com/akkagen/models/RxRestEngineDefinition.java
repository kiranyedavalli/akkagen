package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.akkagen.utils.utils.getJSONStringDiff;
import static com.akkagen.utils.utils.getMapAsString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RxRestEngineDefinition extends AbstractEngineDefinition {

    private final Logger logger = LoggerFactory.getLogger(RxRestEngineDefinition.class);

    @JsonProperty
    private String uri;
    @JsonProperty
    private String requestBody; // For POST and PUT
    @JsonProperty
    private String method;
    @JsonProperty
    private Map<String, String> headers = new HashMap<>();
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
        if(!this.uri.equals(def.getUri())) {
            logger.debug("Input URI " + def.getUri() + " does not match stored URI: " + this.uri);
            return false;
        }

        if(!this.method.equals(def.getMethod())) {
            logger.debug("Input method " + def.getMethod() + " does not match stored method: " + this.method);
            return false;
        }

        logger.debug("Request body diff: " + getJSONStringDiff(this.requestBody, def.getRequestBody()));
/*
        if(!(this.requestBody.contains(def.getRequestBody()) || def.getRequestBody().contains(this.requestBody))) {
            logger.debug("Input RequestBody " + def.getRequestBody() + " does not match stored RequestBody: " + this.requestBody);
            return false;
        }
*/

        if(!(this.headers.entrySet().containsAll(def.getHeaders().entrySet()) ||
                def.getHeaders().entrySet().containsAll(this.headers.entrySet()))) {
            logger.debug("Input Headers " + getMapAsString(def.getHeaders()) + " does not match stored headers: " + getMapAsString(this.headers));
            return false;
        }
        return true;
    }

    public String getPrintOut(){
        return new StringBuilder().append(super.getPrintOut() + "uri: "+ uri + "\nrequestBody: " + requestBody
                + "\nmethod: " + method + "\nheaders: " + getMapAsString(headers)
                + "\nresponseBody: " + responseBody).toString();

    }
}
