package serviceproviders;

import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import common.models.ActionType;
import infra.Akkagen;
import serviceproviders.management.models.AbstractNBRequest;
import common.utils.utils;
import serviceproviders.ServiceProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ManagementServiceProvider extends ServiceProvider {

    private ConcurrentHashMap<String, AbstractNBRequest> storage = new ConcurrentHashMap<>();


    public ManagementServiceProvider(){
        super();
    }

    // Utility Functions

    private Class getSupportedNBRequestClass(){
        return this.getClass();
    }

    private AbstractNBRequest getNBRequestFromJson(String json) {
        return (AbstractNBRequest) utils.getObjectFromJson(json, getSupportedNBRequestClass());
    }

    // Storage functions

    private AbstractNBRequest addToStorage(AbstractNBRequest req) throws AkkagenException {
        //TODO: update storage
        return req;
    }

    private AbstractNBRequest updateToStorage(AbstractNBRequest req) throws AkkagenException {
        //TODO: update storage
        return req;
    }

    private void deleteFromStorage(String id) throws AkkagenException {
        //TODO: update storage
    }

    private AbstractNBRequest store(AbstractNBRequest req, Function<AbstractNBRequest, AbstractNBRequest> storeBehavior) throws AkkagenException {
        return storeBehavior.apply(req);
    }

    // Methods to be implemented by service providers

    protected AbstractNBRequest validateAndGetNBRequest(AbstractNBRequest req) throws AkkagenException{
        // If no implementation the req is valid
        return req;
    }

    // Public methods

    public AbstractNBRequest createNBRequest(String json) throws AkkagenException{
        return store(validateAndGetNBRequest(getNBRequestFromJson(json)), this::addToStorage);
    }

    public AbstractNBRequest updateNBRequest(String json) throws AkkagenException{
        return store(getNBRequestFromJson(json), this::updateToStorage);
    }

    public void deleteNBRequest(String id) throws AkkagenException{
        deleteFromStorage(id);
    }
}
