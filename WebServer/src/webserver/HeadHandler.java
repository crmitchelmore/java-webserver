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

        Date ifModifiedSince = null;
        String ifModifiedSinceString = requestMessage.getHeaderFieldValue("If-Modified-Since");
        if ( ifModifiedSinceString != null ){
            try {
                ifModifiedSince = dateFormat().parse(ifModifiedSinceString);
            }catch (ParseException pe ){
                throw new HTTPException(400);
            }
            if ( ifModifiedSince.compareTo(fileRequest.lastModified()) >= 0 ){
                throw new HTTPException(304);
            }
        }

    }

    private SimpleDateFormat dateFormat()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat;
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

        String httpDate = dateFormat().format(new Date(System.currentTimeMillis()));
        headers.put("Date", httpDate);

        // content type
        headers.put("Content-Type", fileRequest.mimeType());

        // last modified
        String lastModified = dateFormat().format(fileRequest.lastModified());

        headers.put("Last-Modified", lastModified);

        headers.put("Content-Length", ""+fileRequest.fileSize());
        return headers;
    }
}
