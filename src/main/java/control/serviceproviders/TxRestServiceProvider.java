package control.serviceproviders;

import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import common.models.AbstractNBRequest;
import common.models.ActionType;
import common.models.OperationType;
import common.models.TxRestNBRequest;

import java.util.concurrent.ConcurrentHashMap;

public class TxRestServiceProvider implements ServiceProvider {

    private final String prefix = "/tx/rest";

    private ConcurrentHashMap<String, TxRestNBRequest> txRestRequestStore = new ConcurrentHashMap<>();

    public TxRestServiceProvider(){

    }

    @Override
    public String getPrefix(){
        return prefix;
    }

    @Override
    public OperationType getOperation() {
        return OperationType.TX;
    }

    @Override
    public Class getSupportedNBRequestClass() {
        return TxRestNBRequest.class;
    }

    @Override
    public void processNBRequest(String method, AbstractNBRequest req) throws AkkagenException{
        switch(getAction(method)){
            case CREATE:
                txRestRequestStore.putIfAbsent(req.getId(), (TxRestNBRequest)req);
                break;
            default:
                throw new AkkagenException("Unknown/UnSupported operation", AkkagenExceptionType.BAD_REQUEST);

        }
    }
}
