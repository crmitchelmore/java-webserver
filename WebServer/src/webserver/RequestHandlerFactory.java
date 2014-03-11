package webserver;

import javax.xml.ws.http.HTTPException;

/**
 * Created by George on 11/03/14.
 */
public class RequestHandlerFactory {

    public RequestHandlerFactory()
    {

    }

    public static RequestHandler createRequest(String method) throws HTTPException
    {
        if (method.equals("HEAD"))
        {
            return new HeadHandler();
        }
        else if(method.equals("GET"))
        {
            return new GetHandler();
        }
        else if(method.equals("PUT"))
        {
            return new PutHandler();
        }
        else
        {
            throw new HTTPException(501);
        }
    }
}
