package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public class HEADHandler extends RequestHandler {

    public HEADHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

        String ifModifiedSinceString = requestMessage.getHeaderFieldValue(HEADER_IF_MODIFIED_SINCE);
        // If there is an ifModifiedString and the requested URI would return some content usually (Covers cases such as generating directory structure and index.html)
        if ( ifModifiedSinceString != null && fileRequest.hasContent() ){

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
    public byte[] responseBody() throws HTTPException
    {
        if ( !fileRequest.hasContent() ){
             throw new HTTPException(404); //Not found
        }
        // This is not the best way to get here but I don't know how to move this code because the fileBytes is the definitive value for any of the file existing options.
        return new byte[0];
    }

    @Override
    public HashMap<String, String> buildResponseHeaders()
    {
        super.buildResponseHeaders();


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
