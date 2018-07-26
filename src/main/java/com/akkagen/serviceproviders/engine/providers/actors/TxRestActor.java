package com.akkagen.serviceproviders.engine.providers.actors;

import akka.actor.Props;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.models.TxRestEngineDefinition;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static com.akkagen.utils.utils.getJsonObjectFromJsonString;

public class TxRestActor extends EngineAbstractActor<TxRestEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(TxRestActor.class);
    private TxRestEngineDefinition def;
    private static Object TICK_KEY = "TickKey";
    private static final class Tick {}
    private static final class FirstTick{}
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static Props props(){
        return Props.create(TxRestActor.class, TxRestActor::new);
    }

    private TxRestActor(){}

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

    @Override
    protected void runEngine(TxRestEngineDefinition req) {
        this.def = req;
        OkHttpClient client = new OkHttpClient();
        Request request = createRequest(req);

        logger.debug("Calling REST Out to " + request.url().toString());
        client.newCall(request).enqueue(new Callback() {
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
                }
            }
        });

        // Start the timer if it exists
        logger.debug("In TxRestActor " + getSelf() + ":: " + req.toString() + "with req: " + req.getPrintOut());
        if(req.getPeriodicity() > 0) {
            getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(req.getPeriodicity()));
        }
    }

    private void firstTick(FirstTick t){
        if (def.getPeriodicity() > 0) {
            getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofMillis(def.getPeriodicity()));
            logger.debug("Periodic timer for " + getSelf() + "started for id: "
                    + def.getId() + " of " + def.getPeriodicity()
                    + " milli-seconds");
        }
    }

    private void periodicService(Tick t){
        runEngine(getEngineDefinition());
    }

    @Override
    public Receive createReceive() {
        return super.createReceive().orElse(receiveBuilder()
                .match(FirstTick.class, this::firstTick)
                .match(Tick.class, this::periodicService)
                .build());
    }
}
