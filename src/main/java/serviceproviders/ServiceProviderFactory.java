package serviceproviders;


import common.exceptions.AkkagenException;
import serviceproviders.management.TxRestManagementServiceProvider;
import serviceproviders.runtime.TxRestRuntimeServiceProvider;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderFactory {

    private ConcurrentHashMap<String, ManagementServiceProvider> managementServiceProviders = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, RuntimeServiceProvider> runtimeServiceProviders = new ConcurrentHashMap<>();

    public ServiceProviderFactory(){

        // Management Service Providers
        addManagementServiceProvider(new TxRestManagementServiceProvider());

        // Runtime Service Providers
        addRuntimeServiceProvider(new TxRestRuntimeServiceProvider());

    }

    private void addManagementServiceProvider(ManagementServiceProvider sp) throws AkkagenException {
        if(managementServiceProviders.keySet().contains(sp.getPath())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPath() + " already exists!!!");
        }

        managementServiceProviders.put(sp.getPath(), sp);
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

    public ManagementServiceProvider getManagementServiceProvider(String path){
        return managementServiceProviders.getOrDefault(path, null);
    }
}
