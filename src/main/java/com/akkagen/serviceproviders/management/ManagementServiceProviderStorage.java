package com.akkagen.serviceproviders.management;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ManagementServiceProviderStorage<T extends AbstractEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(ManagementServiceProviderStorage.class);
    private ConcurrentHashMap<String, T> storage = new ConcurrentHashMap<>();

    public ManagementServiceProviderStorage(){

    }

    public T createEngineDefition(T req){
        //TODO: Add validations
        storage.put(req.getId(), req);
        logger.debug("Created Engine Definition: " + req.getPrintOut());
        return req;
    }

    public T updateEngineDefinition(T req) throws AkkagenException {
        //TODO: Add validations
        if(storage.keySet().contains(req.getId())) {
            storage.replace(req.getId(), req);
            logger.debug("updated Engine Definition: " + req.getPrintOut());
            return req;
        }
        else{
            throw new AkkagenException("Nothing to update, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

    public void deleteEngineDefinitionById(String id) throws AkkagenException {
        //TODO: Add validations
        if(storage.keySet().contains(id)) {
            logger.debug("removing Engine Definition for id: " + id);
            storage.remove(id);
        }
        else{
            throw new AkkagenException("Nothing to delete, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

    public T getEngineDefinitionById(String id) throws AkkagenException {
        //TODO: Add validations
        if(storage.keySet().contains(id)) {
            logger.debug("returning Engine Definition: " + storage.get(id));
            return storage.get(id);
        }
        else{
            throw new AkkagenException("Nothing to get, object does not exist!!!", AkkagenExceptionType.NOT_FOUND);
        }
    }

}
