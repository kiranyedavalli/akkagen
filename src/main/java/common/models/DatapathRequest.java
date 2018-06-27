package common.models;

public class DatapathRequest {

    private String requestId;
    private ActionType action;
    private OperationType operation;
    private AbstractNBRequest abstractNBRequest;

    public DatapathRequest(){

    }

    public String getRequestId() {
        return requestId;
    }

    public ActionType getAction() {
        return action;
    }

    public OperationType getOperation() {
        return operation;
    }

    public AbstractNBRequest getAbstractNBRequest() {
        return abstractNBRequest;
    }

    public DatapathRequest setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public DatapathRequest setAction(ActionType action) {
        this.action = action;
        return this;
    }

    public DatapathRequest setOperation(OperationType operation) {
        this.operation = operation;
        return this;
    }

    public DatapathRequest setAbstractNBRequest(AbstractNBRequest abstractNBRequest) {
        this.abstractNBRequest = abstractNBRequest;
        return this;
    }
}
