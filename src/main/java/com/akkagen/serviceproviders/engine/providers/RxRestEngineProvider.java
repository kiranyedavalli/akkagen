package com.akkagen.serviceproviders.engine.providers;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.Akkagen;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.RxRestEngineDefinition;

import java.util.concurrent.ConcurrentHashMap;

public class RxRestEngineProvider extends AbstractEngineProvider {

    private EngineRestServer engineRestServer;

    public static Props props(ActorSystem system, String path){
        return Props.create(RxRestEngineProvider.class, () -> new RxRestEngineProvider(system, path));
    }

    private RxRestEngineProvider(ActorSystem system, String path){
        super(system, path);
        engineRestServer = Akkagen.getInstance().getServiceProviderFactory().getEngineRestServer();
    }

    @Override
    public void createEngine(AbstractEngineDefinition def) {
        engineRestServer.addRxRestEngine((RxRestEngineDefinition)def);
    }

    @Override
    public void updateEngine(AbstractEngineDefinition def) {
        engineRestServer.updateRxRestEngine((RxRestEngineDefinition)def);
    }

    @Override
    public void deleteEngine(AbstractEngineDefinition def) {
        engineRestServer.deleteRxRestEngine((RxRestEngineDefinition)def);
    }
}
