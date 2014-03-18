package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;

/**
 * Created by George on 11/03/14.
 */
public class RequestHandlerFactory {

    public RequestHandlerFactory()
    {

    }

    public static RequestHandler createRequest(RequestMessage requestMessage, String rootDir) throws HTTPException
    {
        String method = requestMessage.getMethod();
        if (method.equals("HEAD"))
        {
            return new HeadHandler(requestMessage, rootDir);
        }
        else if(method.equals("GET"))
        {
            return new GetHandler(requestMessage, rootDir);
        }
        else if(method.equals("PUT"))
        {
            return new PutHandler(requestMessage, rootDir);
        }
        else
        {
            throw new HTTPException(501);
        }
    }
}
