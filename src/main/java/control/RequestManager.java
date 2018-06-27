package control;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import common.exceptions.AkkagenException;
import common.models.AbstractNBRequest;
import common.models.DatapathRequest;
import control.serviceproviders.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Akkagen;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class RequestManager extends AllDirectives {

    private final Logger logger = LoggerFactory.getLogger(RequestManager.class);
    private ActorSystem system;
    private String host;
    private int port;
    private ConcurrentHashMap<String, ServiceProvider> serviceProviders = new ConcurrentHashMap<>();

    public RequestManager(String hostname, int port, ActorSystem system){
        this.host = hostname;
        this.port = port;
        this.system = system;
        try{
            createRestServer();
        }
        catch(ExecutionException | InterruptedException e){
            throw new AkkagenException(e.getMessage());
        }
    }

    public void addServiceProvider(ServiceProvider sp) throws AkkagenException {
        if(serviceProviders.keySet().contains(sp.getPrefix())){
            throw new AkkagenException("The Service provider with prefix " + sp.getPrefix() + " already exists!!!");
        }

        serviceProviders.put(sp.getPrefix(), sp);
    }

    private ServiceProvider getServiceProvider(HttpRequest request){
        return serviceProviders.getOrDefault(request.getUri().getPathString(), null);
    }

    private void createRestServer()
            throws ExecutionException, InterruptedException {

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(this.host, this.port),
                materializer);
        logger.debug("Started Rest Server on host: " + host + " port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Shutting down Rest Server...");
            binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
        }));

    }

    private Route createRoute() {
        return route(
                post(() -> extractRequest(
                        request -> entity(akka.http.javadsl.unmarshalling.Unmarshaller.entityToString(),
                                json -> handleHttpRequest(HttpMethods.POST.value(),json, request)))),
                put(() -> extractRequest(
                        request -> entity(akka.http.javadsl.unmarshalling.Unmarshaller.entityToString(),
                                json -> handleHttpRequest(HttpMethods.PUT.value(),json, request)))),
                delete(() -> extractRequest((request)-> handleHttpRequest(HttpMethods.DELETE.value(),null,request))),
                get(() -> extractRequest((request)-> handleHttpRequest(HttpMethods.GET.value(),null,request)))
        );
    }

    private Route handleHttpRequest(String method, String body, HttpRequest request){
        ServiceProvider sp = getServiceProvider(request);
        AbstractNBRequest abstractNBRequest = null;
        if(sp == null){
            return complete(StatusCodes.NOT_FOUND, "Resource not found");
        }
        try {

            // Create request object
            AbstractNBRequest nbRequest = sp.getNBRequestFromJson(body, sp.getSupportedNBRequestClass());
            if(nbRequest.getId().isEmpty()){
                nbRequest.setId(UUID.randomUUID().toString());
            }

            //process Input
            sp.processNBRequest(method, nbRequest);

            // Send it to datapath manager
            DatapathRequest dpRequest = new DatapathRequest()
                    .setRequestId(nbRequest.getId())
                    .setAction(sp.getAction(method))
                    .setOperation(sp.getOperation());
            if(body != null) {
                dpRequest.setAbstractNBRequest(nbRequest);
            }
            Akkagen.getInstance().getDataPathManager().tell(dpRequest, ActorRef.noSender());
        }
        catch(AkkagenException e){
            switch(e.getType()){
                case INTERAL_ERROR:
                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
                default:
                    return complete(StatusCodes.BAD_REQUEST, e.getMessage());
            }
        }
        return complete(StatusCodes.ACCEPTED, "Request accepted");
    }
}
