package com.akkagen.serviceproviders.engine.engineactors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.serviceproviders.engine.engineactors.messages.RunMessage;
import com.akkagen.serviceproviders.engine.engineactors.messages.StopMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class EngineAbstractActor extends AbstractActorWithTimers {

    //TODO: Lgger
    private final Logger logger = LoggerFactory.getLogger(EngineAbstractActor.class);
    private ActorSystem system;
    private AbstractEngineDefinition abstractEngineDefinition;
    private static Object TICK_KEY = "TickKey";
    private static final class Tick {}

    protected EngineAbstractActor(ActorSystem sys) {
        this.system = sys;
    }

    // Has to be implemented by individual request Actors
    protected void runEngine(AbstractEngineDefinition req){
        // default is do nothing
        // Override this and do something interesting
        return;
    }

    protected AbstractEngineDefinition getAbstractEngineDefinition() {
        return abstractEngineDefinition;
    }

    protected void setAbstractEngineDefinition(AbstractEngineDefinition abstractEngineDefinition) {
        this.abstractEngineDefinition = abstractEngineDefinition;
    }

    private void stopService(String id) {
        if(!id.equals(this.abstractEngineDefinition.getId())){
            throw new AkkagenException("Wrong Actor is called for stopping service!", AkkagenExceptionType.INTERAL_ERROR);
        }
        System.out.println("Stopping Actor: " + getSelf());
        getContext().stop(getSelf());
    }

    private void runService(AbstractEngineDefinition req) {
        setAbstractEngineDefinition(req);
        runEngine(req);
        if (req.getPeriodicity() > 0) {
            getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofMillis(req.getPeriodicity()));
        }
    }

    private void periodicService(Tick t){
        runEngine(getAbstractEngineDefinition());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunMessage.class, m -> runService(m.getReq()))
                .match(StopMessage.class, m -> stopService(m.getId()))
                .match(Tick.class,this::periodicService)
                .build();
    }
}
