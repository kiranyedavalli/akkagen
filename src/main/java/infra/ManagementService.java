package infra;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import common.models.AbstractRestServer;
import serviceproviders.management.models.AbstractNBRequest;
import common.models.ActionType;
import common.models.DatapathRequest;
import serviceproviders.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

public class ManagementService extends AbstractRestServer {

    private final Logger logger = LoggerFactory.getLogger(ManagementService.class);
    private ActorSystem system;


    public ManagementService(String host, int port, ActorSystem system){
        super(system, host, port);
        this.system = system;
    }

    @Override
    protected void handleRequest(String method, String path, String body) throws AkkagenException {

        // Get the Service Provider
        ManagementServiceProvider sp = Akkagen.getInstance().getServiceProviderFactory().getManagementServiceProvider(path);
        if(sp == null){
            throw new AkkagenException("Resource not found", AkkagenExceptionType.NOT_FOUND);
        }

        // Validate the input and send it
        AbstractNBRequest req = null;
        ActionType type = ActionType.getActionType(method);
        try {
            switch (type) {
                case CREATE:
                    req = sp.createNBRequest(body);
                    if(req != null) {
                        sendToDatapath(createDatapathRequest(sp.getPath(), req, (p, r) -> new DatapathRequest()
                                .setPath(p)
                                .setRequestId(r.getId())
                                .setAction(type)
                                .setOperation(r.getOperation())
                                .setAbstractNBRequest(r)));
                    }
                    break;
                case UPDATE:
                    req = sp.updateNBRequest(body);
                    if(req != null) {
                        sendToDatapath(createDatapathRequest(sp.getPath(), req, (p, r) -> new DatapathRequest()
                                .setPath(p)
                                .setRequestId(r.getId())
                                .setAction(type)
                                .setOperation(r.getOperation())
                                .setAbstractNBRequest(r)));
                    }
                    break;
                case DELETE:
                    String id = getResourceIdFromPath(sp.getPath());
                    if(id != null) {
                        sendToDatapath(createDatapathRequest(id, null, (p, r) -> new DatapathRequest()
                                .setRequestId(p)
                                .setAction(type)));
                        sp.deleteNBRequest(id);
                    }
                    break;
                default:
                    throw new AkkagenException("method not supported", AkkagenExceptionType.BAD_REQUEST);
            }
        }
        catch(AkkagenException e){
            throw e;
        }

    }

    private String getResourceIdFromPath(String path) throws AkkagenException {
        // TODO: parse and get the id
        return null;
    }

    private DatapathRequest createDatapathRequest(String path, AbstractNBRequest req,
                                                  BiFunction<String, AbstractNBRequest, DatapathRequest> behavior){
        return behavior.apply(path, req);
    }

    private void sendToDatapath(DatapathRequest req){
        Akkagen.getInstance().getRuntimeService().tell(req, ActorRef.noSender());
    }

}
