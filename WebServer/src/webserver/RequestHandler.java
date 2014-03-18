package webserver;

import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public abstract class RequestHandler {

    private String uri;

    public RequestHandler(java.io.InputStream is)
    {

    }

    public abstract String getMethod();

    public abstract int getResponse();

    public abstract String getResponseBody();

    public abstract Map<String, String> getResponseHeaders();
}
