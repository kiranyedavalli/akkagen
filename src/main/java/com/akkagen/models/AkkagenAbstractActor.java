/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/8/18 1:21 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.models;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AkkagenAbstractActor extends AbstractActorWithTimers {

    private final Logger logger = LoggerFactory.getLogger(AkkagenAbstractActor.class);
    private ActorSystem system;

    public AkkagenAbstractActor(ActorSystem system){
        this.system = system;
    }

    protected ActorSystem getActorSystem(){
        return this.system;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AkkagenIdentify.class, i -> getSender().tell(new AkkagenIdentifyAck(i.getId()), getSelf()))
                .matchAny(o -> logger.debug("Unknown message: " + o.getClass().getCanonicalName()))
                .build();
    }

    public static abstract class baseIdentify{
        private String id;
        public baseIdentify(String id){this.id = id;}
        public String getId(){return this.id;}
    }
    public static class AkkagenIdentify extends baseIdentify{public AkkagenIdentify(String id){super(id);} }
    public static class AkkagenIdentifyAck extends baseIdentify{public AkkagenIdentifyAck(String id){super(id);} }
}
