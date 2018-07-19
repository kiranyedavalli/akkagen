package com.akkagen.serviceproviders.management.services;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.*;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path(PathConstants.__RX_REST)
public class RxRestService extends ManagementServiceProvider {

    private final Logger logger = LoggerFactory.getLogger(RxRestService.class);

    public RxRestService(){}

    @Override
    public String getPath() {
        return PathConstants.__RX_REST;
    }

    @Override
    protected AbstractEngineDefinition validateAndGetEngineDefinition(AbstractEngineDefinition req)
            throws AkkagenException{
        return req;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRxRest(RxRestEngineDefinition req){
        logger.debug("Received POST call");
        req.setId(UUID.randomUUID().toString());
        return handlePostPutRequest(ActionType.CREATE, req, getCreatePostPutNBInputBehavior());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRxRest(RxRestEngineDefinition req){
        logger.debug("Received PUT call");
        return handlePostPutRequest(ActionType.UPDATE, req, getCreatePostPutNBInputBehavior());
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRxRest(@QueryParam("id") String id){
        logger.debug("Received DELETE call");
        return handleDeleteGetRequest(ActionType.DELETE, id, getCreateDeleteGetNBInputBehavior());
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRxRestById(@QueryParam("id") String id){
        logger.debug("Received GET call");
        return handleDeleteGetRequest(ActionType.GET, id, getCreateDeleteGetNBInputBehavior());
    }
}
