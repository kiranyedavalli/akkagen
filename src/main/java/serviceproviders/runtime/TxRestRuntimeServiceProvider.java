package serviceproviders.runtime;

import common.exceptions.AkkagenException;
import common.models.DatapathRequest;
import serviceproviders.RuntimeServiceProvider;
import serviceproviders.management.models.AbstractNBRequest;

public class TxRestRuntimeServiceProvider extends RuntimeServiceProvider {

    private final String path = "/tx/rest";

   public TxRestRuntimeServiceProvider(){
       super();
       setPath(path);
   }

   protected void validate(AbstractNBRequest req) throws AkkagenException{
       // If no implementation the req is valid
   }

}
