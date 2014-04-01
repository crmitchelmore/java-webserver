package webserver;

import java.util.HashMap;

/**
 * Created by cmitchelmore on 01/04/2014.
 */
public class Routes {

    public static JavaWebClass route(String rootDirectory, String uri, HashMap<String, String> urlParams, HashMap<String, Object> postParams)
    {
        //This would usually route to scripts but as we are using java we route to classes!
        if ( uri.equals("form.ja") ){
            return new BasicForm(rootDirectory,urlParams, postParams);
        }

        return null;
    }
}
