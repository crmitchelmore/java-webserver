package webserver;

import java.util.HashMap;

/**
 * Created by cmitchelmore on 01/04/2014.
 */
public abstract class JavaWebClass {

    protected HashMap<String, String> urlParams;
    protected HashMap<String, Object> postParams;

    public JavaWebClass(String rootDirectory, HashMap<String, String> urlParams, HashMap<String, Object> postParams){
        this.urlParams = urlParams;
        this.postParams = postParams;

    }

    public abstract byte[] responseBody();
}
