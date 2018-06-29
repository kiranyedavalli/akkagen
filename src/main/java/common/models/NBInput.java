package common.models;

import java.util.HashMap;

public class NBInput {

    private String path;
    private ActionType action;
    private AbstractNBRequest body;

    private HashMap<String, String> headers;
    private HashMap<String, String> queryParams;

    public NBInput() {
    }

    public ActionType getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    public AbstractNBRequest getBody() {
        return body;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HashMap<String, String> getQueryParams() {
        return queryParams;
    }

    public NBInput setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public NBInput setPath(String path) {
        this.path = path;
        return this;
    }

    public NBInput setBody(AbstractNBRequest body) {
        this.body = body;
        return this;
    }

    public NBInput setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public NBInput setQueryParams(HashMap<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }
}
