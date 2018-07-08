package com.akkagen.serviceproviders.management;

import akka.actor.ActorRef;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.ActionType;
import com.akkagen.models.EngineInput;
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
    private BiFunction<ActionType, AbstractEngineDefinition, Response> createNBInputBehavior = (t, r) -> {
        try {
            AbstractEngineDefinition req = handleRequest(new NBInput().setPath(this.getPath()).setAction(t).setAbstractEngineDefinition(r));
            return Response.accepted().entity(req).build();
        } catch (AkkagenException e) {
            return handleAkkagenException(e);
        }
    };

    private TriFunction<String, ActionType, AbstractEngineDefinition, EngineInput> createDRBehavior = (p, a, r) -> new EngineInput()
            .setPath(p)
            .setAction(a)
            .setAbstractEngineDefinition(r);

    protected Response processRequest(ActionType type, AbstractEngineDefinition req,
                                    BiFunction<ActionType, AbstractEngineDefinition, Response> behavior) {
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

    protected AbstractEngineDefinition validateAndGetEngineDefinition(AbstractEngineDefinition req) throws AkkagenException {
        // If no implementation the req is valid
        return req;
    }

    /*
    * public methods
    */

    public ManagementServiceProvider(){ }

    public BiFunction<ActionType, AbstractEngineDefinition, Response> getCreateNBInputBehavior() {
        return createNBInputBehavior;
    }

    public AbstractEngineDefinition handleRequest(NBInput input) throws AkkagenException {

        // Validate the input and send it to datapath
        AbstractEngineDefinition req = null;
        ActionType actionType = input.getAction();
        String id = null;
        switch (actionType) {
            case CREATE:
                req = input.getAbstractEngineDefinition();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("req: " + req.toString());
                store(validateAndGetEngineDefinition(req), r -> getStorage().createEngineDefition(r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case UPDATE:
                req = input.getAbstractEngineDefinition();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("req: " + req.toString());
                store(validateAndGetEngineDefinition(req), r -> getStorage().updateEngineDefinition(r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case DELETE:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, getStorage().getEngineDefinitionById(id), createDRBehavior));
                // TODO: Need to get a confirmation that delete is successful before we delete it from storage.
                getStorage().deleteEngineDefinitionById(id);
                return null;
            case GET:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                return getStorage().getEngineDefinitionById(id);
            case GETALL:
                //TODO
            default:
                throw new AkkagenException("method not supported", AkkagenExceptionType.BAD_REQUEST);
        }

    }

    private EngineInput createDatapathRequest(String path, ActionType action, AbstractEngineDefinition req,
                                              TriFunction<String, ActionType, AbstractEngineDefinition, EngineInput> behavior){
        return behavior.apply(path, action, req);
    }

    private void sendToDatapath(EngineInput req){
        Akkagen.getInstance().getRuntimeService().tell(req, ActorRef.noSender());
    }

    private void store(AbstractEngineDefinition req, Consumer<AbstractEngineDefinition> storeBehavior) throws AkkagenException {
        storeBehavior.accept(req);
    }
}
