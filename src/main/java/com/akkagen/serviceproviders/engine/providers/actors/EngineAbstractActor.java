/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.engine.providers.actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorSystem;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.AkkagenAbstractActor;
import com.akkagen.serviceproviders.engine.calculators.CalculatorRouter;
import com.akkagen.serviceproviders.engine.providers.messages.StartMessage;
import com.akkagen.serviceproviders.engine.providers.messages.StopMessage;
import com.akkagen.serviceproviders.engine.providers.messages.UpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EngineAbstractActor<T extends AbstractEngineDefinition> extends AkkagenAbstractActor {

    private final Logger logger = LoggerFactory.getLogger(EngineAbstractActor.class);
    private T engineDefinition;
    private CalculatorRouter calculator;


    protected EngineAbstractActor(ActorSystem system){
        super(system);
    }

    // Has to be implemented by individual request Actors
    protected abstract void startEngine(T req);
    protected abstract void updateEngine(T req);

    protected T getEngineDefinition() {
        return engineDefinition;
    }

    protected void setEngineDefinition(T engineDefinition) {
        this.engineDefinition = engineDefinition;
    }

    private void stopService(String id) {
        if(!id.equals(this.engineDefinition.getId())){
            throw new AkkagenException("Wrong Actor is called for stopping service!", AkkagenExceptionType.INTERAL_ERROR);
        }
        logger.debug("Stopping Actor: " + getSelf());
        getContext().stop(getSelf());
    }

    private void startService(T req) {
        setEngineDefinition(req);
        startEngine(req);
        logger.debug("Engine " + getSelf() + " started for id: " + req.getId());
    }

    private void updateService(T req) {
        setEngineDefinition(req);
        updateEngine(req);
        logger.debug("Engine " + getSelf() + " updated for id: " + req.getId());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartMessage.class, m -> startService((T)m.getReq()))
                .match(UpdateMessage.class, m -> updateService((T)m.getReq()))
                .match(StopMessage.class, m -> stopService(m.getId()))
                .build().orElse(super.createReceive());
    }
}
