package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.InputStream;

/**
 * Created by George on 11/03/14.
 */
public class RequestHandlerFactory {

    private static final float MINIMUM_HTTP_VERSION = Float.parseFloat(RequestMessage.DEFAULT_HTTP_VERSION);
    private static final int MAXIMUM_URI_LENGTH = 2000;
    public RequestHandlerFactory()
    {

    }

    public static RequestHandler createRequest(RequestMessage requestMessage, InputStream inputStream, String rootDir) throws HTTPException
    {
        //A comment here...
        if ( Float.parseFloat(requestMessage.getVersion()) < MINIMUM_HTTP_VERSION ){
            throw new HTTPException(505);
        }
        if ( requestMessage.getURI().length() > MAXIMUM_URI_LENGTH ){
            throw new HTTPException(414);
        }

        String method = requestMessage.getMethod();
        System.out.println("Method: " + method);
        if ( method.equals("HEAD") ){
            return new HEADHandler(requestMessage, rootDir);
        }else if( method.equals("GET") ) {
            return new GETHandler(requestMessage, rootDir);
        }else if( method.equals("PUT") ){
            return new PUTHandler(requestMessage, inputStream, rootDir);
        }else if( method.equals("POST") ){
            return new POSTHandler(requestMessage, inputStream, rootDir);
        }else{
            throw new HTTPException(501);//Not implemented
        }
    }
}
