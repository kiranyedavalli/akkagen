package com.akkagen.serviceproviders.management;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.http.scaladsl.model.HttpMethods;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.*;
import com.akkagen.utils.TriFunction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static akka.http.javadsl.server.Directives.complete;
import static com.akkagen.utils.utils.getObjectFromJson;

public class ManagementServiceProvider<T extends AbstractEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(ManagementServiceProvider.class);
    private ManagementServiceProviderStorage<T> storage;
    private BiFunction<ActionType, T, Route> createPostPutNBInputBehavior = (t, r) -> {
            try{
                NBInput<T> nbInput = new NBInput().setPath(this.getPath()).setAction(t).setEngineDefinition(r);
                T def = handleMgmtRequest(nbInput);
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
            NBInput<T> nbInput = new NBInput().setPath(this.getPath()).setAction(t).addToQueryParams("id",i);
           T def = handleMgmtRequest(nbInput);
           if(t.equals(ActionType.GET)) {
               if (def == null) {
                   return complete(StatusCodes.NOT_FOUND, i, Jackson.marshaller());
               } else {
                   return complete(StatusCodes.OK, def, Jackson.marshaller());
               }
           }
           return complete(StatusCodes.ACCEPTED, i, Jackson.marshaller());
        }
        catch(AkkagenException e){
            return handleAkkagenException(e);
        }
    };

    private ManagementServiceProviderStorage<T> getStorage(){
        return storage;
    }

    private TriFunction<String, ActionType, T, EngineInput> createEngineInputBehavior = (p, a, r) ->
            new EngineInput().setPath(p).setAction(a).setEngineDefinition(r);

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

    private T handleMgmtRequest(NBInput<T> input) throws AkkagenException {
        T req = null;
        ActionType actionType = input.getAction();
        String id = null;
        switch (actionType) {
            case POST:
            case PUT:
                req = input.getEngineDefinition();
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("Received " + actionType + " for req: " + req.getPrintOut());
                if(!inputValidator.test(req)){
                    logger.debug("invalid input");
                    throw new AkkagenException("Input invalid", AkkagenExceptionType.BAD_REQUEST);
                }
                store(req, r -> getStorage().createEngineDefition(r));
                sendToEngine(createEngineInput(input.getPath(), actionType, req, createEngineInputBehavior));
                return req;
            case DELETE:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("Received " + actionType + " for id: " + id);
                sendToEngine(createEngineInput(input.getPath(), actionType, getStorage().getEngineDefinitionById(id), createEngineInputBehavior));
                // TODO: Need to get a confirmation that delete is successful before we delete it from storage.
                getStorage().deleteEngineDefinitionById(id);
                return null;
            case GET:
                id = input.getQueryParams().get("id");
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                logger.debug("Received " + actionType + " for id: " + id);
                return getStorage().getEngineDefinitionById(id);
            case GETALL:
                //TODO
            default:
                throw new AkkagenException("method not supported", AkkagenExceptionType.BAD_REQUEST);
        }

    }

    private EngineInput createEngineInput(String path, ActionType action, T req,
                                          TriFunction<String, ActionType, T, EngineInput> behavior){
        return behavior.apply(path, action, req);
    }

    private void sendToEngine(EngineInput req){
        ActorRef esp = Akkagen.getInstance().getServiceProviderFactory().getEngineProvider(getPath());
        esp.tell(req, ActorRef.noSender());
        logger.debug("Sent \n" + req.getPrintOut() + " \n to Engine Provider: " + esp.toString());
    }

    private void store(T req, Consumer<T> storeBehavior) throws AkkagenException {
        storeBehavior.accept(req);
    }

    private Route handlePostPutRequest(ActionType type, T req,
                                            BiFunction<ActionType, T, Route> behavior) {
        return behavior.apply(type, req);
    }

    private Route handleDeleteGetRequest(ActionType type, String id,
                                              BiFunction<ActionType, String, Route> behavior) {
        return behavior.apply(type, id);
    }

    private BiFunction<ActionType, T, Route> getCreatePostPutNBInputBehavior() {
        return createPostPutNBInputBehavior;
    }

    private BiFunction<ActionType, String, Route> getCreateDeleteGetNBInputBehavior() {
        return createDeleteGetNBInputBehavior;
    }

    private Class<T> klass;
    private String path;
    private Predicate<T> inputValidator;
    private Predicate<ActionType> methodValidator;
    public ManagementServiceProvider(String path, Class<T> klass, Predicate<T> inputValidator, Predicate<ActionType> methodValidator){
        this.path = path;
        this.klass = klass;
        this.inputValidator = inputValidator;
        this.methodValidator = methodValidator;
        storage = new ManagementServiceProviderStorage<T>();
    }
    public String getPath(){return this.path;}
    public Route handleRestCall(ActionType type, String body, HttpRequest request){
        if(!methodValidator.test(type)){
            return complete(StatusCodes.BAD_REQUEST, "Method: " + type.name() + " not supported");
        }
        T req = getObjectFromJson(body, klass);
        Optional<String> id = request.getUri().query().get("id");
        switch(type){
            case POST:
                req.setId(UUID.randomUUID().toString());
                return handlePostPutRequest(ActionType.POST, req, getCreatePostPutNBInputBehavior());
            case PUT:
                return handlePostPutRequest(ActionType.PUT, req, getCreatePostPutNBInputBehavior());
            case DELETE:
                if(id.isPresent()){
                    logger.debug("Got " + type + " request for id: " + id.get());
                }
                else{
                    logger.debug("Got " + type + " request for invalid id");
                    return complete(StatusCodes.BAD_REQUEST, "Invalid query input");
                }
                return handleDeleteGetRequest(ActionType.DELETE, id.get(), getCreateDeleteGetNBInputBehavior());
            case GET:
                if(id.isPresent()){
                    logger.debug("Got " + type + " request for id: " + id.get());
                }
                else{
                    logger.debug("Got " + type + " request for invalid id");
                    return complete(StatusCodes.BAD_REQUEST, "Invalid query input");
                }
                return handleDeleteGetRequest(ActionType.GET, id.get(), getCreateDeleteGetNBInputBehavior());
            default:
                return complete(StatusCodes.BAD_REQUEST, "Method not supported");
        }
    }
}
