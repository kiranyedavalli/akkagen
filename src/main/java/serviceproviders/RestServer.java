package serviceproviders;

import common.exceptions.AkkagenException;
import common.exceptions.AkkagenExceptionType;
import common.models.PathConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import serviceproviders.ManagementServiceProvider;
import serviceproviders.ServiceProvider;
import serviceproviders.management.restservices.TxRestService;

import java.util.HashMap;


public class RestServer {

    private HashMap<String, String> serviceProviders = new HashMap<>();
    private ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    private Server jettyServer;
    private ServletHolder jerseyServlet;

    public RestServer(String basepath, int port) {

        context.setContextPath(basepath);
        jettyServer = new Server(port);
        jettyServer.setHandler(context);
        jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
    }

    public void start(){
        try {
            jettyServer.start();
            jettyServer.join();
        }
        catch(Exception e) {
            throw new AkkagenException(e.getMessage(), AkkagenExceptionType.INTERAL_ERROR);
        }
    }

    public void stop(){
        try {
            jettyServer.stop();
            jettyServer.destroy();
        }
        catch(Exception e){
            throw new AkkagenException(e.getMessage(), AkkagenExceptionType.INTERAL_ERROR);
        }
    }

    public void addServiceProvider(ServiceProvider msp) {
        serviceProviders.put("jersey.config.server.provider.classnames", msp.getClass().getCanonicalName());
        jerseyServlet.setInitParameters(serviceProviders);
    }

    public String getPath(){
        return this.context.getContextPath();
    }
}

