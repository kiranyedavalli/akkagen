package serviceproviders.management;

import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import serviceproviders.ManagementServiceProvider;
import serviceproviders.management.models.AbstractNBRequest;
import common.models.ActionType;
import serviceproviders.management.models.TxRestNBRequest;

import java.util.concurrent.ConcurrentHashMap;

public class TxRestManagementServiceProvider extends ManagementServiceProvider {

    private final String path = "/tx/rest";

    public TxRestManagementServiceProvider(){
        super.setPath(this.path);
    }

    @Override
    public String getPath(){
        return path;
    }

    @Override
    protected AbstractNBRequest validateAndGetNBRequest(AbstractNBRequest req) throws AkkagenException {
        //TODO: Validate the request
        return req;
    }
}
