package com.akkagen.serviceproviders.engine;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.EngineInput;
import com.akkagen.serviceproviders.engine.providers.messages.AbstractEngineMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineStarter extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(EngineStarter.class);

    public static Props props(){
        return Props.create(EngineStarter.class, EngineStarter::new);
    }

    private EngineStarter(){}

    private void processInput(EngineInput input) throws AkkagenException{

        ActorRef ep = Akkagen.getInstance().getServiceProviderFactory().getEngineProvider(input.getPath());
        logger.debug("Received Engine Input for service provider " + ep.toString());

        switch(input.getAction()){
            case CREATE:
                ep.tell(new AbstractEngineMessage.CreateEngine(input.getAbstractEngineDefinition()), getSelf());
                break;
            case UPDATE:
                ep.tell(new AbstractEngineMessage.UpdateEngine(input.getAbstractEngineDefinition()), getSelf());
                break;
            case DELETE:
                ep.tell(new AbstractEngineMessage.DeleteEngine(input.getAbstractEngineDefinition()), getSelf());
                break;
            default:
                throw new AkkagenException("Unknown ActionType", AkkagenExceptionType.BAD_REQUEST);
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
