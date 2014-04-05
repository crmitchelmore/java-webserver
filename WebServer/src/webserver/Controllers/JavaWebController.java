package webserver.Controllers;

import java.util.HashMap;

/**
 * Created by cmitchelmore on 01/04/2014.
 */
public abstract class JavaWebController {

    protected HashMap<String, String> urlParams;
    protected HashMap<String, Object> postParams;

    public JavaWebController(String rootDirectory, HashMap<String, String> urlParams, HashMap<String, Object> postParams){
        this.urlParams = urlParams;
        this.postParams = postParams;

    }

    public abstract byte[] responseBody();
    public int responseCode()
    {
        return 201;//Override this if necessary
    }
}
