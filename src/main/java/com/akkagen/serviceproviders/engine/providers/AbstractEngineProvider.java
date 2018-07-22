package com.akkagen.serviceproviders.engine.providers;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.EngineInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEngineProvider<T extends AbstractEngineDefinition> extends AbstractActor {

    private Logger logger = LoggerFactory.getLogger(AbstractEngineProvider.class);

    private ActorSystem system;
    private String path;

    public AbstractEngineProvider(ActorSystem system, String path){
        this.system = system;
        this.path = path;
    }

    public ActorSystem getSystem() {
        return system;
    }

    public String getPath() {
        return path;
    }

    protected abstract void createEngine(AbstractEngineDefinition def);
    protected abstract void updateEngine(AbstractEngineDefinition def);
    protected abstract void deleteEngine(AbstractEngineDefinition def);

    public void processInput(EngineInput input){
        switch(input.getAction()){
            case CREATE:
                createEngine(input.getEngineDefinition());
                break;
            case UPDATE:
                updateEngine(input.getEngineDefinition());
                break;
            case DELETE:
                deleteEngine(input.getEngineDefinition());
                break;
            default:
                throw new AkkagenException("Unknown actiontype");
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EngineInput.class, this::processInput)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
