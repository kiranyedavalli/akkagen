package common.models;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpMethods;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import common.exceptions.AkkagenException;
import infra.ManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

public abstract class AbstractRestServer extends AllDirectives {

    private final Logger logger = LoggerFactory.getLogger(ManagementService.class);

    public AbstractRestServer(ActorSystem system, String host, int port){
        createRestServer(system, host, port);
    }

    private void createRestServer(ActorSystem system, String host, int port) {
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(host, port),
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

        // Current Support is One Request Per Rest Call

        try {
            handleRequest(method, request.getUri().getPathString(), body);
        }
        catch(AkkagenException e){
            switch(e.getType()){
                case INTERAL_ERROR:
                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, e.getMessage());
                case NOT_FOUND:
                    return complete(StatusCodes.NOT_FOUND, e.getMessage());
                default:
                    return complete(StatusCodes.BAD_REQUEST, e.getMessage());
            }
        }
        return complete(StatusCodes.ACCEPTED, "Request accepted");
    }

    protected abstract void handleRequest(String method, String path, String body) throws AkkagenException;
}
