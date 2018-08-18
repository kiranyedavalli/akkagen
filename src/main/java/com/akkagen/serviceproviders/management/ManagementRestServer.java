/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.management;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.akkagen.models.AbstractAkkagenRestServer;
import com.akkagen.models.ActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ManagementRestServer extends AbstractAkkagenRestServer {

    private final Logger logger = LoggerFactory.getLogger(ManagementRestServer.class);
    private ConcurrentHashMap<String, ManagementServiceProvider> serviceProviders = new ConcurrentHashMap<>();

    public void addServiceProvider(String path, ManagementServiceProvider sp){
        serviceProviders.putIfAbsent(path, sp);
        logger.debug("Added path: " + path +  "\tsp: " + sp.toString());
    }

    private ManagementServiceProvider getManagementServiceProvider(String path){
        return serviceProviders.getOrDefault(path, null);
    }

    public ManagementRestServer(ActorSystem system, String host, int port, boolean useHttps){
        super(system, host, port, useHttps);
    }

    @Override
    protected Route handleRestCall(String method, String body, HttpRequest request){
        String path = request.getUri().getPathString();
        logger.debug("Received path: " + path +"\nbody: " + body);
        ManagementServiceProvider sp = getManagementServiceProvider(path);
        if(sp == null) {
            logger.debug("No SP found for path: " + path);
            return complete(StatusCodes.NOT_FOUND, "Service provider for " + path + " Not Found");
        }
        logger.debug("Found SP: " + sp.toString() + " for path: " + path);
        return sp.handleRestCall(ActionType.getActionType(method), body, request);
    }
}
