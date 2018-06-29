package serviceproviders;

import akka.actor.ActorRef;
import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import common.models.AbstractNBRequest;
import common.models.ActionType;
import common.models.DatapathRequest;
import common.models.NBInput;
import common.utils.TriFunction;
import common.utils.utils;
import infra.Akkagen;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ManagementServiceProvider implements ServiceProvider {

    private ConcurrentHashMap<String, AbstractNBRequest> storage = new ConcurrentHashMap<>();
    private TriFunction<String, ActionType, AbstractNBRequest, DatapathRequest> createDRBehavior = (p, a, r) -> new DatapathRequest()
            .setPath(p)
            .setId(r.getId())
            .setAction(a)
            .setAbstractNBRequest(r);
    private TriFunction<String, ActionType, AbstractNBRequest, DatapathRequest> deleteDRBehavior = (i, a, r) -> new DatapathRequest()
            .setId(i)
            .setAction(a);
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

    public AbstractNBRequest handleRequest(NBInput input) throws AkkagenException {

        // Validate the input and send it to datapath
        AbstractNBRequest req = input.getBody();
        ActionType actionType = input.getAction();
        String id = input.getQueryParams().get("id");
        switch (actionType) {
            case CREATE:
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                store(validateAndGetNBRequest(req), r -> storage.put(r.getId(), r));
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case UPDATE:
                if(req == null){
                    throw new AkkagenException("The request object is empty", AkkagenExceptionType.BAD_REQUEST);
                }
                store(validateAndGetNBRequest(req), r -> {
                    if(storage.keySet().contains(r.getId())){
                        storage.replace(r.getId(), r);
                    }
                    else{
                        throw new AkkagenException("Update: Resource does not exist", AkkagenExceptionType.NOT_FOUND);
                    }
                });
                sendToDatapath(createDatapathRequest(input.getPath(), actionType, req, createDRBehavior));
                return req;
            case DELETE:
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                sendToDatapath(createDatapathRequest(id, actionType,null, deleteDRBehavior));
                if(storage.keySet().contains(id)){
                    storage.remove(id);
                }
                else{
                    throw new AkkagenException("Update: Resource does not exist", AkkagenExceptionType.NOT_FOUND);
                }

                return null;
            case GET:
                if(StringUtils.isBlank(id)){
                    throw new AkkagenException("The id is blank", AkkagenExceptionType.BAD_REQUEST);
                }
                if(storage.keySet().contains(id)){
                    return storage.get(id);
                }
                else{
                    throw new AkkagenException("Update: Resource does not exist", AkkagenExceptionType.NOT_FOUND);
                }
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
