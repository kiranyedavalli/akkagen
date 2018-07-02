package serviceproviders;

import akka.actor.ActorRef;
import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import common.models.AbstractNBRequest;
import common.models.ActionType;
import common.models.DatapathRequest;
import common.models.NBInput;
import common.utils.TriFunction;
import infra.Akkagen;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class ManagementServiceProvider implements ServiceProvider {

    private final Logger log = LoggerFactory.getLogger(ManagementServiceProvider.class);
    private BiFunction<ActionType, AbstractNBRequest, Response> createUpdateBehavior = (t, r) -> {
        try {
            AbstractNBRequest req = handleRequest(new NBInput().setPath(this.getPath()).setAction(t).setBody(r));
            return Response.accepted().entity(req).build();
        } catch (AkkagenException e) {
            return handleAkkagenException(e);
        }
    };

    private TriFunction<String, ActionType, AbstractNBRequest, DatapathRequest> createDRBehavior = (p, a, r) -> new DatapathRequest()
            .setPath(p)
            .setId(r.getId())
            .setAction(a)
            .setAbstractNBRequest(r);
    private TriFunction<String, ActionType, AbstractNBRequest, DatapathRequest> deleteDRBehavior = (i, a, r) -> new DatapathRequest()
            .setId(i)
            .setAction(a);

    protected Response processRequest(ActionType type, AbstractNBRequest req,
                                    BiFunction<ActionType, AbstractNBRequest, Response> behavior) {
        return behavior.apply(type, req);
    }

    protected Response handleAkkagenException(AkkagenException e){
        switch(e.getType()){
            case NOT_FOUND:
                return Response.status(Response.Status.NOT_FOUND).build();
            case BAD_REQUEST:
                return Response.status(Response.Status.BAD_REQUEST).build();
            default:
                return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private ManagementServiceProviderStorage getStorage(){
        return Akkagen.getInstance().getServiceProviderFactory().getManagementServiceProviderStorage(this.getPath());
    }

    /*
    * Methods that MUST be implemented by service providers
    */

    protected AbstractNBRequest validateAndGetNBRequest(AbstractNBRequest req) throws AkkagenException {
        // If no implementation the req is valid
        return req;
    }

    /*
    * public methods
    */

    public ManagementServiceProvider(){ }

    public BiFunction<ActionType, AbstractNBRequest, Response> getCreateUpdateBehavior() {
        return createUpdateBehavior;
    }

    public AbstractNBRequest handleRequest(NBInput input) throws AkkagenException {

        // Validate the input and send it to datapath
        AbstractNBRequest req = null;
        ActionType actionType = input.getAction();
        String id = null;
        switch (actionType) {
            case CREATE:
                req = input.getBody();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                log.debug("req: " + req.toString());
                store(validateAndGetNBRequest(req), r -> getStorage().createNBRequest(r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case UPDATE:
                req = input.getBody();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                log.debug("req: " + req.toString());
                store(validateAndGetNBRequest(req), r -> getStorage().updateNBRequest(r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case DELETE:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                sendToDatapath(createDatapathRequest(id, actionType,null, deleteDRBehavior));
                getStorage().deleteNBRequestById(id);
                return null;
            case GET:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                return getStorage().getNBRequestById(id);
            case GETALL:
                //TODO
            default:
                throw new AkkagenException("method not supported", AkkagenExceptionType.BAD_REQUEST);
        }

    }

    private DatapathRequest createDatapathRequest(String path, ActionType action, AbstractNBRequest req,
                                                  TriFunction<String, ActionType, AbstractNBRequest, DatapathRequest> behavior){
        return behavior.apply(path, action, req);
    }

    private void sendToDatapath(DatapathRequest req){
        Akkagen.getInstance().getRuntimeService().tell(req, ActorRef.noSender());
    }

    private void store(AbstractNBRequest req, Consumer<AbstractNBRequest> storeBehavior) throws AkkagenException {
        storeBehavior.accept(req);
    }
}
