package com.akkagen.serviceproviders.engine;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.EngineInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineStarter extends AbstractActor {

    //TODO: Logging

    private final Logger logger = LoggerFactory.getLogger(EngineStarter.class);

    public static Props props(){
        return Props.create(EngineStarter.class, () -> new EngineStarter());
    }

    private EngineStarter(){ }

    private void processRequest(EngineInput req) throws AkkagenException{

        EngineProvider sp = Akkagen.getInstance().getServiceProviderFactory().getRuntimeServiceProvider(req.getPath());

        switch(req.getAction()){
            case CREATE:
                sp.createEngines(req.getAbstractEngineDefinition());
                break;
            case UPDATE:
                sp.updateEngines(req.getAbstractEngineDefinition());
                break;
            case DELETE:
                sp.deleteEngines(req.getAbstractEngineDefinition().getId());
                break;
            default:
                throw new AkkagenException("Unknown ActionType", AkkagenExceptionType.BAD_REQUEST);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EngineInput.class, this::processRequest)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
