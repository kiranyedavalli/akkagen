package com.akkagen;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.PathConstants;
import com.akkagen.models.RxRestEngineDefinition;
import com.akkagen.serviceproviders.engine.providers.EngineRestServer;
import com.akkagen.serviceproviders.engine.providers.RxRestEngineProvider;
import com.akkagen.serviceproviders.engine.providers.TxRestEngineProvider;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import com.akkagen.serviceproviders.management.ManagementServiceProviderStorage;
import com.akkagen.models.RestServer;
import com.akkagen.serviceproviders.engine.providers.AbstractEngineProvider;
import com.akkagen.serviceproviders.management.services.TxRestService;
import com.akkagen.serviceproviders.engine.providers.actors.TxRestActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private final Logger logger = LoggerFactory.getLogger(ServiceProviderFactory.class);
    private EngineRestServer engineRestServer;
    ActorSystem system = Akkagen.getInstance().getSystem();

    // Management Storage
    private ConcurrentHashMap<String, ManagementServiceProviderStorage> managementServiceProviderStorageMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ManagementServiceProvider> managementServiceProviderMap = new ConcurrentHashMap<>();

    // Runtime Storage
    private ConcurrentHashMap<String, ActorRef> engineProviderMap = new ConcurrentHashMap<>();

    public void initializeMgmtRestServer(String basePath, int port, String servicePackage){
        RestServer mgmtRestServer = new RestServer(basePath, port);
        mgmtRestServer.addProviderPackage(servicePackage);
        mgmtRestServer.start();
        logger.debug("Management Rest Server Started");
    }

    public ServiceProviderFactory(){
        initializeManagementServiceProviders();
        initializeEngineProviders();

        //TODO: Move this to the time when the RxRest server is defined
        initializeEngineRestServer();
    }

    private void initializeEngineRestServer(){
        engineRestServer = new EngineRestServer(system);
    }

    // Management Service Providers - add a new Management service provide here
    private void initializeManagementServiceProviders(){
        addManagementServiceProvider(new TxRestService());

        // Add more in future
    }

    // utility functions for Management Service Providers
    private void addManagementServiceProvider(ManagementServiceProvider sp) throws AkkagenException {
        if(managementServiceProviderMap.keySet().contains(sp.getPath())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPath() + " already exists!!!");
        }
        managementServiceProviderMap.put(sp.getPath(), sp);
        logger.debug("Added " + sp.toString() + " to the Mgmt Service Provider Map");
        managementServiceProviderStorageMap.put(sp.getPath(), new ManagementServiceProviderStorage());
        logger.debug("Added " + sp.toString() + " to the Mgmt Service Provider Storage");
    }

    public ManagementServiceProvider getManagementServiceProvider(String path){
        return managementServiceProviderMap.getOrDefault(path, null);
    }

    public ManagementServiceProviderStorage getManagementServiceProviderStorage(String path){
        return managementServiceProviderStorageMap.getOrDefault(path, null);
    }

    // Runtime Service Providers

    private void initializeEngineProviders(){
        addEngineProvider(system.actorOf(TxRestEngineProvider.props(system, PathConstants.__TX_REST)), PathConstants.__TX_REST);
        addEngineProvider(system.actorOf(RxRestEngineProvider.props(system, PathConstants.__RX_REST)), PathConstants.__RX_REST);
        // Add more in future
    }

    private void addEngineProvider(ActorRef sp, String path) throws AkkagenException {
        if(engineProviderMap.keySet().contains(path)){
            throw new AkkagenException("The Service provider with prefix " + path + " already exists!!!");
        }

        engineProviderMap.put(path, sp);
        logger.debug("Added " + sp.path() + " to the Runtime Service Provider Map");
    }

    public ActorRef getEngineProvider(String path){
        return engineProviderMap.getOrDefault(path, null);
    }

    public EngineRestServer getEngineRestServer() { return engineRestServer; }
}
