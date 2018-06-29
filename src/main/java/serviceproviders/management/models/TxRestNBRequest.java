package serviceproviders.management.models;

import common.models.AbstractNBRequest;

import java.util.Map;

public class TxRestNBRequest extends AbstractNBRequest {

    private String url;
    private String body;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> queryParams;

    public TxRestNBRequest() {
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

    public TxRestNBRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public TxRestNBRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public TxRestNBRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    public TxRestNBRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public TxRestNBRequest setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }
}
