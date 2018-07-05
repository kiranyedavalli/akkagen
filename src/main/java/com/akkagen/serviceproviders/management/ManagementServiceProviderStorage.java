package com.akkagen.serviceproviders.management;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractNBRequest;

import java.util.concurrent.ConcurrentHashMap;

public class ManagementServiceProviderStorage {

    //TODO: Logger
    private ConcurrentHashMap<String, AbstractNBRequest> storage = new ConcurrentHashMap<>();

    public ManagementServiceProviderStorage(){

    }

    public AbstractNBRequest createNBRequest(AbstractNBRequest req){
        storage.put(req.getId(), req);
        return req;
    }

    public AbstractNBRequest updateNBRequest(AbstractNBRequest req) throws AkkagenException {
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

    public AbstractNBRequest getNBRequestById(String id) throws AkkagenException {
        if(storage.keySet().contains(id)) {
            return storage.get(id);
        }
        else{
            throw new AkkagenException("Nothing to get, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

}
