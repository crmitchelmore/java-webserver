package webserver;

import webserver.Controllers.JavaWebController;
import webserver.Controllers.Profile;
import webserver.Controllers.Lines;

import java.util.HashMap;

/**
 * Created by cmitchelmore on 01/04/2014.
 */
public class Routes {

    public static JavaWebController route(String rootDirectory, String uri, HashMap<String, String> urlParams, HashMap<String, Object> postParams)
    {
        //This would usually route to scripts but as we are using java we route to classes!
        //Also cheating slightly with ends with but I don't know where you will run the post_tests.html from!
        if ( uri.endsWith("multipartform.ja") ){
            return new Profile(rootDirectory,urlParams, postParams);
        }else if ( uri.endsWith("urlencodedform.ja") ){
            return new Lines(rootDirectory,urlParams, postParams);
        }

        return null;
    }
}
