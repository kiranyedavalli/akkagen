package com.akkagen.serviceproviders.engine.providers;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.serviceproviders.engine.providers.messages.AbstractEngineMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEngineProvider extends AbstractActor {

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

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AbstractEngineMessage.CreateEngine.class, e -> createEngine(e.getDef()))
                .match(AbstractEngineMessage.UpdateEngine.class, e -> updateEngine(e.getDef()))
                .match(AbstractEngineMessage.DeleteEngine.class, e -> deleteEngine(e.getDef()))
                .build();
    }
}
