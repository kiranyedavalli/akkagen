package common;

public abstract class AbstractRequest {

    private String id;
    private String name;
    private String description;

    public AbstractRequest() {
    }

    public String getId() {
        return id;
    }

    public AbstractRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AbstractRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AbstractRequest setDescription(String description) {
        this.description = description;
        return this;
    }
}
