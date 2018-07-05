package com.akkagen.serviceproviders.runtime.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractNBRequest;
import com.akkagen.serviceproviders.runtime.actors.messages.RunMessage;
import com.akkagen.serviceproviders.runtime.actors.messages.StopMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AkkagenAbstractActor extends AbstractActor {

    //TODO: Lgger
    private final Logger logger = LoggerFactory.getLogger(AkkagenAbstractActor.class);
    private ActorSystem system;
    private AbstractNBRequest abstractNBRequest;

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
        getContext().stop(getSelf());
    }

    private void runService(AbstractNBRequest req){
        setAbstractNBRequest(req);
        processRequest(req);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RunMessage.class, m -> runService(m.getReq()))
                .match(StopMessage.class, m -> stopService(m.getId()))
                .build();
    }
}
