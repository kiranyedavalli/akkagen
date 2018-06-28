package serviceproviders.management.models;

import common.models.OperationType;

public abstract class AbstractNBRequest {

    private String id;
    private String name;
    private String description;
    private OperationType operation;

    public AbstractNBRequest() {
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public OperationType getOperation() {
        return operation;
    }

    public AbstractNBRequest setId(String id) {
        this.id = id;
        return this;
    }
    public AbstractNBRequest setName(String name) {
        this.name = name;
        return this;
    }
    public AbstractNBRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public AbstractNBRequest setOperation(OperationType operation) {
        this.operation = operation;
        return this;
    }
}
