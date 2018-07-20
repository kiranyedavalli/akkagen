package com.akkagen;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.PathConstants;
import com.akkagen.models.RxRestEngineDefinition;
import com.akkagen.serviceproviders.engine.providers.EngineRestServer;
import com.akkagen.serviceproviders.engine.providers.RxRestEngineProvider;
import com.akkagen.serviceproviders.engine.providers.TxRestEngineProvider;
import com.akkagen.serviceproviders.management.ManagementRestServer;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import com.akkagen.serviceproviders.management.ManagementServiceProviderStorage;
import com.akkagen.models.RestServer;
import com.akkagen.serviceproviders.engine.providers.AbstractEngineProvider;
import com.akkagen.serviceproviders.management.services.RxRestService;
import com.akkagen.serviceproviders.management.services.TxRestService;
import com.akkagen.serviceproviders.engine.providers.actors.TxRestActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private final Logger logger = LoggerFactory.getLogger(ServiceProviderFactory.class);
    private EngineRestServer engineRestServer = null;
    private ManagementRestServer mgmtRestServer = null;
    private ActorSystem system;
    private final String host = "localhost";

    // Mgmt Rest Server
    private final String mgmtBasepath = PathConstants.__BASE_PATH;
    private final int mgmtPort = 9000;
    private final String mgmtServicePackage = "com.akkagen.serviceproviders.management.services";

    // Engine Rest Server
    private final int enginePort = 33777;


    // Management Storage
    private ConcurrentHashMap<String, ManagementServiceProviderStorage> managementServiceProviderStorageMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ManagementServiceProvider> managementServiceProviderMap = new ConcurrentHashMap<>();

    // Runtime Storage
    private ConcurrentHashMap<String, ActorRef> engineProviderMap = new ConcurrentHashMap<>();

    public void initializeMgmtRestServer(){
        /*RestServer mgmtRestServer = new RestServer(mgmtBasepath, mgmtPort);
        mgmtRestServer.addProviderPackage(mgmtServicePackage);
        mgmtRestServer.start();*/
        mgmtRestServer = new ManagementRestServer(system, host, mgmtPort);
        logger.debug("Management Rest Server Started");
    }

    public ServiceProviderFactory(ActorSystem system){
        this.system = system;
    }

    // Management Service Providers - add a new Management service provide here
    public ManagementRestServer getManagementRestServer(){
        return mgmtRestServer;
    }

    public void initializeManagementServiceProviders(){
        addManagementServiceProvider(new TxRestService());
        addManagementServiceProvider(new RxRestService());
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

    public void initializeEngineProviders(){
        logger.debug("Created new engineRestServer");
        addEngineProvider(system.actorOf(TxRestEngineProvider.props(system, PathConstants.__TX_REST)), PathConstants.__TX_REST);
        addEngineProvider(system.actorOf(RxRestEngineProvider.props(system, PathConstants.__RX_REST)), PathConstants.__RX_REST);
        // Add more in future
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

    public EngineRestServer getEngineRestServer() {
        if(engineRestServer == null){
            engineRestServer = new EngineRestServer(system, host, enginePort);
        }
        return engineRestServer;
    }
}
