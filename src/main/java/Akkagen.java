import control.ControlManager;
import datapath.DataPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class Akkagen {

    private final Logger log = LoggerFactory.getLogger(Akkagen.class);
    private final String hostname = "localhost";
    private final int port = 9000;
    private DataPathManager dataPathManager;
    private ControlManager controlManager;

    public Akkagen() {
    }

    public DataPathManager getDataPathManager() {
        return dataPathManager;
    }

    public void setDataPathManager(DataPathManager dataPathManager) {
        this.dataPathManager = dataPathManager;
    }

    public ControlManager getControlManager() {
        return controlManager;
    }

    public void setControlManager(ControlManager controlManager) {
        this.controlManager = controlManager;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {

        Akkagen akkaGen = new Akkagen();

        /* Control Path

        PHASE 1:

            - Provides the REST Service to take in TX requests
            - Stores these requests and serves them for viewing
            - Notifies the Datapath manager of these requests
            - Serves the request operational status information

        PHASE 2:

            - Provides the REST Service to take in RX requests
            - Stores these requests and serves them for viewing
            - Notifies the Datapath manager of these requests
            - Serves the request operational status information

        PHASE 3:

            - Given a mongoDB configuration persists the storage

         */

        akkaGen.setControlManager(new ControlManager(akkaGen.getHostname(), akkaGen.getPort()));


        /* Datapath

        PHASE 1:

            - Creates the actors for each of the TX requests
            - Stores these requests and serves them for viewing
            - Gathers operational stats about the TXs

        PHASE 2:

            - Creates the actors for each of the RX requests
            - Stores these requests and serves them for viewing
            - Gathers operational stats about the RXs

        PHASE 3:

            - Given a mongoDB configuration persists the storage

         */

        akkaGen.setDataPathManager(new DataPathManager());

        System.out.println("Akkagen Started");
    }
}
