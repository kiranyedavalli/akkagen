package control.serviceproviders;

import com.google.gson.Gson;
import common.exceptions.AkkagenException;
import common.models.ActionType;
import common.models.OperationType;
import common.models.AbstractNBRequest;

import java.util.UUID;

public interface ServiceProvider {

    String getPrefix();

    default ActionType getAction(String method){
        for(ActionType val: ActionType.values()){
            if(method.equals(val.name())){
                return val;
            }
        }
        return null;
    }

    OperationType getOperation();

    Class getSupportedNBRequestClass();

    default <T extends AbstractNBRequest> T getNBRequestFromJson(String json, Class<T> t){
        return new Gson().fromJson(json, t);

    }

    void processNBRequest(String method, AbstractNBRequest req) throws AkkagenException;
}
