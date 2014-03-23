package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public class HEADHandler extends RequestHandler {

    public HEADHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

        String ifModifiedSinceString = requestMessage.getHeaderFieldValue(HEADER_IF_MODIFIED_SINCE);
        if ( ifModifiedSinceString != null ){

            try {
                Date ifModifiedSince = this.simpleDateFormat.parse(ifModifiedSinceString);
                if ( ifModifiedSince.compareTo(fileRequest.lastModified()) >= 0 ){
                    throw new HTTPException(304); //Not modified
                }
            }catch (ParseException pe){
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
        super.responseHeaders();

        // content type
        headers.put(HEADER_CONTENT_TYPE, fileRequest.mimeType());

        // last modified
        String lastModified = this.simpleDateFormat.format(fileRequest.lastModified());
        headers.put(HEADER_LAST_MODIFIED, lastModified);

        // content length
        headers.put(HEADER_CONTENT_LENGTH, "" + fileRequest.fileSize());
        return headers;
    }
}
