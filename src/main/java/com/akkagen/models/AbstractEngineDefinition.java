package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractEngineDefinition {

    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private int instances;
    @JsonProperty
    private int periodicity;

    public AbstractEngineDefinition() {
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
    public int getInstances() {
        return instances;
    }
    public int getPeriodicity() {
        return periodicity;
    }

    public AbstractEngineDefinition setId(String id) {
        this.id = id;
        return this;
    }
    public AbstractEngineDefinition setName(String name) {
        this.name = name;
        return this;
    }
    public AbstractEngineDefinition setDescription(String description) {
        this.description = description;
        return this;
    }
    public AbstractEngineDefinition setInstances(int instances) {
        this.instances = instances;
        return this;
    }
    public AbstractEngineDefinition setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
        return this;
    }

    public String getPrintOut(){
        return String.format("id: %s\nname: %s\ndescription: %s\ninstances: %d\nperiodicity: %d\n",
                id, name, description, instances, periodicity);
    }
}
