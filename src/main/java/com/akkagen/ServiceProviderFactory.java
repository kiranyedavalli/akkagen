package com.akkagen;


import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.PathConstants;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import com.akkagen.serviceproviders.management.ManagementServiceProviderStorage;
import com.akkagen.models.RestServer;
import com.akkagen.serviceproviders.runtime.RuntimeServiceProvider;
import com.akkagen.serviceproviders.management.services.TxRestService;
import com.akkagen.serviceproviders.runtime.actors.TxRestActor;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private ConcurrentHashMap<String, ManagementServiceProviderStorage> managementServiceProviderStorageMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ManagementServiceProvider> managementServiceProviderMap = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String, RuntimeServiceProvider> runtimeServiceProviders = new ConcurrentHashMap<>();

    private RestServer mgmtRestServer;

    public ServiceProviderFactory(){
        initializeManagementServiceProviders();
        initializeRuntimeServiceProviders();
    }

    public void initializeRestServer(String basePath, int port){
        mgmtRestServer = new RestServer(basePath, port);
        managementServiceProviderMap.keySet().forEach(sp -> mgmtRestServer.addServiceProvider(managementServiceProviderMap.get(sp)));
        mgmtRestServer.start();
    }

    // Management Service Providers - add a new Management service provide here

    private void initializeManagementServiceProviders(){
        addManagementServiceProvider(new TxRestService());
    }


    // utility functions for Management Service Providers
    private void addManagementServiceProvider(ManagementServiceProvider sp) throws AkkagenException {
        if(managementServiceProviderMap.keySet().contains(sp.getPath())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPath() + " already exists!!!");
        }

        managementServiceProviderMap.put(sp.getPath(), sp);
        managementServiceProviderStorageMap.put(sp.getPath(), new ManagementServiceProviderStorage());

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
        addRuntimeServiceProvider(new RuntimeServiceProvider(system, TxRestActor.props(system), PathConstants.__TX_REST));
    }

    private void addRuntimeServiceProvider(RuntimeServiceProvider sp) throws AkkagenException {
        if(runtimeServiceProviders.keySet().contains(sp.getPath())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPath() + " already exists!!!");
        }

        runtimeServiceProviders.put(sp.getPath(), sp);
    }

    public RuntimeServiceProvider getRuntimeServiceProvider(String path){
        return runtimeServiceProviders.getOrDefault(path, null);
    }

}
