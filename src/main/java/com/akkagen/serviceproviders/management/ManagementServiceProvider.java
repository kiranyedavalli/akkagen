package com.akkagen.serviceproviders.management;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.*;
import com.akkagen.utils.TriFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static akka.http.javadsl.server.Directives.complete;

public abstract class ManagementServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(ManagementServiceProvider.class);
    private BiFunction<ActionType, AbstractEngineDefinition, Route> createPostPutNBInputBehavior = (t, r) -> {
            try{
                AbstractEngineDefinition def = handleMgmtRequest(new NBInput().setPath(this.getPath()).setAction(t).setAbstractEngineDefinition(r));
                if(def == null){
                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, def, Jackson.marshaller());
                }
                return complete(StatusCodes.ACCEPTED, def, Jackson.marshaller());
            }
            catch(AkkagenException e){
                return handleAkkagenException(e);
            }
    };

    private BiFunction<ActionType, String, Route> createDeleteGetNBInputBehavior = (t, i) -> {
        try{
           AbstractEngineDefinition def = handleMgmtRequest(new NBInput().setPath(getPath()).setAction(t).addToQueryParams("id",i));
           if(t.equals(ActionType.GET) && def == null){
               return complete(StatusCodes.NOT_FOUND, i, Jackson.marshaller());
           }
           return complete(StatusCodes.ACCEPTED, i, Jackson.marshaller());
        }
        catch(AkkagenException e){
            return handleAkkagenException(e);
        }
    };

    // Private methods

    private ManagementServiceProviderStorage getStorage(){
        return Akkagen.getInstance().getServiceProviderFactory().getManagementServiceProviderStorage(this.getPath());
    }

    private TriFunction<String, ActionType, AbstractEngineDefinition, EngineInput> createEngineInputBehavior = (p, a, r) ->
            new EngineInput().setPath(p).setAction(a).setAbstractEngineDefinition(r);

    private Route handleAkkagenException(AkkagenException e){
        switch(e.getType()){
            case NOT_FOUND:
                return complete(StatusCodes.NOT_FOUND, "Resource not found");
            case INTERAL_ERROR:
                return complete(StatusCodes.INTERNAL_SERVER_ERROR, "Internal Server Errror");
            case BAD_REQUEST:
            default:
                return complete(StatusCodes.BAD_REQUEST, "BAD REQUEST");
        }
    }

    private AbstractEngineDefinition handleMgmtRequest(NBInput input) throws AkkagenException {

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

    protected Route handlePostPutRequest(ActionType type, AbstractEngineDefinition req,
                                            BiFunction<ActionType, AbstractEngineDefinition, Route> behavior) {
        return behavior.apply(type, req);
    }

    protected Route handleDeleteGetRequest(ActionType type, String id,
                                              BiFunction<ActionType, String, Route> behavior) {
        return behavior.apply(type, id);
    }

    protected BiFunction<ActionType, AbstractEngineDefinition, Route> getCreatePostPutNBInputBehavior() {
        return createPostPutNBInputBehavior;
    }

    protected BiFunction<ActionType, String, Route> getCreateDeleteGetNBInputBehavior() {
        return createDeleteGetNBInputBehavior;
    }

    protected Route handleRequest(String method, AbstractEngineDefinition req, HttpRequest request){
        switch(method){
            case "POST":
                req.setId(UUID.randomUUID().toString());
                return handlePostPutRequest(ActionType.CREATE, req, getCreatePostPutNBInputBehavior());
            case "PUT":
                return handlePostPutRequest(ActionType.CREATE, req, getCreatePostPutNBInputBehavior());
            case "DELETE":
            case "GET":
            default:
                return complete(StatusCodes.BAD_REQUEST, "Method not supported");
        }
    }
    /*
    * Methods that MUST be implemented by service providers
    */

    public abstract String getPath();
    protected abstract AbstractEngineDefinition validateAndGetEngineDefinition(AbstractEngineDefinition req) throws AkkagenException;
    protected abstract Route handleRestCall(String method, String body, HttpRequest request);

    /*
    * public methods
    */

    public ManagementServiceProvider(){}
}
