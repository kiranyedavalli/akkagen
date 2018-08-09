/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/8/18 12:16 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.engine.providers;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.Akkagen;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.EngineInput;
import com.akkagen.models.ProtocolConstants;
import com.akkagen.models.RxRestEngineDefinition;

public class RxRestEngineProvider extends AbstractEngineProvider<RxRestEngineDefinition> {

    private RxRestEngineServer rxRestEngineServer = null;
    private String host;

    public static Props props(ActorSystem system, String path, String host){
        return Props.create(RxRestEngineProvider.class, () -> new RxRestEngineProvider(system, path, host));
    }

    private RxRestEngineProvider(ActorSystem system, String path, String host){
        super(system, path);
        this.host = host;
    }

    @Override
    public void createEngine(RxRestEngineDefinition def) {
        rxRestEngineServer = new RxRestEngineServer(getActorSystem(), host, def.getPort(),
                (def.getProtocol().equals(ProtocolConstants._HTTPS)));
        rxRestEngineServer.addRxRestEngine(def);
    }

    @Override
    public void updateEngine(RxRestEngineDefinition def) {
        if(rxRestEngineServer != null) {
            rxRestEngineServer.updateRxRestEngine(def);
            return;
        }
        throw new AkkagenException("RxRestEngineServer does not exist!!!");
    }

    @Override
    public void deleteEngine(RxRestEngineDefinition def) {
        if(rxRestEngineServer != null) {
            rxRestEngineServer.deleteRxRestEngine(def);
            return;
        }
        throw new AkkagenException("RxRestEngineServer does not exist!!!");
    }
}
