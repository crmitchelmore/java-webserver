package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public class HeadHandler extends RequestHandler {

    public HeadHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);
        DateFormat dateFormat = new SimpleDateFormat();
        Date ifModifiedSince = null;
        String ifModifiedSinceString = requestMessage.getHeaderFieldValue("If-Modified-Since");
        if ( ifModifiedSinceString != null ){
            try {
                ifModifiedSince = dateFormat.parse(ifModifiedSinceString);
            }catch (ParseException pe ){
                throw new HTTPException(400);
            }
            if ( ifModifiedSince.compareTo(fileRequest.lastModified()) > 0 ){
                throw new HTTPException(304);
            }
        }

    }


    @Override
    public int httpResponseCode()
    {
        return 200;
    }

    @Override
    public byte[] responseBody() throws IOException{
        return null;
    }

    @Override
    public HashMap<String, String> responseHeaders()
    {
        // current date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String httpDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        super.headers.put("Date", httpDate);

        // content type
        super.headers.put("Content-Type", "");

        // last modified
      String lastModified = simpleDateFormat.format(fileRequest.lastModified());

        super.headers.put("Last-Modified", lastModified);

        return headers;
    }
}
