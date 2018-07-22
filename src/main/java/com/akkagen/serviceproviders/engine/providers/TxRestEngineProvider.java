package com.akkagen.serviceproviders.engine.providers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractEngineDefinition;
import com.akkagen.models.EngineInput;
import com.akkagen.serviceproviders.engine.providers.actors.TxRestActor;
import com.akkagen.serviceproviders.engine.providers.messages.RunMessage;
import com.akkagen.serviceproviders.engine.providers.messages.StopMessage;
import com.akkagen.utils.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class TxRestEngineProvider extends AbstractEngineProvider {

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

    private void runActors(AbstractEngineDefinition req, String id, ArrayList<ActorRef> actorList,
                           TriConsumer<AbstractEngineDefinition, String, ArrayList<ActorRef>> runBehavior){
        logger.debug("Sending Message to Actors for id: " + id);
        runBehavior.accept(req, id, actorList);
    }

    private TriConsumer<AbstractEngineDefinition, String, ArrayList<ActorRef>> runBehavior =
            (r, i, l) -> l.forEach(actor -> actor.tell(new RunMessage(r), ActorRef.noSender()));

    private TriConsumer<AbstractEngineDefinition, String, ArrayList<ActorRef>> stopBehavior =
            (r, i, l) -> l.forEach(actor -> actor.tell(new StopMessage(i), ActorRef.noSender()));


    public void createEngine(AbstractEngineDefinition req) {
        ArrayList<ActorRef> actorList = new ArrayList<>();
        IntStream.range(0,req.getInstances()).forEach(i -> {
            ActorRef actor = getSystem().actorOf(TxRestActor.props(), getPath().replace("/", "-") + "-" + i);
            actorList.add(actor);
        });
        addActorList(req.getId(), actorList);
        runActors(req, req.getId(), getActorList(req.getId()), runBehavior);
        logger.debug("Created Engines for id: " + req.getId());
    }

    public void updateEngine(AbstractEngineDefinition req) {
        runActors(req, req.getId(), getActorList(req.getId()), runBehavior);
        logger.debug("Updated Engines for id: " + req.getId());
    }

    public void deleteEngine(AbstractEngineDefinition req) {
        runActors(req, req.getId(), getActorList(req.getId()), stopBehavior);
        deleteActorList(req.getId());
        logger.debug("Deleted Engines for id: " + req.getId());
    }

}
