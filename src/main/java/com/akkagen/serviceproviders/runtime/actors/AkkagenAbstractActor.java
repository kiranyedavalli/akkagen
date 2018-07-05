package com.akkagen.serviceproviders.runtime.actors;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractNBRequest;
import com.akkagen.serviceproviders.runtime.actors.messages.RunMessage;
import com.akkagen.serviceproviders.runtime.actors.messages.StopMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class AkkagenAbstractActor extends AbstractActorWithTimers {

    //TODO: Lgger
    private final Logger logger = LoggerFactory.getLogger(AkkagenAbstractActor.class);
    private ActorSystem system;
    private AbstractNBRequest abstractNBRequest;
    private static Object TICK_KEY = "TickKey";
    private static final class Tick {}

    protected AkkagenAbstractActor(ActorSystem sys) {
        this.system = sys;
    }

    // Has to be implemented by individual request Actors
    protected void processRequest(AbstractNBRequest req){
        // default is do nothing
        // Override this and do something interesting
        return;
    }

    protected AbstractNBRequest getAbstractNBRequest() {
        return abstractNBRequest;
    }

    protected void setAbstractNBRequest(AbstractNBRequest abstractNBRequest) {
        this.abstractNBRequest = abstractNBRequest;
    }

    private void stopService(String id) {
        if(!id.equals(this.abstractNBRequest.getId())){
            throw new AkkagenException("Wrong Actor is called for stopping service!", AkkagenExceptionType.INTERAL_ERROR);
        }
        System.out.println("Stopping Actor: " + getSelf());
        getContext().stop(getSelf());
    }

    private void runService(AbstractNBRequest req) {
        setAbstractNBRequest(req);
        processRequest(req);
        if (req.getPeriodicity() > 0) {
            getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofMillis(req.getPeriodicity()));
        }
    }

    private void periodicService(Tick t){
        processRequest(getAbstractNBRequest());
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
