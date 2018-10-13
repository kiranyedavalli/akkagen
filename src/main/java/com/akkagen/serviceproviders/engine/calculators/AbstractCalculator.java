/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.serviceproviders.engine.calculators;

import akka.actor.ActorSystem;
import com.akkagen.models.AkkagenAbstractActor;

public class AbstractCalculator extends AkkagenAbstractActor {
    public AbstractCalculator(ActorSystem system) {
        super(system);
    }
}
