package infra;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import common.models.AbstractNBRequest;
import common.models.PathConstants;
import org.apache.log4j.BasicConfigurator;
import serviceproviders.ServiceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceproviders.management.restservices.TxRestService;

import java.util.concurrent.ConcurrentHashMap;


/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class Akkagen {

    private static volatile Akkagen akkagen;
    private final Logger log = LoggerFactory.getLogger(Akkagen.class);
    private ActorSystem system;
    private ActorRef runtimeService;
    private ServiceProviderFactory spFactory;

    public Akkagen() {

    }

    public void initialize(){
        this.system = ActorSystem.create("akkagen");
        this.runtimeService = system.actorOf(RuntimeService.props(system), "runtime-service");
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

    public Logger getLog() {
        return this.log;
    }

    public static void main(String[] args) {
        int port = 9000;
        Akkagen.getInstance().initialize();
        Akkagen.getInstance().getServiceProviderFactory().initializeRestServer(PathConstants.__BASE_PATH, port);
        Akkagen.getInstance().getLog().debug("service.Akkagen Started");
    }
}
