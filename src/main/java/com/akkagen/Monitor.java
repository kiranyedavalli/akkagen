/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/8/18 1:23 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen;

import akka.actor.*;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.models.AkkagenAbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

public class Monitor extends AkkagenAbstractActor {

    private final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private static Object MONITOR_KEY = "MonitorKey";
    private static final class MonitorTick {}
    private static final class FirstMonitorTick{}

    private int monitorTimer;
    private boolean showThreadDetails;
    private boolean showThreadNames;
    private boolean showResources;

    private int numActors = 0;
    private String currentId;

    private long bytesToMegabytes(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }

    public static Props props(ActorSystem system, Properties properties){
        return Props.create(Monitor.class, () -> new Monitor(system, properties));
    }

    private Monitor(ActorSystem system, Properties properties){
        super(system);
        monitorTimer = Integer.parseInt(properties.getProperty("monitorTimer"));
        showThreadDetails = Boolean.valueOf(properties.getProperty("showThreadDetails"));
        showThreadNames = Boolean.valueOf(properties.getProperty("showThreadNames"));
        showResources = Boolean.valueOf(properties.getProperty("showResources"));
        logger.debug("akkagen.properties:monitorTimer: {}, showThreadDetails: {}, showThreadNames: {}, showResources: {}",
                monitorTimer, showThreadDetails, showThreadNames, showResources);
        getTimers().startSingleTimer(MONITOR_KEY, new FirstMonitorTick(), Duration.ofMillis(1));
    }

    private void printThreadDetails(){
        if(!showThreadDetails) return;
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
        if(!showThreadNames) return;
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
        logger.debug("Total Number of Actors: " + numActors);
    }

    private void printResources(){
        if(!showResources) return;
        Runtime runtime = Runtime.getRuntime();
        logger.debug("\nTotal Memory: " + bytesToMegabytes(runtime.totalMemory()) + "MB" +
                "\nFree Memory: " + bytesToMegabytes(runtime.freeMemory()) + "MB" +
                "\nMax Memory: " + bytesToMegabytes(runtime.maxMemory()) + "MB" +
                "\nAvailable Processors: " + runtime.availableProcessors() +
                "\n");
    }

    private void monitor(MonitorTick t){
        printThreadDetails();
        printThreadNames();
        printActorStatus();
        printResources();
        currentId = UUID.randomUUID().toString();
        numActors = 0;
        AkkagenIdentify identify = new AkkagenIdentify(currentId);
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
