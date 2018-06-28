package infra;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import common.models.DatapathRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceproviders.RuntimeServiceProvider;

public class RuntimeService extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(RuntimeService.class);
    private ActorSystem system;

    public static Props props(ActorSystem system){
        return Props.create(RuntimeService.class, () -> new RuntimeService(system));
    }

    public RuntimeService(ActorSystem system){
        this.system = system;
    }

    private void processRequest(DatapathRequest req){

        RuntimeServiceProvider sp = Akkagen.getInstance().getServiceProviderFactory().getRuntimeServiceProvider(req.getPath());

        // Create/update/delete the actor

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DatapathRequest.class, this::processRequest)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
