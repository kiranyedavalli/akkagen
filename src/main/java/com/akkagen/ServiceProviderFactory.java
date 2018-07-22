package com.akkagen;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.PathConstants;
import com.akkagen.models.RxRestEngineDefinition;
import com.akkagen.models.TxRestEngineDefinition;
import com.akkagen.serviceproviders.engine.providers.RxRestEngineServer;
import com.akkagen.serviceproviders.engine.providers.RxRestEngineProvider;
import com.akkagen.serviceproviders.engine.providers.TxRestEngineProvider;
import com.akkagen.serviceproviders.management.ManagementRestServer;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import com.akkagen.serviceproviders.management.ManagementServiceProviderStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private final Logger logger = LoggerFactory.getLogger(ServiceProviderFactory.class);
    private ActorSystem system;
    private final String host = "localhost";

    public ServiceProviderFactory(ActorSystem system){
        this.system = system;
    }

    /*
     *    MANAGEMENT
     */
    private ManagementRestServer mgmtRestServer = null;
    private final int mgmtPort = 9000;

    public void initializeMgmtRestServer(){
        mgmtRestServer = new ManagementRestServer(system, host, mgmtPort);
        logger.debug("Management Rest Server Started");
    }

    public void initializeManagementServiceProviders(){
        new ManagementServiceProvider<TxRestEngineDefinition>(PathConstants.__TX_REST,
                TxRestEngineDefinition.class,
                r -> true);
        new ManagementServiceProvider<RxRestEngineDefinition>(PathConstants.__RX_REST,
                RxRestEngineDefinition.class,
                r -> true);
        // Declare new mgmt service providers here.
    }

    public ManagementRestServer getManagementRestServer(){
        return mgmtRestServer;
    }

    /*
     *   ENGINE
     */
    private ConcurrentHashMap<String, ActorRef> engineProviderMap = new ConcurrentHashMap<>();

    public void initializeEngineProviders(){
        addEngineProvider(system.actorOf(TxRestEngineProvider.props(system, PathConstants.__TX_REST)), PathConstants.__TX_REST);
        addEngineProvider(system.actorOf(RxRestEngineProvider.props(system, PathConstants.__RX_REST)), PathConstants.__RX_REST);
        // Add new engine providers here
    }

    private void addEngineProvider(ActorRef sp, String path) throws AkkagenException {
        if(engineProviderMap.keySet().contains(path)){
            throw new AkkagenException("The Engine provider with prefix " + path + " already exists!!!");
        }

        engineProviderMap.put(path, sp);
        logger.debug("Added " + sp.toString() + " to the Engine Provider Map");
    }

    public ActorRef getEngineProvider(String path){
        return engineProviderMap.getOrDefault(path, null);
    }
}
