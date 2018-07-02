package common.models;

import java.util.HashMap;

public class NBInput {

    private String path;
    private ActionType action;
    private AbstractNBRequest body;

    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> queryParams = new HashMap<>();

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

    public NBInput addToHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public NBInput addToQueryParams(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }
}
