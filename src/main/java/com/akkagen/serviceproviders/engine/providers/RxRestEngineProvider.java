package com.akkagen.serviceproviders.engine.providers;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.Akkagen;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.EngineInput;
import com.akkagen.models.RxRestEngineDefinition;

public class RxRestEngineProvider extends AbstractEngineProvider {

    private RxRestEngineServer rxRestEngineServer;
    private final String host = "localhost";
    private final int port = 33777;

    public static Props props(ActorSystem system, String path){
        return Props.create(RxRestEngineProvider.class, () -> new RxRestEngineProvider(system, path));
    }

    private RxRestEngineProvider(ActorSystem system, String path){
        super(system, path);
        rxRestEngineServer = new RxRestEngineServer(system, host, port);
    }

    @Override
    public void createEngine(AbstractEngineDefinition def) {
        rxRestEngineServer.addRxRestEngine((RxRestEngineDefinition)def);
    }

    @Override
    public void updateEngine(AbstractEngineDefinition def) {
        rxRestEngineServer.updateRxRestEngine((RxRestEngineDefinition)def);
    }

    @Override
    public void deleteEngine(AbstractEngineDefinition def) {
        rxRestEngineServer.deleteRxRestEngine((RxRestEngineDefinition)def);
    }
}
