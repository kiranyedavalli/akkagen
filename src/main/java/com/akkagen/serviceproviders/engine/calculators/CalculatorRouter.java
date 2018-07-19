package com.akkagen.serviceproviders.engine.calculators;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CalculatorRouter extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(CalculatorRouter.class);
    private Router calculatorRouter;
    private List<Routee> routees = new ArrayList<Routee>();

    private Consumer<Class> addRouteeBehavior = c -> {
        ActorRef r = getContext().getSystem().actorOf(Props.create(c));
        getContext().watch(r);
        routees.add(new ActorRefRoutee(r));
    };

    private void addRoutee(Class c, Consumer<Class> behavior) {
        behavior.accept(c);
        logger.debug("Added routee " + c.getCanonicalName());
    }

    public static Props props(){
        return Props.create(CalculatorRouter.class, () -> new CalculatorRouter());
    }

    private CalculatorRouter() {
        initialize();
    }

    private void initialize(){
        addRoutee(CountCalculator.class, addRouteeBehavior);
        addRoutee(DelayCalculator.class, addRouteeBehavior);
        addRoutee(ThroughputCalculator.class, addRouteeBehavior);
        calculatorRouter = new Router(new BroadcastRoutingLogic(), routees);
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(InstanceStartMessage.class, m -> calculatorRouter.route(m, getSender()))
                .match(InstanceEndMessage.class, m -> calculatorRouter.route(m, getSender()))
                .matchAny(o -> logger.error("Unknown Message Received!!!"))
                .build();
    }

    public static class InstanceStartMessage {}
    public static class InstanceEndMessage {}
}
