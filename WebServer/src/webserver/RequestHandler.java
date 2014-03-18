package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public abstract class RequestHandler {

    private String uri;

    protected FileRequest fileRequest;

    protected Map<String, String> headers;

    public RequestHandler(RequestMessage requestMessage, String rootDir) throws HTTPException
    {
        try {
            fileRequest = new FileRequest(rootDir, requestMessage.getURI());
        }
        catch(URISyntaxException use)
        {
            throw new HTTPException(403);
        }
        catch (SecurityException se)
        {
            throw new HTTPException(400);
        }
    }

    public abstract String getMethod();

    public abstract int getResponse();

    public abstract String getResponseBody();

    public abstract Map<String, String> getResponseHeaders();
}
