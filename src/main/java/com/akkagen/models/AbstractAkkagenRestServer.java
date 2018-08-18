/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.models;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.*;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public abstract class AbstractAkkagenRestServer extends AllDirectives {

    private final Logger logger = LoggerFactory.getLogger(AbstractAkkagenRestServer.class);
    private ActorSystem system;
    private Http http;
    private ActorMaterializer materializer;

    public AbstractAkkagenRestServer(ActorSystem system, String host, int port, boolean useHttps){
        this.system = system;
        this.http = Http.get(system);
        this.materializer = ActorMaterializer.create(system);
        try {
            createRestServer(system, host, port, useHttps);
        }
        catch(ExecutionException | InterruptedException e){
            throw new AkkagenException("Not able to start REST server", AkkagenExceptionType.INTERAL_ERROR);
        }
    }

    protected ActorSystem getActorSystem(){
        return this.system;
    }

    private HttpsConnectionContext useHttps(ActorSystem system) {
        HttpsConnectionContext https = null;
        try {
            // initialise the keystore
            // !!! never put passwords into code !!!
            final char[] password = new char[]{'a', 'b', 'c', 'd', 'e', 'f'};

            final KeyStore ks = KeyStore.getInstance("PKCS12");
            final InputStream keystore = AbstractAkkagenRestServer.class.getClassLoader().getResourceAsStream("httpsKeys/keys/server.p12");
            if (keystore == null) {
                throw new RuntimeException("Keystore required!");
            }
            ks.load(keystore, password);

            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(ks, password);

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            https = ConnectionContext.https(sslContext);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            system.log().error("Exception while configuring HTTPS.", e);
        } catch (CertificateException | KeyStoreException | UnrecoverableKeyException | IOException e) {
            system.log().error("Exception while ", e);
        }

        return https;
    }

    private void createRestServer(ActorSystem system, String host, int port, boolean isHttps)
            throws ExecutionException, InterruptedException {

        if(isHttps){
            http.setDefaultServerHttpContext(useHttps(system));
        }

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(host, port),
                materializer);
        logger.debug("Started " + (isHttps ? "HTTPS" : "HTTP") + " Rest Server on " + host + ":" + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Shutting down Rest Server...");
            binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
        }));

    }

    private final Unmarshaller<HttpEntity, String> unmarshaller = akka.http.javadsl.unmarshalling.Unmarshaller.entityToString();

    protected Route createRoute() {
        return route(postRoute, putRoute, deleteRoute, getRoute);
    }

    private Route postRoute = post(() -> extractRequest(request -> entity(unmarshaller, json -> handleRestCall(HttpMethods.POST.value(),json, request))));

    private Route putRoute = put(() -> extractRequest(request -> entity(unmarshaller, json -> handleRestCall(HttpMethods.PUT.value(),json, request))));

    private Route deleteRoute = delete(() -> extractRequest((request)->handleRestCall(HttpMethods.DELETE.value(),null, request)));

    private Route getRoute = get(() -> extractRequest((request)->handleRestCall(HttpMethods.GET.value(),null, request)));

    protected Route handleRestCall(String method, String body, HttpRequest request){
        return complete(StatusCodes.NOT_FOUND,"Input not supported");
    }
}
