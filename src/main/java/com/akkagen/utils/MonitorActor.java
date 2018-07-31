package com.akkagen.utils;

import akka.actor.*;
import com.akkagen.models.AkkagenAbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;

public class MonitorActor extends AkkagenAbstractActor {

    private final Logger logger = LoggerFactory.getLogger(MonitorActor.class);
    private static Object MONITOR_KEY = "MonitorKey";
    private static final class MonitorTick {}
    private static final class FirstMonitorTick{}
    private final int monitorTimer = 60; // seconds
    private int numActors = 0;
    private String currentId;
    private final long MEGABYTE = 1024L * 1024L;

    private long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

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
        logger.debug("\nTotal Number of Actors: " + numActors +
                "\n");
    }

    private void printResources(){
        Runtime runtime = Runtime.getRuntime();
        logger.debug("\nTotal Memory: " + bytesToMegabytes(runtime.totalMemory()) + "MB" +
                "\nFree Memory: " + bytesToMegabytes(runtime.freeMemory()) + "MB" +
                "\nMax Memory: " + bytesToMegabytes(runtime.maxMemory()) + "MB" +
                "\nAvailable Processors: " + runtime.availableProcessors() +
                "\n");
    }

    private void monitor(MonitorTick t){
        printThreadStatus();
        //printThreadNames();
        printActorStatus();
        printResources();
        currentId = UUID.randomUUID().toString();
        numActors = 0;
        AkkagenIdentify identify = new AkkagenIdentify(UUID.randomUUID().toString());
        getActorSystem().actorSelection("user/*").tell(identify, getSelf());
        getActorSystem().actorSelection("user/*/*").tell(identify, getSelf());
    }

    private void firstMonitorTick(FirstMonitorTick t){
        getTimers().startPeriodicTimer(MONITOR_KEY, new MonitorTick(), Duration.ofSeconds(monitorTimer));
        logger.debug("Periodic timer for " + getSelf() + "started for " + monitorTimer + " seconds");
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
