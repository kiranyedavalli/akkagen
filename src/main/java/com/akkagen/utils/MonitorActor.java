package com.akkagen.utils;

import akka.actor.*;
import com.akkagen.models.AkkagenAbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorActor extends AkkagenAbstractActor {

    private final Logger logger = LoggerFactory.getLogger(MonitorActor.class);
    private static Object MONITOR_KEY = "MonitorKey";
    private static final class MonitorTick {}
    private static final class FirstMonitorTick{}
    private final int monitorTimer = 5000; // every 5 seconds
    private int numActors = 0;
    private String currentId;

    public static Props props(ActorSystem system){
        return Props.create(MonitorActor.class, () -> new MonitorActor(system));
    }

    private MonitorActor(ActorSystem system){
        super(system);
        getTimers().startSingleTimer(MONITOR_KEY, new FirstMonitorTick(), Duration.ofMillis(1));
    }

    private void printThreadStatus(){
        logger.debug("\nTotal Threads: " + Thread.getAllStackTraces().keySet().size() +
                "\nRunnable Threads: " + Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.RUNNABLE).count() +
                "\nBlocked Threads: " + Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.BLOCKED).count() +
                "\nNew Threads: " + Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.NEW).count() +
                "\nTerminated Threads: " + Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.TERMINATED).count() +
                "\nWaiting Threads: " + Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.WAITING).count() +
                "\nTimed Waiting Threads: " + Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.TIMED_WAITING).count() +
                "\n");
    }

    private void printThreadNames(){
        logger.debug("\nRunnable Threads Names: ");
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.RUNNABLE).forEach(t -> logger.debug(t.getName()));
        logger.debug("\nBlocked Threads: ");
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.BLOCKED).forEach(t -> logger.debug(t.getName()));
        logger.debug("\nNew Threads: ");
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.NEW).forEach(t -> logger.debug(t.getName()));
        logger.debug("\nTerminated Threads: " );
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.TERMINATED).forEach(t -> logger.debug(t.getName()));
        logger.debug("\nWaiting Threads: ");
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.WAITING).forEach(t -> logger.debug(t.getName()));
        logger.debug("\nTimed Waiting Threads: ");
        Thread.getAllStackTraces().keySet().stream().filter(t -> t.getState() == Thread.State.TIMED_WAITING).forEach(t -> logger.debug(t.getName()));
    }

    private void printActorStatus(){
        logger.debug("\nTotal Number of Actors: " + numActors);
    }

    private void monitor(MonitorTick t){
        printThreadStatus();
        printThreadNames();
        printActorStatus();
        currentId = UUID.randomUUID().toString();
        numActors = 0;
        getActorSystem().actorSelection("user/*").tell(new AkkagenIdentify(currentId), getSelf());
        getActorSystem().actorSelection("user/*/*").tell(new AkkagenIdentify(currentId), getSelf());
    }

    private void firstMonitorTick(FirstMonitorTick t){
        getTimers().startPeriodicTimer(MONITOR_KEY, new MonitorTick(), Duration.ofMillis(monitorTimer));
        logger.debug("Periodic timer for " + getSelf() + "started for " + monitorTimer + " milli-seconds");
    }

    private void identifyAck(AkkagenIdentifyAck ack, ActorRef sender){
        if(ack.getId().equals(currentId)){
            numActors++;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FirstMonitorTick.class, this::firstMonitorTick)
                .match(MonitorTick.class, this::monitor)
                .match(AkkagenIdentifyAck.class, i -> identifyAck(i, getSender()))
                .build().orElse(super.createReceive());
    }
}
