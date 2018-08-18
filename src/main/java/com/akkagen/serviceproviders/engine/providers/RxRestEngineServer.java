/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.engine.providers;


import akka.actor.ActorSystem;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.Route;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractAkkagenRestServer;
import com.akkagen.models.ActionType;
import com.akkagen.models.RxRestEngineDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RxRestEngineServer extends AbstractAkkagenRestServer {

    private final Logger logger = LoggerFactory.getLogger(RxRestEngineServer.class);
    private ConcurrentHashMap<String, RxRestEngineDefinition> rxRestEngines = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> rxRestEnginesCount = new ConcurrentHashMap<>();

    public void addRxRestEngine(RxRestEngineDefinition def){
        rxRestEngines.put(def.getUri(), def);
        logger.debug("Added to RxRestEngines: " + def.getPrintOut());
    }

    public void updateRxRestEngine(RxRestEngineDefinition def){
        if(rxRestEngines.keySet().contains(def.getUri())) {
            rxRestEngines.replace(def.getUri(), def);
            return;
        }
        throw new AkkagenException("Unknown rxRestEngine update!!!", AkkagenExceptionType.BAD_REQUEST);
    }

    public void deleteRxRestEngine(RxRestEngineDefinition def){
        if(rxRestEngines.keySet().contains(def.getUri())){
            rxRestEngines.remove(def.getUri());
            return;
        }
        throw new AkkagenException("Unknown rxRestEngine update!!!", AkkagenExceptionType.BAD_REQUEST);
    }

    public Long getrxRestCount(String uri){
        return rxRestEnginesCount.getOrDefault(uri, null);
    }

    private RxRestEngineDefinition getRxRestEngine(String uri){
        RxRestEngineDefinition rex = rxRestEngines.getOrDefault(uri, null);
        if(rex != null) {logger.debug("Found: " + rex.getPrintOut() + " for uri: " + uri);}
        else {logger.debug("No RxRestEngine found for uri: " + uri);}
        return rex;
    }

    public RxRestEngineServer(ActorSystem system, String host, int port, boolean useHttps){
        super(system, host, port, useHttps);
    }

    @Override
    protected Route handleRestCall(String method, String body, HttpRequest request){
        Map<String, String> headers = new HashMap<>();
        String uri = request.getUri().getPathString().replaceFirst("/","");
        logger.debug("method: " + method);
        logger.debug("uri: " + uri);
        logger.debug("body: " + body);
        request.getHeaders().forEach(h -> {
            headers.put(h.name(), h.value());
            logger.debug("name: " + h.name() + "\tvalue: " + h.value());
        });
        RxRestEngineDefinition input = new RxRestEngineDefinition()
                .setUri(uri)
                .setMethod(method)
                .setRequestBody(body)
                .setHeaders(headers);
        RxRestEngineDefinition value = getRxRestEngine(uri);
        if(value != null && value.equals(input)){
            rxRestEnginesCount.compute(uri, (u,l) -> l == null ? 1 : l+1);
            logger.debug("Count for " + uri + " : " + rxRestEnginesCount.get(uri));
            logger.debug("Found a match: " + value.getPrintOut());
            if(ActionType.getActionType(method).equals(ActionType.POST) ||
                    ActionType.getActionType(method).equals(ActionType.PUT) ||
                    ActionType.getActionType(method).equals(ActionType.DELETE)) {
                return complete(StatusCodes.ACCEPTED, value.getResponseBody());
            }
            return complete(StatusCodes.OK, value.getResponseBody());
        }
        logger.debug("DID NOT FIND A MATCH FOR: "  + value.getPrintOut());
        return complete(StatusCodes.NOT_FOUND,"Input not supported");
    }
}
