package com.akkagen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractNBRequest {

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
    public int getInstances() {
        return instances;
    }
    public int getPeriodicity() {
        return periodicity;
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
    public AbstractNBRequest setInstances(int instances) {
        this.instances = instances;
        return this;
    }
    public AbstractNBRequest setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
        return this;
    }
}
