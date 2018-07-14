package com.akkagen.serviceproviders.engine;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.utils.TriConsumer;
import com.akkagen.serviceproviders.engine.engineactors.messages.RunMessage;
import com.akkagen.serviceproviders.engine.engineactors.messages.StopMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class EngineProvider {

    private Logger logger = LoggerFactory.getLogger(EngineProvider.class);
    private ConcurrentHashMap<String, ArrayList<ActorRef>> actorMap = new ConcurrentHashMap<>();
    private ActorSystem system;
    private Props props;
    private String path;

    public EngineProvider(ActorSystem system, Props props, String path){
        this.system = system;
        this.props = props;
        this.path = path;
    }

    private void addActorList(String id, ArrayList<ActorRef> list) {
        actorMap.put(id, list);
        logger.debug("Added ActorList for id: " + id);
    }
    private void deleteActorList(String id) throws AkkagenException {
        if(!actorMap.keySet().contains(id)){
            throw new AkkagenException("Nothing to delete for " + id, AkkagenExceptionType.NOT_FOUND);
        }
        actorMap.remove(id);
        logger.debug("Deleted ActorList for id: " + id);
    }
    private ArrayList<ActorRef> getActorList(String id) {
        if(!actorMap.keySet().contains(id)){
            throw new AkkagenException("Nothing to get for " + id, AkkagenExceptionType.NOT_FOUND);
        }
        return actorMap.get(id);
    }

    private void runActors(AbstractEngineDefinition req, String id, ArrayList<ActorRef> actorList,
                           TriConsumer<AbstractEngineDefinition, String, ArrayList<ActorRef>> runBehavior){
        logger.debug("Sending Message to Actors for id: " + id);
        runBehavior.accept(req, id, actorList);
    }

    private ActorSystem getSystem(){
        return this.system;
    }

    private Props getProps() {
        return this.props;
    }

    public String getPath() {
        return this.path;
    }

    public void createEngines(AbstractEngineDefinition req) {
        ArrayList<ActorRef> actorList = new ArrayList<>();
        IntStream.range(0,req.getInstances()).forEach(i -> {
            ActorRef actor = getSystem().actorOf(getProps(), getPath()+i);
            actorList.add(actor);
            addActorList(req.getId(), actorList);
        });
        runActors(req, req.getId(), getActorList(req.getId()),
                (r, i, l) -> l.forEach(actor -> actor.tell(new RunMessage(r), ActorRef.noSender())));
        logger.debug("Created Engines for id: " + req.getId());
    }

    public void updateEngines(AbstractEngineDefinition req) {
        runActors(req, req.getId(), getActorList(req.getId()),
                (r, i, l) -> l.forEach(actor -> actor.tell(new RunMessage(r), ActorRef.noSender())));
        logger.debug("Updated Engines for id: " + req.getId());
    }

    public void deleteEngines(String id) {
        runActors(null, id, getActorList(id),
                (r, i, l) -> l.forEach(actor -> actor.tell(new StopMessage(i), ActorRef.noSender())));
        deleteActorList(id);
        logger.debug("Deleted Engines for id: " + id);
    }
}