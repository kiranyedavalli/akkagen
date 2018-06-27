package datapath;

import akka.actor.AbstractActor;
import akka.actor.Props;
import common.models.DatapathRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPathManager extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(DataPathManager.class);

    public static Props props(){
        return Props.create(DataPathManager.class, () -> new DataPathManager());
    }

    public DataPathManager(){
    }

    private void processRequest(DatapathRequest req){

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DatapathRequest.class, this::processRequest)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
