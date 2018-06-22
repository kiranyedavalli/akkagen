package datapath;

import akka.actor.AbstractActor;
import common.AbstractRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPathManager extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(DataPathManager.class);

    public DataPathManager(){

    }

    private void processRequest(AbstractRequest req){

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AbstractRequest.class, this::processRequest)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
