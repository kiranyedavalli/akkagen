package com.akkagen.serviceproviders.management;

import akka.actor.ActorRef;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractNBRequest;
import com.akkagen.models.ActionType;
import com.akkagen.models.RuntimeRequest;
import com.akkagen.models.NBInput;
import com.akkagen.utils.TriFunction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class ManagementServiceProvider {

    //TODO: Logger
    private final Logger logger = LoggerFactory.getLogger(ManagementServiceProvider.class);
    private BiFunction<ActionType, AbstractNBRequest, Response> createUpdateBehavior = (t, r) -> {
        try {
            AbstractNBRequest req = handleRequest(new NBInput().setPath(this.getPath()).setAction(t).setAbstractNBRequest(r));
            return Response.accepted().entity(req).build();
        } catch (AkkagenException e) {
            return handleAkkagenException(e);
        }
    };

    private TriFunction<String, ActionType, AbstractNBRequest, RuntimeRequest> createDRBehavior = (p, a, r) -> new RuntimeRequest()
            .setPath(p)
            .setAction(a)
            .setAbstractNBRequest(r);

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

    public abstract String getPath();

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
                req = input.getAbstractNBRequest();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("req: " + req.toString());
                store(validateAndGetNBRequest(req), r -> getStorage().createNBRequest(r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case UPDATE:
                req = input.getAbstractNBRequest();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("req: " + req.toString());
                store(validateAndGetNBRequest(req), r -> getStorage().updateNBRequest(r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case DELETE:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, getStorage().getNBRequestById(id), createDRBehavior));
                // TODO: Need to get a confirmation that delete is successful before we delete it from storage.
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

    private RuntimeRequest createDatapathRequest(String path, ActionType action, AbstractNBRequest req,
                                                 TriFunction<String, ActionType, AbstractNBRequest, RuntimeRequest> behavior){
        return behavior.apply(path, action, req);
    }

    private void sendToDatapath(RuntimeRequest req){
        Akkagen.getInstance().getRuntimeService().tell(req, ActorRef.noSender());
    }

    private void store(AbstractNBRequest req, Consumer<AbstractNBRequest> storeBehavior) throws AkkagenException {
        storeBehavior.accept(req);
    }
}
