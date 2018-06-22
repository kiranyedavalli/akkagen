package common;

import java.util.Map;

public class TxRestRequest extends AbstractRequest {

    private String url;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> queryParams;

    public TxRestRequest() {
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public TxRestRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public TxRestRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public TxRestRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public TxRestRequest setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }
}
