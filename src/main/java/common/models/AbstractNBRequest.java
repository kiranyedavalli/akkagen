package common.models;

public abstract class AbstractNBRequest {

    private String id;
    private String name;
    private String description;

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
}
