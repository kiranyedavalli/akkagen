package com.akkagen.serviceproviders.management;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.akkagen.models.AbstractAkkaRestServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ManagementRestServer extends AbstractAkkaRestServer {

    private final Logger logger = LoggerFactory.getLogger(ManagementRestServer.class);
    private ConcurrentHashMap<String, ManagementServiceProvider> serviceProviders = new ConcurrentHashMap<>();

    public void addServiceProvider(String path, ManagementServiceProvider sp){
        serviceProviders.putIfAbsent(path, sp);
        logger.debug("Added path: " + path +  "\tsp: " + sp.toString());
    }

    private ManagementServiceProvider getManagementServiceProvider(String path){
        return serviceProviders.getOrDefault(path, null);
    }

    public ManagementRestServer(ActorSystem system, String host, int port){
        super(system, host, port);
    }

    @Override
    protected Route handleRestCall(String method, String body, HttpRequest request){
        String path = request.getUri().getPathString();
        ManagementServiceProvider sp = getManagementServiceProvider(path);
        if(sp == null) {
            logger.debug("No SP found for path: " + path);
            return complete(StatusCodes.NOT_FOUND, "Resource Not Found");
        }
        logger.debug("Found SP: " + sp.toString() + " for path: " + path);
        return sp.handleRestCall(method, body, request);
    }
}