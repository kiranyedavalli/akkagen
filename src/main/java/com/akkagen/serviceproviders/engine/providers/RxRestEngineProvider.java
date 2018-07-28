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

    public static Props props(ActorSystem system, String path){
        return Props.create(RxRestEngineProvider.class, () -> new RxRestEngineProvider(system, path));
    }

    private RxRestEngineProvider(ActorSystem system, String path){
        super(system, path);
    }

    @Override
    public void createEngine(RxRestEngineDefinition def) {
        rxRestEngineServer = new RxRestEngineServer(getActorSystem(), "localhost", def.getPort(),
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
