package com.akkagen;


import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.PathConstants;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import com.akkagen.serviceproviders.management.ManagementServiceProviderStorage;
import com.akkagen.models.RestServer;
import com.akkagen.serviceproviders.engine.EngineProvider;
import com.akkagen.serviceproviders.management.services.TxRestService;
import com.akkagen.serviceproviders.engine.engineactors.TxRestActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private Logger logger = LoggerFactory.getLogger(ServiceProviderFactory.class);

    // Management Storage
    private ConcurrentHashMap<String, ManagementServiceProviderStorage> managementServiceProviderStorageMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ManagementServiceProvider> managementServiceProviderMap = new ConcurrentHashMap<>();

    // Runtime Storage
    private ConcurrentHashMap<String, EngineProvider> runtimeServiceProviderMap = new ConcurrentHashMap<>();

    public void initializeRestServer(String basePath, int port){
        RestServer mgmtRestServer = new RestServer(basePath, port);
        managementServiceProviderMap.keySet().forEach(sp -> mgmtRestServer.addServiceProvider(managementServiceProviderMap.get(sp)));
        mgmtRestServer.start();
        logger.debug("Management Rest Server Started");
    }

    public ServiceProviderFactory(){
        initializeManagementServiceProviders();
        initializeRuntimeServiceProviders();
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

    private void initializeRuntimeServiceProviders(){
        ActorSystem system = Akkagen.getInstance().getSystem();
        addRuntimeServiceProvider(new EngineProvider(system, TxRestActor.props(system), PathConstants.__TX_REST));
        // Add more in future
    }

    private void addRuntimeServiceProvider(EngineProvider sp) throws AkkagenException {
        if(runtimeServiceProviderMap.keySet().contains(sp.getPath())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPath() + " already exists!!!");
        }

        runtimeServiceProviderMap.put(sp.getPath(), sp);
        logger.debug("Added " + sp.toString() + " to the Runtime Service Provider Map");
    }

    public EngineProvider getRuntimeServiceProvider(String path){
        return runtimeServiceProviderMap.getOrDefault(path, null);
    }

}
