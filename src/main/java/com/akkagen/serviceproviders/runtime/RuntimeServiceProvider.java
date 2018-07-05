package com.akkagen.serviceproviders.runtime;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.models.AbstractNBRequest;
import com.akkagen.utils.TriConsumer;
import com.akkagen.serviceproviders.runtime.actors.messages.RunMessage;
import com.akkagen.serviceproviders.runtime.actors.messages.StopMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeServiceProvider {

    //TODO: logging
    private ConcurrentHashMap<String, ArrayList<ActorRef>> actorMap = new ConcurrentHashMap<>();
    private ActorSystem system;
    private Props props;
    private String path;

    public RuntimeServiceProvider(ActorSystem system, Props props, String path){
        this.system = system;
        this.props = props;
        this.path = path;
    }

    private void addActorList(String id, ArrayList<ActorRef> list) {
        actorMap.put(id, list);
    }
    private void deleteActorList(String id) throws AkkagenException {
        if(!actorMap.keySet().contains(id)){
            throw new AkkagenException("Nothing to delete for " + id, AkkagenExceptionType.NOT_FOUND);
        }
        actorMap.remove(id);
    }
    private ArrayList<ActorRef> getActorList(String id) {
        if(!actorMap.keySet().contains(id)){
            throw new AkkagenException("Nothing to get for " + id, AkkagenExceptionType.NOT_FOUND);
        }
        return actorMap.get(id);
    }

    private void runActors(AbstractNBRequest req, String id, ArrayList<ActorRef> actorList,
                           TriConsumer<AbstractNBRequest, String, ArrayList<ActorRef>> runBehavior){
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

    public void createRuntimeService(AbstractNBRequest req) {
        ArrayList<ActorRef> actorList = new ArrayList<>();
        for(int i=0; i<req.getInstances(); i++ ){
            ActorRef actor = getSystem().actorOf(getProps(), getPath()+i);
            actorList.add(actor);
            addActorList(req.getId(), actorList);
        }
        runActors(req, req.getId(), getActorList(req.getId()), (r, i, l) -> l.forEach(actor -> actor.tell(new RunMessage(r), ActorRef.noSender())));
    }

    public void updateRuntimeService(AbstractNBRequest req) {
        runActors(req, req.getId(), getActorList(req.getId()), (r, i, l) -> l.forEach(actor -> actor.tell(new RunMessage(r), ActorRef.noSender())));
    }

    public void deleteRuntimService(String id) {
        runActors(null, id, getActorList(id), (r, i, l) -> l.forEach(actor -> actor.tell(new StopMessage(i), ActorRef.noSender())));
        deleteActorList(id);
    }
}
