package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public abstract class RequestHandler
{

    protected FileRequest fileRequest;

    protected HashMap<String, String> headers;

    public RequestHandler(RequestMessageBody requestMessageBody, String rootDirectory) throws HTTPException
    {
        try {
            fileRequest = new FileRequest(rootDirectory, requestMessageBody.getURI());
        } catch( UnsupportedEncodingException e){
            throw new HTTPException(400); //Bad request e.g. the URI is not valid
        } catch (SecurityException se) {
            throw new HTTPException(403); //Forbidden. URI is not contained by rootDirectory
        }
        headers = new HashMap<String, String>();
    }
    public HashMap<String, String> getParameters() {
        return fileRequest.getParameters();
    }

    public abstract int httpResponseCode();

    public abstract byte[] responseBody() throws HTTPException;

    public abstract HashMap<String, String> responseHeaders();
}
