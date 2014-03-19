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
public class HEADHandler extends RequestHandler {


    private SimpleDateFormat simpleDateFormat;

    public HEADHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

        this.simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String ifModifiedSinceString = requestMessage.getHeaderFieldValue("If-Modified-Since");
        if ( ifModifiedSinceString != null ){

            try {
                Date ifModifiedSince = this.simpleDateFormat.parse(ifModifiedSinceString);
                if ( ifModifiedSince.compareTo(fileRequest.lastModified()) >= 0 ){
                    throw new HTTPException(304); //Not modified
                }
            }catch (ParseException pe ){
                throw new HTTPException(400); //Bad Request. There might be a case to just ignore this error as we could continue.
            }


        }

    }


    @Override
    public int httpResponseCode()
    {
        return 200;
    }

    @Override
    public byte[] responseBody() throws HTTPException{
        return null;
    }

    @Override
    public HashMap<String, String> responseHeaders()
    {
        // current date
        String httpDate = this.simpleDateFormat.format(new Date(System.currentTimeMillis()));
        headers.put("Date", httpDate);

        // content type
        headers.put("Content-Type", fileRequest.mimeType());

        // last modified
        String lastModified = this.simpleDateFormat.format(fileRequest.lastModified());
        headers.put("Last-Modified", lastModified);


        headers.put("Content-Length", "" + fileRequest.fileSize());
        return headers;
    }
}
