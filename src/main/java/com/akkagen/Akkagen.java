package com.akkagen;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.akkagen.models.PathConstants;
import com.akkagen.serviceproviders.engine.EngineStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class Akkagen {

    private static volatile Akkagen akkagen;
    private final Logger logger = LoggerFactory.getLogger(Akkagen.class);
    private ActorSystem system;
    private ActorRef runtimeService;
    private ServiceProviderFactory spFactory;

    private Akkagen() {

    }

    public void initialize(){
        this.system = ActorSystem.create("akkagen");
        this.runtimeService = system.actorOf(EngineStarter.props(), "runtime-service");
        this.spFactory = new ServiceProviderFactory();
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

    public ActorRef getRuntimeService() {
        return this.runtimeService;
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
        int port = 9000;
        // The following order is important
        Akkagen.getInstance().initialize();
        Akkagen.getInstance().getServiceProviderFactory().initializeRestServer(PathConstants.__BASE_PATH, port);
        Akkagen.getInstance().getLogger().debug("service.Akkagen Started");
    }
}
