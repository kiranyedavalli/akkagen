package com.akkagen.serviceproviders.engine.providers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.TxRestEngineDefinition;
import com.akkagen.serviceproviders.engine.providers.actors.TxRestActor;
import com.akkagen.serviceproviders.engine.providers.messages.StartMessage;
import com.akkagen.serviceproviders.engine.providers.messages.StopMessage;
import com.akkagen.serviceproviders.engine.providers.messages.UpdateMessage;
import com.akkagen.utils.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

public class TxRestEngineProvider extends AbstractEngineProvider<TxRestEngineDefinition> {

    private Logger logger = LoggerFactory.getLogger(TxRestEngineProvider.class);
    private ConcurrentHashMap<String, ArrayList<ActorRef>> actorMap = new ConcurrentHashMap<>();

    public static Props props(ActorSystem system, String path){
        return Props.create(TxRestEngineProvider.class, () -> new TxRestEngineProvider(system, path));
    }

    private TxRestEngineProvider(ActorSystem system, String path){
        super(system, path);
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

    private void runActors(TxRestEngineDefinition req, String id, ArrayList<ActorRef> actorList,
                           TriConsumer<TxRestEngineDefinition, String, ArrayList<ActorRef>> runBehavior){
        logger.debug("Sending Message to Actors for id: " + id);
        runBehavior.accept(req, id, actorList);
    }

    private TriConsumer<TxRestEngineDefinition, String, ArrayList<ActorRef>> startBehavior =
            (r, i, l) -> l.forEach(actor -> actor.tell(new StartMessage<TxRestEngineDefinition>(r), ActorRef.noSender()));

    private TriConsumer<TxRestEngineDefinition, String, ArrayList<ActorRef>> updateBehavior =
            (r, i, l) -> l.forEach(actor -> actor.tell(new UpdateMessage<TxRestEngineDefinition>(r), ActorRef.noSender()));

    private TriConsumer<TxRestEngineDefinition, String, ArrayList<ActorRef>> stopBehavior =
            (r, i, l) -> l.forEach(actor -> actor.tell(new StopMessage(i), ActorRef.noSender()));

    private int instances = 0;

    public void createEngine(TxRestEngineDefinition req) {
        ArrayList<ActorRef> actorList = new ArrayList<>();
        instances = req.getInstances();
        IntStream.range(0,instances).forEach(i -> {
            ActorRef actor = getContext().actorOf(TxRestActor.props(getActorSystem()), getPath().replace("/", "-") + "-" + i);
            actorList.add(actor);
        });
        addActorList(req.getId(), actorList);
        runActors(req, req.getId(), getActorList(req.getId()), startBehavior);
        logger.debug("Created Engines for id: " + req.getId());
    }

    public void updateEngine(TxRestEngineDefinition req) {
        int diff = abs(instances - req.getInstances());
        if(instances < req.getInstances()){
            logger.debug("Adding " + diff + " number of new actors");
            ArrayList<ActorRef> newActorList = new ArrayList<>();
            IntStream.range(0, diff).forEach(i -> {
                ActorRef actor = getContext().actorOf(TxRestActor.props(getActorSystem()),
                        getPath().replace("/", "-") + "-" + instances + i);
                newActorList.add(actor);
                getActorList(req.getId()).add(actor);
            });
            runActors(req, req.getId(), newActorList, startBehavior);
        }else if((instances != 0) && instances > req.getInstances()){
            logger.debug("Removing " + diff + " number of actors");
            ArrayList<ActorRef> killActorList = new ArrayList<>();
            // Start removing from the end of the list for efficiency
            ArrayList<ActorRef> list = getActorList(req.getId());
            IntStream.range(0, diff).forEach(i -> {
                killActorList.add(list.get(list.size()-1));
                list.remove(list.size()-1);
            });
            runActors(req, req.getId(), killActorList, stopBehavior);
        }
        runActors(req, req.getId(), getActorList(req.getId()), updateBehavior);
        logger.debug("Updated Engines for id: " + req.getId());
    }

    public void deleteEngine(TxRestEngineDefinition req) {
        runActors(req, req.getId(), getActorList(req.getId()), stopBehavior);
        deleteActorList(req.getId());
        logger.debug("Deleted Engines for id: " + req.getId());
    }

}
