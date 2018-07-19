package com.akkagen.serviceproviders.engine.providers;


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
import com.akkagen.models.RxRestEngineDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class EngineRestServer extends AllDirectives{

    private final Logger logger = LoggerFactory.getLogger(EngineRestServer.class);
    private ConcurrentHashMap<String, RxRestEngineDefinition> rxRestEngines = new ConcurrentHashMap<>();
    private String basepath = "rest-server";
    private int enginePort = 33777;
    private String host = "localhost";

    public void addRxRestEngine(RxRestEngineDefinition def){
        rxRestEngines.putIfAbsent(def.getUri(), def);
    }

    public void updateRxRestEngine(RxRestEngineDefinition def){
        if(rxRestEngines.keySet().contains(def.getUri())) {
            rxRestEngines.replace(def.getUri(), def);
            return;
        }
        throw new AkkagenException("Unknown rxRestEngine update!!!", AkkagenExceptionType.BAD_REQUEST);
    }

    public void deleteRxRestEngine(RxRestEngineDefinition def){
        if(rxRestEngines.keySet().contains(def.getUri())){
            rxRestEngines.remove(def.getUri());
            return;
        }
        throw new AkkagenException("Unknown rxRestEngine update!!!", AkkagenExceptionType.BAD_REQUEST);
    }

    private RxRestEngineDefinition getRxRestEngine(String uri){
        return rxRestEngines.getOrDefault(uri, null);
    }

    public EngineRestServer(ActorSystem system){
        try {
            createRestServer(system, host, enginePort);
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

    private Route handleRestCall(String method, String body, HttpRequest request){
        Map<String, String> headers = new HashMap<>();
        String uri = request.getUri().getPathString();
        request.getHeaders().forEach(h -> headers.put(h.name(), h.value()));
        RxRestEngineDefinition input = new RxRestEngineDefinition()
                .setUri(uri)
                .setMethod(method)
                .setRequestBody(body)
                .setHeaders(headers);
        RxRestEngineDefinition value = getRxRestEngine(uri);
        if(value != null && value.equals(input)){
            return complete(StatusCodes.OK, value.getResponseBody());
        }
        return complete(StatusCodes.NOT_FOUND,method + " Not Supported");
    }

}
