package serviceproviders;


import common.exceptions.AkkagenException;
import common.models.AbstractNBRequest;
import serviceproviders.management.restservices.TxRestService;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private ConcurrentHashMap<String, ManagementServiceProvider> managementServiceProviders = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ManagementServiceProviderStorage> managementServiceProviderStorageMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, RuntimeServiceProvider> runtimeServiceProviders = new ConcurrentHashMap<>();

    private RestServer mgmtRestServer;

    public ServiceProviderFactory(){
        initializeManagementServiceProviders();
    }

    public void initializeRestServer(String basePath, int port){
        mgmtRestServer = new RestServer(basePath, port);
        managementServiceProviders.keySet().stream().forEach(sp -> mgmtRestServer.addServiceProvider(managementServiceProviders.get(sp)));
        mgmtRestServer.start();
    }

    // Management Service Providers

    private void initializeManagementServiceProviders(){
        addManagementServiceProvider(new TxRestService());
    }

    private void addManagementServiceProvider(ManagementServiceProvider sp) throws AkkagenException {
        if(managementServiceProviders.keySet().contains(sp.getPath())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPath() + " already exists!!!");
        }

        managementServiceProviders.put(sp.getPath(), sp);
        managementServiceProviderStorageMap.put(sp.getPath(), new ManagementServiceProviderStorage());

    }

    public ManagementServiceProvider getManagementServiceProvider(String path){
        return managementServiceProviders.getOrDefault(path, null);
    }

    public ManagementServiceProviderStorage getManagementServiceProviderStorage(String path){
        return managementServiceProviderStorageMap.getOrDefault(path, null);
    }

    // Runtime Service Providers

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
