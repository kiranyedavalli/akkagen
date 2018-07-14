package com.akkagen.serviceproviders.engine.engineactors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.models.AbstractEngineDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.models.TxRestEngineDefinition;

public class TxRestActor extends EngineAbstractActor {

    private final Logger logger = LoggerFactory.getLogger(TxRestActor.class);
    private TxRestEngineDefinition txRestNBRequest;

    public static Props props(ActorSystem system){
        return Props.create(TxRestActor.class, () -> new TxRestActor(system));
    }

    private TxRestActor(ActorSystem sys) {
        super(sys);
    }

    @Override
    protected void runEngine(AbstractEngineDefinition req) {
        this.txRestNBRequest = (TxRestEngineDefinition) req;
        //TODO: do the rest client
        logger.debug("In TxRestActor " + getSelf() + ":: " + req.toString() + "with req: " + txRestNBRequest.getPrintOut());
    }
}
