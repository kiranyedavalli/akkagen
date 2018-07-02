package serviceproviders.runtime;

import common.exceptions.AkkagenException;
import common.models.PathConstants;
import serviceproviders.RuntimeServiceProvider;
import common.models.AbstractNBRequest;

public class TxRestRuntimeServiceProvider extends RuntimeServiceProvider {

    @Override
    public String getPath() {
        return PathConstants.__TX_REST;
    }

    public TxRestRuntimeServiceProvider(){
    }

}
