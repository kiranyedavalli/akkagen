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

    private final Logger logger = LoggerFactory.getLogger(ManagementServiceProvider.class);
    private BiFunction<ActionType, AbstractEngineDefinition, Response> createPostPutNBInputBehavior = (t, r) -> {
        try {
            AbstractEngineDefinition res = handleMgmtRequest(new NBInput()
                    .setPath(this.getPath())
                    .setAction(t)
                    .setAbstractEngineDefinition(r));
            return Response.accepted().entity(res).build();
        } catch (AkkagenException e) {
            return handleAkkagenException(e);
        }
    };

    private BiFunction<ActionType, String, Response> createDeleteGetNBInputBehavior = (t, i) -> {
        try {
            AbstractEngineDefinition res = handleMgmtRequest(new NBInput()
                    .setPath(getPath())
                    .setAction(t)
                    .addToQueryParams("id",i));
            return (res == null) ? Response.accepted().entity("deleteId: "+i).build() : Response.ok().entity(res).build();
        }
        catch(AkkagenException e){
            return handleAkkagenException(e);
        }
    };

    private TriFunction<String, ActionType, AbstractEngineDefinition, EngineInput> createEngineInputBehavior = (p, a, r) -> new EngineInput()
            .setPath(p)
            .setAction(a)
            .setAbstractEngineDefinition(r);

    protected Response handlePostPutRequest(ActionType type, AbstractEngineDefinition req,
                                            BiFunction<ActionType, AbstractEngineDefinition, Response> behavior) {
        return behavior.apply(type, req);
    }

    protected Response handleDeleteGetRequest(ActionType type, String id,
                                              BiFunction<ActionType, String, Response> behavior) {
        return behavior.apply(type, id);
    }

    protected Response handleAkkagenException(AkkagenException e){
        switch(e.getType()){
            case NOT_FOUND:
                logger.debug(e.getMessage() + e.getType());
                return Response.status(Response.Status.NOT_FOUND).build();
            case BAD_REQUEST:
                logger.debug(e.getMessage() + e.getType());
                return Response.status(Response.Status.BAD_REQUEST).build();
            default:
                logger.debug(e.getMessage() + e.getType());
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
    protected abstract AbstractEngineDefinition validateAndGetEngineDefinition(AbstractEngineDefinition req) throws AkkagenException;

    /*
    * public methods
    */

    public ManagementServiceProvider(){ }

    public BiFunction<ActionType, AbstractEngineDefinition, Response> getCreatePostPutNBInputBehavior() {
        return createPostPutNBInputBehavior;
    }

    public BiFunction<ActionType, String, Response> getCreateDeleteGetNBInputBehavior() {
        return createDeleteGetNBInputBehavior;
    }

    public AbstractEngineDefinition handleMgmtRequest(NBInput input) throws AkkagenException {

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
                sendToEngine(createEngineInput(input.getPath(), actionType, req, createEngineInputBehavior));
                return req;
            case UPDATE:
                req = input.getAbstractEngineDefinition();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("req: " + req.toString());
                store(validateAndGetEngineDefinition(req), r -> getStorage().updateEngineDefinition(r));
                sendToEngine(createEngineInput(input.getPath(), actionType, req, createEngineInputBehavior));
                return req;
            case DELETE:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                sendToEngine(createEngineInput(input.getPath(), actionType, getStorage().getEngineDefinitionById(id), createEngineInputBehavior));
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

    private EngineInput createEngineInput(String path, ActionType action, AbstractEngineDefinition req,
                                          TriFunction<String, ActionType, AbstractEngineDefinition, EngineInput> behavior){
        return behavior.apply(path, action, req);
    }

    private void sendToEngine(EngineInput req){
        Akkagen.getInstance().getEngineStarter().tell(req, ActorRef.noSender());
        logger.debug("Sent to DataPath: " + req.getPrintOut());
    }

    private void store(AbstractEngineDefinition req, Consumer<AbstractEngineDefinition> storeBehavior) throws AkkagenException {
        storeBehavior.accept(req);
    }
}
