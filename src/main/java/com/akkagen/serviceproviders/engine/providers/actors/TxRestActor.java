/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/7/18 11:53 AM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.engine.providers.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.models.TxRestEngineDefinition;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.akkagen.utils.Utils.getJsonObjectFromJsonString;

public class TxRestActor extends EngineAbstractActor<TxRestEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(TxRestActor.class);
    private TxRestEngineDefinition def;
    private static Object TICK_KEY = "TickKey";
    private static final class Tick {}
    private static final class FirstTick{}
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient okHttpClient;
    private int timeout = 5; // Minutes

    public static Props props(ActorSystem system){
        return Props.create(TxRestActor.class, () -> new TxRestActor(system));
    }

    private TxRestActor(ActorSystem system){
        super(system);
    }

    private OkHttpClient.Builder getClientBuilder(){
        return new OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.MINUTES)
                .writeTimeout(timeout, TimeUnit.MINUTES)
                .connectTimeout(timeout, TimeUnit.MINUTES);
    }

    private void createHttpClient(TxRestEngineDefinition def){
        if(def.getUrl().startsWith("https")){
            logger.debug("Its https");
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                    throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                    throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                okHttpClient = getClientBuilder()
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(def.getUrl().startsWith("http")){
            logger.debug("Its http");
            okHttpClient = getClientBuilder().build();
        }
        logger.debug("Created " + (def.getUrl().startsWith("https") ? "HTTPS" : "HTTP") + " OkHttp Client: " + okHttpClient.toString());
    }

    private Request createRequest(TxRestEngineDefinition req){
        Request.Builder request = new Request.Builder();

        // build URL
        if(StringUtils.isBlank(req.getUrl())){
            throw new AkkagenException("URL is missing in the input", AkkagenExceptionType.BAD_REQUEST);
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(req.getUrl()).newBuilder();
        if(req.getQueryParams() != null){
            req.getQueryParams().forEach(urlBuilder::addQueryParameter);
        }
        request.url(urlBuilder.toString());

        // build headers
        if(req.getHeaders() != null){
            req.getHeaders().forEach(request::addHeader);
        }

        // build body
        JsonObject json = getJsonObjectFromJsonString(req.getBody());
        json.addProperty("instanceId",UUID.randomUUID().toString());
        RequestBody body = RequestBody.create(JSON, json.toString());
        switch(req.getMethod()){
            case POST:
                request.post(body);
                break;
            case PUT:
                request.put(body);
                break;
            case DELETE:
            case GET:
                // No body for these methods
                break;
            default:
                throw new AkkagenException("Unsupported Action", AkkagenExceptionType.BAD_REQUEST);
        }
        return request.build();
    }

    private void runEngine(TxRestEngineDefinition def){
        Request request = createRequest(def);
        logger.debug("Calling REST Out to " + request.url().toString());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    logger.debug("Response code: " + response.code());
                    response.body().close();
                }
            }
        });
    }

    private long periodicTimer = 0;

    @Override
    protected void startEngine(TxRestEngineDefinition def) {
        this.def = def;
        this.periodicTimer = def.getPeriodicity();
        createHttpClient(def);

        if(periodicTimer == 0){
            runEngine(def);
        }
        else {
            getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(1));
        }

        // Start the timer if it exists
        logger.debug("In TxRestActor " + getSelf() + ":: " + def.toString() + "with def: " + def.getPrintOut());
    }

    @Override
    protected void updateEngine(TxRestEngineDefinition def){
        // Update the definition
        this.def = def;

        // Update the timer if there is a change
        if(def.getPeriodicity() == 0) {
            periodicTimer = 0;
            getTimers().cancel(TICK_KEY);
            logger.debug("Cancelled timer for " + getSelf());
        }
        else if(periodicTimer != def.getPeriodicity()){
            periodicTimer = def.getPeriodicity();
            getTimers().cancel(TICK_KEY);
            getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(1));
        }
    }

    private void firstTick(FirstTick t){
        getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofMillis(periodicTimer));
        logger.debug("Periodic timer for " + getSelf() + "started for id: "
                + def.getId() + " of " + def.getPeriodicity()
                + " milli-seconds");
    }

    private void periodicService(Tick t){
        runEngine(getEngineDefinition());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FirstTick.class, this::firstTick)
                .match(Tick.class, this::periodicService)
                .build().orElse(super.createReceive());
    }
}
