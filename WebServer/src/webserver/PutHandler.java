package webserver;

import in2011.http.RequestMessage;

import javax.print.DocFlavor;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class PUTHandler extends RequestHandler {


    public PUTHandler(RequestMessage requestMessage, InputStream inputStream, String rootDirectory)
    {
        super(requestMessage, rootDirectory);

        String contentLengthString = requestMessage.getHeaderFieldValue(HEADER_CONTENT_LENGTH);
        if ( contentLengthString == null ){
            throw new HTTPException(411);//Client must specify content length
        }
        long contentLength = Long.parseLong(contentLengthString);

        if ( contentLength > MAX_CONTENT_LENGTH ){
            throw new HTTPException(413);//Entity too large
        }
        try {
            byte[] bytes = bodyBytesFromInputStream(inputStream);
            if ( bytes != null && bytes.length > 0 ){
                fileRequest.createFileOrFolderWithBytes(bytes);
            }
        }catch (SecurityException s){
            throw new HTTPException(409);//Conflict
        }catch (IOException s){
            throw new HTTPException(500);//Internal server error
        }
    }

    @Override
    public int httpResponseCode()
    {
        return 201; //Created
    }

    @Override
    public byte[] responseBody() throws HTTPException
    {
        return new byte[0];
    }

    @Override
    public HashMap<String, String> responseHeaders()
    {
        super.responseHeaders();

        return headers;
    }
}
