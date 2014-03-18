package webserver;

import in2011.http.RequestMessage;

import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class HeadHandler extends RequestHandler {

    public HeadHandler(RequestMessage requestMessage)
    {
        super(requestMessage);

    }

    @Override
    public String getMethod()
    {
        return "HEAD";
    }

    @Override
    public int getResponse() {
        return 0;
    }

    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        return null;
    }

    @Override
    public Map<String, String> getReponseHeaders()
    {

    }
}
