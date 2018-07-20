package com.akkagen.serviceproviders.management.services;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;

import static com.akkagen.utils.utils.getObjectFromJsonString;

public class TxRestService extends ManagementServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(TxRestService.class);

    public TxRestService(){
        Akkagen.getInstance().getServiceProviderFactory().getManagementRestServer().addServiceProvider(PathConstants.__TX_REST, this);
    }

    @Override
    public String getPath() {
        return PathConstants.__TX_REST;
    }

    @Override
    protected AbstractEngineDefinition validateAndGetEngineDefinition(AbstractEngineDefinition req)
            throws AkkagenException{
        return req;
    }

    @Override
    public Route handleRestCall(String method, String body, HttpRequest request){
        TxRestEngineDefinition req = (TxRestEngineDefinition) getObjectFromJsonString(body, TxRestEngineDefinition.class);
        return handleRequest(method, req, request);
    }
}
