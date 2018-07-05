package com.akkagen.serviceproviders.runtime.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.models.AbstractNBRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.models.TxRestNBRequest;

public class TxRestActor extends AkkagenAbstractActor {

    private final Logger logger = LoggerFactory.getLogger(TxRestActor.class);
    private TxRestNBRequest txRestNBRequest;

    public static Props props(ActorSystem system){
        return Props.create(TxRestActor.class, () -> new TxRestActor(system));
    }

    private TxRestActor(ActorSystem sys) {
        super(sys);
    }

    @Override
    protected void processRequest(AbstractNBRequest req) {
        this.txRestNBRequest = (TxRestNBRequest) req;
        //TODO: do the rest client
    }

}
