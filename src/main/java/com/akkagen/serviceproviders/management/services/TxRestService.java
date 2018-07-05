package com.akkagen.serviceproviders.management.services;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.ActionType;
import com.akkagen.models.NBInput;
import com.akkagen.models.PathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import com.akkagen.models.TxRestNBRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.UUID;

@Path(PathConstants.__TX_REST)
public class TxRestService extends ManagementServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(TxRestService.class);

    public TxRestService(){}

    @Override
    public String getPath() {
        return PathConstants.__TX_REST;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTxRest(TxRestNBRequest req){
        logger.debug("In POST call");
        req.setId(UUID.randomUUID().toString());
        return processRequest(ActionType.CREATE, req, getCreateUpdateBehavior());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTxRest(TxRestNBRequest req){
        logger.debug("In PUT call");
        return processRequest(ActionType.UPDATE, req, getCreateUpdateBehavior());
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTxRest(@QueryParam("id") String id){
        logger.debug("In DELETE call");
        try{
            handleRequest(new NBInput().setPath(getPath()).setAction(ActionType.DELETE).addToQueryParams("id", id));
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
        logger.debug("In GET call");
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
