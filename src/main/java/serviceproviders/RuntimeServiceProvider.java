package serviceproviders;

import akka.actor.ActorRef;
import akka.parboiled2.RuleTrace;
import common.models.DatapathRequest;
import serviceproviders.ServiceProvider;

import java.util.concurrent.ConcurrentHashMap;

public abstract class RuntimeServiceProvider implements ServiceProvider {

    private ConcurrentHashMap<String, DatapathRequest> storage = new ConcurrentHashMap<>();

    public void createRuntimeService(DatapathRequest dp){

    }

    public void updateRuntimeService(DatapathRequest dp) {

    }

    public void deleteRuntimService(String id) {

    }



}
