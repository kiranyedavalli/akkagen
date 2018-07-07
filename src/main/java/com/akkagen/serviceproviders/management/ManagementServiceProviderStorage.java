package com.akkagen.serviceproviders.management;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;

import java.util.concurrent.ConcurrentHashMap;

public class ManagementServiceProviderStorage {

    //TODO: Logger
    private ConcurrentHashMap<String, AbstractEngineDefinition> storage = new ConcurrentHashMap<>();

    public ManagementServiceProviderStorage(){

    }

    public AbstractEngineDefinition createNBRequest(AbstractEngineDefinition req){
        storage.put(req.getId(), req);
        return req;
    }

    public AbstractEngineDefinition updateNBRequest(AbstractEngineDefinition req) throws AkkagenException {
        if(storage.keySet().contains(req.getId())) {
            storage.replace(req.getId(), req);
            return req;
        }
        else{
            throw new AkkagenException("Nothing to update, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

    public void deleteNBRequestById(String id) throws AkkagenException {
        if(storage.keySet().contains(id)) {
            storage.remove(id);
        }
        else{
            throw new AkkagenException("Nothing to delete, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

    public AbstractEngineDefinition getNBRequestById(String id) throws AkkagenException {
        if(storage.keySet().contains(id)) {
            return storage.get(id);
        }
        else{
            throw new AkkagenException("Nothing to get, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

}
