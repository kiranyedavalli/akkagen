package com.akkagen.models;

import com.akkagen.exceptions.AkkagenException;
import com.akkagen.exceptions.AkkagenExceptionType;
import com.akkagen.serviceproviders.management.ManagementServiceProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


public class RestServer {

    private final Logger logger = LoggerFactory.getLogger(RestServer.class);
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

    public void addProviderPackage(String servicePackage){
        serviceProviders.put("jersey.config.server.provider.packages", servicePackage);
        jerseyServlet.setInitParameters(serviceProviders);
    }

    public void addProviderClass(Class klass){
        serviceProviders.put("jersey.config.server.provider.classnames", klass.getCanonicalName());
        jerseyServlet.setInitParameters(serviceProviders);
    }

    public void start(){
        try {
            jettyServer.start();
            jettyServer.join();
            logger.debug("Rest Server started!!!");
        }
        catch(Exception e) {
            throw new AkkagenException(e.getMessage(), AkkagenExceptionType.INTERAL_ERROR);
        }
    }

    public void stop(){
        try {
            jettyServer.stop();
            jettyServer.destroy();
            logger.debug("Rest Server Stopped");
        }
        catch(Exception e){
            throw new AkkagenException(e.getMessage(), AkkagenExceptionType.INTERAL_ERROR);
        }
    }

    public String getPath(){
        return this.context.getContextPath();
    }
}

