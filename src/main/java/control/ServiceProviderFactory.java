package control;


import control.serviceproviders.TxRestServiceProvider;

public class ServiceProviderFactory {

    private TxRestServiceProvider txRestServiceProvider = new TxRestServiceProvider();

    public ServiceProviderFactory(RequestManager rm){
        rm.addServiceProvider(txRestServiceProvider);
    }
}
