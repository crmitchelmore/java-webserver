package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class PutHandler extends RequestHandler {

    private static final long MAX_FILE_SIZE = 1024 * 1024;
    public PutHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

        String contentLengthString = requestMessage.getHeaderFieldValue("Content-Length");
        if ( contentLengthString == null ){
            throw new HTTPException(411);//Client must specify content length
        }
        long contentLength = Long.getLong(contentLengthString);
        if ( contentLength > MAX_FILE_SIZE ){
            throw new HTTPException(413);//Entity too large
        }
        byte[] bytes = null;
        try {
            fileRequest.createFileOrFolderWithBytes(null, MAX_FILE_SIZE);
        }catch (SecurityException s){
            throw new HTTPException(409);//Conflict
        }catch ( IOException s){
            throw new HTTPException(500);//Internal server error
        }
    }

    @Override
    public int httpResponseCode()
    {
        return 201;
    }

    @Override
    public byte[] responseBody() throws IOException{
        return null;
    }

    @Override
    public HashMap<String, String> responseHeaders() {
        return null;
    }
}
