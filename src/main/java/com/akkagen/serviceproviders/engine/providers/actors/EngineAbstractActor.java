package com.akkagen.serviceproviders.engine.providers.actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.serviceproviders.engine.calculators.CalculatorRouter;
import com.akkagen.serviceproviders.engine.providers.messages.RunMessage;
import com.akkagen.serviceproviders.engine.providers.messages.StopMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class EngineAbstractActor extends AbstractActorWithTimers {

    private final Logger logger = LoggerFactory.getLogger(EngineAbstractActor.class);
    private AbstractEngineDefinition abstractEngineDefinition;
    private CalculatorRouter calculator;
    private static Object TICK_KEY = "TickKey";
    private static final class Tick {}
    private static final class FirstTick{}

    protected EngineAbstractActor(){}

    // Has to be implemented by individual request Actors
    protected abstract void runEngine(AbstractEngineDefinition req);

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
        logger.debug("Stopping Actor: " + getSelf());
        getContext().stop(getSelf());
    }

    private void runService(AbstractEngineDefinition req) {
        setAbstractEngineDefinition(req);
        runEngine(req);
        logger.debug("Engine " + getSelf() + " started for id: " + req.getId());
        getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(req.getPeriodicity()));
    }

    private void firstTick(FirstTick t){
        if (abstractEngineDefinition.getPeriodicity() > 0) {
            getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofMillis(abstractEngineDefinition.getPeriodicity()));
            logger.debug("Periodic timer for " + getSelf() + "started for id: "
                    + abstractEngineDefinition.getId() + " of " + abstractEngineDefinition.getPeriodicity()
                    + " milli-seconds");
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
                .match(FirstTick.class, this::firstTick)
                .match(Tick.class, this::periodicService)
                .build();
    }
}
