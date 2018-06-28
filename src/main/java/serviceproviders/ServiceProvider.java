package serviceproviders;

import java.util.concurrent.ConcurrentHashMap;

public abstract class ServiceProvider {

    private String path;

    public ServiceProvider(){
    }

    public String getPath(){
        return this.path;
    }

    public void setPath(String path){
        this.path = path;
    }

}
