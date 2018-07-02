package infra;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
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

    private void processRequest(DatapathRequest req) throws AkkagenException{

        RuntimeServiceProvider sp = Akkagen.getInstance().getServiceProviderFactory().getRuntimeServiceProvider(req.getPath());

        switch(req.getAction()){
            case CREATE:
                sp.createRuntimeService(req);
                break;
            case UPDATE:
                sp.updateRuntimeService(req);
                break;
            case DELETE:
                sp.deleteRuntimService(req);
                break;
            default:
                throw new AkkagenException("Unknown ActionType", AkkagenExceptionType.BAD_REQUEST);
        }

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DatapathRequest.class, this::processRequest)
                .matchAny(o -> logger.info("%s:: received unknown message",getSelf().toString()))
                .build();
    }
}
