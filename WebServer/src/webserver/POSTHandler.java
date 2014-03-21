package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by cmitchelmore on 19/03/2014.
 */
public class POSTHandler extends  RequestHandler{

    public POSTHandler(RequestMessageBody requestMessageBody, String rootDirectory)
    {
        super(requestMessageBody, rootDirectory);


    }

    @Override
    public int httpResponseCode() {
        return 200; //??
    }

    public HashMap<String, String> postParams()
    {

        return null;
    }

    @Override
    public byte[] responseBody() throws HTTPException {
        return new byte[0];
    }

    @Override
    public HashMap<String, String> responseHeaders() {
        super.responseHeaders();
        return headers;
    }
}
