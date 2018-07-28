package com.akkagen.serviceproviders.engine.providers;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.AkkagenAbstractActor;
import com.akkagen.models.EngineInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEngineProvider<T extends AbstractEngineDefinition> extends AkkagenAbstractActor {

    private Logger logger = LoggerFactory.getLogger(AbstractEngineProvider.class);

    private String path;

    public AbstractEngineProvider(ActorSystem system, String path){
        super(system);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    protected abstract void createEngine(T def);
    protected abstract void updateEngine(T def);
    protected abstract void deleteEngine(T def);

    private void processInput(EngineInput input){
        switch(input.getAction()){
            case POST:
                createEngine((T)input.getEngineDefinition());
                break;
            case PUT:
                updateEngine((T)input.getEngineDefinition());
                break;
            case DELETE:
                deleteEngine((T)input.getEngineDefinition());
                break;
            default:
                throw new AkkagenException("Unknown actiontype");
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EngineInput.class, this::processInput)
                .build().orElse(super.createReceive());
    }
}
