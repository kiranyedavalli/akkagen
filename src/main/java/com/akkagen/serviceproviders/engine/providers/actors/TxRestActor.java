package com.akkagen.serviceproviders.engine.providers.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.models.AbstractEngineDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.models.TxRestEngineDefinition;

public class TxRestActor extends EngineAbstractActor<TxRestEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(TxRestActor.class);

    public static Props props(){
        return Props.create(TxRestActor.class, TxRestActor::new);
    }

    private TxRestActor(){}

    @Override
    protected void runEngine(TxRestEngineDefinition req) {
        //TODO: do the rest client
        logger.debug("In TxRestActor " + getSelf() + ":: " + req.toString() + "with req: " + req.getPrintOut());
    }
}
