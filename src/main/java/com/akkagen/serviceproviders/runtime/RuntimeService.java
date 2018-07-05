package com.akkagen.serviceproviders.runtime;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.RuntimeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeService extends AbstractActor {

    //TODO: Logging

    private final Logger logger = LoggerFactory.getLogger(RuntimeService.class);

    public static Props props(){
        return Props.create(RuntimeService.class, () -> new RuntimeService());
    }

    private RuntimeService(){ }

    private void processRequest(RuntimeRequest req) throws AkkagenException{

        RuntimeServiceProvider sp = Akkagen.getInstance().getServiceProviderFactory().getRuntimeServiceProvider(req.getPath());

        switch(req.getAction()){
            case CREATE:
                sp.createRuntimeService(req.getAbstractNBRequest());
                break;
            case UPDATE:
                sp.updateRuntimeService(req.getAbstractNBRequest());
                break;
            case DELETE:
                sp.deleteRuntimService(req.getAbstractNBRequest().getId());
                break;
            default:
                throw new AkkagenException("Unknown ActionType", AkkagenExceptionType.BAD_REQUEST);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RuntimeRequest.class, this::processRequest)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
