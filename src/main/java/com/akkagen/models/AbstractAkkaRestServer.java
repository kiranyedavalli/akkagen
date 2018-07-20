package com.akkagen.models;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public abstract class AbstractAkkaRestServer extends AllDirectives {

    private final Logger logger = LoggerFactory.getLogger(AbstractAkkaRestServer.class);

    public AbstractAkkaRestServer(ActorSystem system, String host, int port){
        try {
            createRestServer(system, host, port);
        }
        catch(ExecutionException | InterruptedException e){
            throw new AkkagenException("Not able to start REST server", AkkagenExceptionType.INTERAL_ERROR);
        }
    }

    private void createRestServer(ActorSystem system, String host, int port)
            throws ExecutionException, InterruptedException {

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
                                json -> handleRestCall("POST",json, request)))),
                put(() -> extractRequest(
                        request -> entity(akka.http.javadsl.unmarshalling.Unmarshaller.entityToString(),
                                json -> handleRestCall("PUT",json, request)))),
                delete(() -> extractRequest((request)->handleRestCall("DELETE",null, request))),
                get(() -> extractRequest((request)->handleRestCall("GET",null, request)))
        );
    }

    protected Route handleRestCall(String method, String body, HttpRequest request){
        return complete(StatusCodes.NOT_FOUND,"Input not supported");
    }

}
