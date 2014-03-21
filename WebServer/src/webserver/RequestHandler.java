package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public abstract class RequestHandler
{

    protected FileRequest fileRequest;
    protected SimpleDateFormat simpleDateFormat;
    protected HashMap<String, String> headers;

    public RequestHandler(RequestMessageBody requestMessageBody, String rootDirectory) throws HTTPException
    {
        this.simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

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

    public HashMap<String, String> responseHeaders()
    {
        // current date
        String httpDate = this.simpleDateFormat.format(new Date(System.currentTimeMillis()));
        headers.put("Date", httpDate);
        return headers;
    }
}
