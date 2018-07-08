package com.akkagen.serviceproviders.management.services;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.ActionType;
import com.akkagen.models.NBInput;
import com.akkagen.models.PathConstants;
import com.akkagen.models.TxRestEngineDefinition;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path(PathConstants.__TX_REST)
public class RxRestService extends ManagementServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(RxRestService.class);

    public RxRestService(){}

    @Override
    public String getPath() {
        return PathConstants.__RX_REST;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRxRest(TxRestEngineDefinition req){
        logger.debug("Received POST call");
        req.setId(UUID.randomUUID().toString());
        return processRequest(ActionType.CREATE, req, getCreateNBInputBehavior());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTxRest(TxRestEngineDefinition req){
        logger.debug("Received PUT call");
        return processRequest(ActionType.UPDATE, req, getCreateNBInputBehavior());
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTxRest(@QueryParam("id") String id){
        logger.debug("Received DELETE call");
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
        logger.debug("Received GET call");
        TxRestEngineDefinition res;
        try {
            res = (TxRestEngineDefinition) handleRequest(new NBInput().setPath(getPath())
                    .setAction(ActionType.GET).addToQueryParams("id", id));
            return Response.ok().entity(res).build();
        }
        catch (AkkagenException e){
            return handleAkkagenException(e);
        }
    }
}
