package com.akkagen.serviceproviders.engine.providers.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.serviceproviders.engine.providers.messages.RunMessage;
import com.akkagen.serviceproviders.engine.providers.messages.StopMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.akkagen.models.TxRestEngineDefinition;

import java.time.Duration;

public class TxRestActor extends EngineAbstractActor<TxRestEngineDefinition> {

    private final Logger logger = LoggerFactory.getLogger(TxRestActor.class);
    private TxRestEngineDefinition def;
    private static Object TICK_KEY = "TickKey";
    private static final class Tick {}
    private static final class FirstTick{}

    public static Props props(){
        return Props.create(TxRestActor.class, TxRestActor::new);
    }

    private TxRestActor(){}

    @Override
    protected void runEngine(TxRestEngineDefinition req) {
        this.def = req;
        //TODO: do the rest client
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
