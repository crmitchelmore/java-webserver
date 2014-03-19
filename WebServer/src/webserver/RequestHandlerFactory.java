package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;

/**
 * Created by George on 11/03/14.
 */
public class RequestHandlerFactory {

    private static final float MINIMUM_HTTP_VERSION = 1.1f;
    private static final int MAXIMUM_URI_LENGTH = 2000;
    public RequestHandlerFactory()
    {

    }

    public static RequestHandler createRequest(RequestMessage requestMessage, String rootDir) throws HTTPException
    {
        //A comment here...
        if ( Float.parseFloat(requestMessage.getVersion()) < MINIMUM_HTTP_VERSION ){
            throw new HTTPException(505);
        }
        if ( requestMessage.getURI().length() > MAXIMUM_URI_LENGTH ){
            throw new HTTPException(414);
        }

        String method = requestMessage.getMethod();
        if ( method.equals("HEAD") ){
            return new HeadHandler(requestMessage, rootDir);
        } else if( method.equals("GET") ) {
            return new GetHandler(requestMessage, rootDir);
        }else if( method.equals("PUT") ){
            return new PutHandler(requestMessage, rootDir);
        }else if( method.equals("POST") ){
            return null; //TODO
        }else{
            throw new HTTPException(501);//Not implemented
        }
    }
}
