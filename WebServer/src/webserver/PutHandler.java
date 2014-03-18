package webserver;

import in2011.http.RequestMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class PutHandler extends RequestHandler {

    public PutHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);
    }

    @Override
    public String getMethod()
    {
        return "PUT";
    }

    @Override
    public int getResponse() {
        return 0;
    }

    @Override
    public byte[] getResponseBody() throws IOException{
        return null;
    }

    @Override
    public HashMap<String, String> getResponseHeaders() {
        return null;
    }
}
