package common.models;

public class DatapathRequest {

    private String id;
    private String path;
    private ActionType action;
    private AbstractNBRequest abstractNBRequest;

    public DatapathRequest(){

    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public ActionType getAction() {
        return action;
    }

    public AbstractNBRequest getAbstractNBRequest() {
        return abstractNBRequest;
    }


    public DatapathRequest setPath(String path) {
        this.path = path;
        return this;
    }

    public DatapathRequest setId(String id) {
        this.id = id;
        return this;
    }

    public DatapathRequest setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public DatapathRequest setAbstractNBRequest(AbstractNBRequest abstractNBRequest) {
        this.abstractNBRequest = abstractNBRequest;
        return this;
    }
}
