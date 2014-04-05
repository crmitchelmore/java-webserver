package webserver.Controllers;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by cmitchelmore on 05/04/2014.
 */
public class Lines  extends JavaWebController {

    private String responseString;

    public Lines(String rootDirectory, HashMap<String, String> urlParams, HashMap<String, Object> postParams){
        super(rootDirectory, urlParams, postParams);
        //Do stuff!

        String name = (String)postParams.get("aname");
        String like = (String)postParams.get("alike");
        String countString = (String)postParams.get("acount");
        int count = Integer.parseInt(countString);
        responseString = generateHTML(name, like, count);

    }
    private String generateHTML(String name, String like, int count)
    {
        StringBuilder html = new StringBuilder();
        html.append("<html>\n<head><title>" + name + " likes " + like + "</title></head>\n<body>\n");
        for ( int i = 0;i<count;i++){
             html.append(name + " likes " + like + "<br>\n");
        }
        html.append("</body>\n</html>");
        return html.toString();
    }


    @Override
    public byte[] responseBody() {
        return responseString.getBytes();
    }

    @Override
    public int responseCode() {
        return 200;//Not creating anything
    }
}
