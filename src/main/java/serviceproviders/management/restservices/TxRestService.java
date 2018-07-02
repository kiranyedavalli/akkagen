package serviceproviders.management.restservices;

import common.exceptions.AkkagenException;
import common.models.ActionType;
import common.models.NBInput;
import common.models.PathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceproviders.ManagementServiceProvider;
import serviceproviders.management.models.TxRestNBRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.UUID;

@Path(PathConstants.__TX_REST)
public class TxRestService extends ManagementServiceProvider {

    private final Logger log = LoggerFactory.getLogger(TxRestService.class);

    public TxRestService(){}

    @Override
    public String getPath() {
        return PathConstants.__TX_REST;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTxRest(TxRestNBRequest req){
        log.debug("In POST call");
        req.setId(UUID.randomUUID().toString());
        return processRequest(ActionType.CREATE, req, getCreateUpdateBehavior());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTxRest(TxRestNBRequest req){
        log.debug("In PUT call");
        return processRequest(ActionType.UPDATE, req, getCreateUpdateBehavior());
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTxRest(@QueryParam("id") String id){
        log.debug("In DELETE call");
        try{
            handleRequest(new NBInput().setPath(getPath()).setAction(ActionType.DELETE).addToQueryParams("id", id));
            return Response.accepted().entity(id).build();
        }
        catch (AkkagenException e){
            return handleAkkagenException(e);
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTxRestById(@QueryParam("id") String id){
        System.out.println("In GET call");
        TxRestNBRequest res;
        try {
            res = (TxRestNBRequest) handleRequest(new NBInput().setPath(getPath())
                    .setAction(ActionType.GET).addToQueryParams("id", id));
            return Response.ok().entity(res).build();
        }
        catch (AkkagenException e){
            return handleAkkagenException(e);
        }
    }
}
