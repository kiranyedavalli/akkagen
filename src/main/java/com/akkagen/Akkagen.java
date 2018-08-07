/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.akkagen.models.*;
import com.akkagen.utils.MonitorActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;


/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class Akkagen {

    private static volatile Akkagen akkagen;
    private final Logger logger = LoggerFactory.getLogger(Akkagen.class);
    private ActorSystem system;
    private ServiceProviderFactory spFactory;
    private ActorRef monitor;

    private Akkagen() {

    }

    public void initialize(){
        this.system = ActorSystem.create("akkagen");
        this.spFactory = new ServiceProviderFactory(system);
        this.monitor = system.actorOf(MonitorActor.props(system), "monitor-actor");
    }

    public static Akkagen getInstance(){
        if(akkagen == null){
            synchronized (Akkagen.class){
                if(akkagen == null){
                    akkagen = new Akkagen();
                }
            }
        }
        return akkagen;
    }

    public ActorSystem getSystem() {
        return this.system;
    }

    public ServiceProviderFactory getServiceProviderFactory() {
        return this.spFactory;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public static void main(String[] args) {
        // The following order is important
        Akkagen.getInstance().initialize();

        // Management

        Akkagen.getInstance().getServiceProviderFactory().initializeMgmtRestServer();
        Akkagen.getInstance().getServiceProviderFactory().initializeMgmtServiceProvider(PathConstants.__TX_REST,
                TxRestEngineDefinition.class,
                TxRestEngineDefinition.inputDataValidator,
                TxRestEngineDefinition.methodValidator);
        Akkagen.getInstance().getServiceProviderFactory().initializeMgmtServiceProvider(PathConstants.__RX_REST,
                RxRestEngineDefinition.class,
                RxRestEngineDefinition.inputDataValidator,
                RxRestEngineDefinition.methodValidator);
        Akkagen.getInstance().getServiceProviderFactory().initializeMgmtServiceProvider(PathConstants.__RX_REST_STATS,
                RxRestEngineStatsDefinition.class,
                RxRestEngineStatsDefinition.inputDataValidator,
                RxRestEngineStatsDefinition.methodValidator);

        // Engines

        Akkagen.getInstance().getServiceProviderFactory().initializeEngineProviders();

        Akkagen.getInstance().getLogger().debug("***** Akkagen Started *****");
    }
}
