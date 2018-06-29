package serviceproviders.management.restservices;

import common.exceptions.AkkagenException;
import common.models.AbstractNBRequest;
import common.models.ActionType;
import common.models.NBInput;
import common.models.PathConstants;
import serviceproviders.ManagementServiceProvider;
import serviceproviders.management.models.TxRestNBRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Path(PathConstants.__TX__REST)
public class TxRestService extends ManagementServiceProvider {

    private BiFunction<ActionType, AbstractNBRequest, Response> cuBehavior = (t, r) -> {
        try {
            handleRequest(new NBInput().setPath(getPath()).setAction(t).setBody(r));
            return Response.accepted().build();
        } catch (AkkagenException e) {
            return handleAkkagenException(e);
        }
    };

    public TxRestService(){

    }

    @Override
    public String getPath() {
        return PathConstants.__TX__REST;
    }

    private Response handleAkkagenException(AkkagenException e){
        return Response.status(Response.Status.BAD_REQUEST).build();
        //TODO
    }

    private Response processRequest(ActionType type, AbstractNBRequest req,
                                    BiFunction<ActionType, AbstractNBRequest, Response> behavior) {
        return behavior.apply(type, req);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTxRest(TxRestNBRequest req){
        req.setId(UUID.randomUUID().toString());
        return processRequest(ActionType.CREATE, req, cuBehavior);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTxRest(TxRestNBRequest req){
        return processRequest(ActionType.UPDATE, req, cuBehavior);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTxRest(@QueryParam("id") String id){
        HashMap<String, String> qp = new HashMap<>();
        qp.put("id", id);
        try{
            handleRequest(new NBInput().setPath(getPath()).setAction(ActionType.DELETE).setQueryParams(qp));
            return Response.accepted().build();
        }
        catch (AkkagenException e){
            return handleAkkagenException(e);
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTxRestById(@QueryParam("id") String id){
        HashMap<String, String> qp = new HashMap<>();
        qp.put("id", id);
        TxRestNBRequest res;
        try {
            res = (TxRestNBRequest) handleRequest(new NBInput().setPath(getPath())
                    .setAction(ActionType.GET).setQueryParams(qp));
            return Response.ok().entity(res).build();
        }
        catch (AkkagenException e){
            return handleAkkagenException(e);
        }
    }
}
