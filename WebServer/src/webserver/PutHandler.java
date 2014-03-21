package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class PUTHandler extends RequestHandler {

    private static final long MAX_FILE_SIZE = 1024 * 1024;

    public PUTHandler(RequestMessageBody requestMessageBody, String rootDirectory)
    {
        super(requestMessageBody, rootDirectory);

        String contentLengthString = requestMessageBody.getHeaderFieldValue("Content-Length");
        if ( contentLengthString == null ){
            throw new HTTPException(411);//Client must specify content length
        }
        long contentLength = Long.parseLong(contentLengthString);

        if ( contentLength > MAX_FILE_SIZE ){
            throw new HTTPException(413);//Entity too large
        }

        byte[] bytes = requestMessageBody.getMessageBody();
        if ( bytes != null && bytes.length > 0 ){
            try {
                fileRequest.createFileOrFolderWithBytes(bytes, MAX_FILE_SIZE);
            }catch (SecurityException s){
                throw new HTTPException(409);//Conflict
            }catch ( IOException s){
                throw new HTTPException(500);//Internal server error
            }
        }
    }

    @Override
    public int httpResponseCode()
    {
        return 201; //Created
    }

    @Override
    public byte[] responseBody() throws HTTPException{
        return new byte[0];
    }

    @Override
    public HashMap<String, String> responseHeaders() {
        super.responseHeaders();

        return headers;
    }
}
