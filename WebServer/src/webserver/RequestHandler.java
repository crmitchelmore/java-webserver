package webserver;

import in2011.http.RequestMessage;

import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public abstract class RequestHandler {

    private String uri;

    public RequestHandler(RequestMessage requestMessage)
    {

    }

    public abstract String getMethod();

    public abstract int getResponse();

    public abstract String getResponseBody();

    public abstract Map<String, String> getResponseHeaders();
}
