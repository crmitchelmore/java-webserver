package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;

/**
 * Created by George on 11/03/14.
 */
public class RequestHandlerFactory {

    private static final float MINIMUM_HTTP_VERSION = Float.parseFloat(RequestMessage.DEFAULT_HTTP_VERSION);
    private static final int MAXIMUM_URI_LENGTH = 2000;
    public RequestHandlerFactory()
    {

    }

    public static RequestHandler createRequest(RequestMessageBody requestMessageBody, String rootDir) throws HTTPException
    {
        //A comment here...
        if ( Float.parseFloat(requestMessageBody.getVersion()) < MINIMUM_HTTP_VERSION ){
            throw new HTTPException(505);
        }
        if ( requestMessageBody.getURI().length() > MAXIMUM_URI_LENGTH ){
            throw new HTTPException(414);
        }

        String method = requestMessageBody.getMethod();
        if ( method.equals("HEAD") ){
            return new HEADHandler(requestMessageBody, rootDir);
        } else if( method.equals("GET") ) {
            return new GETHandler(requestMessageBody, rootDir);
        }else if( method.equals("PUT") ){
            return new PUTHandler(requestMessageBody, rootDir);
        }else if( method.equals("POST") ){
            return new POSTHandler(requestMessageBody, rootDir);
        }else{
            throw new HTTPException(501);//Not implemented
        }
    }
}
