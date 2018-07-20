package com.akkagen.serviceproviders.management.services;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.*;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.akkagen.utils.utils.getObjectFromJsonString;

public class RxRestService extends ManagementServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(RxRestService.class);

    public RxRestService(){
        Akkagen.getInstance().getServiceProviderFactory().getManagementRestServer().addServiceProvider(PathConstants.__RX_REST, this);
    }

    @Override
    public String getPath() {
        return PathConstants.__RX_REST;
    }

    @Override
    protected AbstractEngineDefinition validateAndGetEngineDefinition(AbstractEngineDefinition req)
            throws AkkagenException{
        return req;
    }

    @Override
    public Route handleRestCall(String method, String body, HttpRequest request){
        RxRestEngineDefinition req = (RxRestEngineDefinition) getObjectFromJsonString(body, RxRestEngineDefinition.class);
        return handleRequest(method, req, request);
    }
}
