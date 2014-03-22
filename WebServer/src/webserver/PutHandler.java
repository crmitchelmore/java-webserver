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

    private static final long MAX_FILE_SIZE = 1024 * 1024;

    public PUTHandler(RequestMessage requestMessage, InputStream inputStream, String rootDirectory)
    {
        super(requestMessage, rootDirectory);

        String contentLengthString = requestMessage.getHeaderFieldValue("Content-Length");
        if ( contentLengthString == null ){
            throw new HTTPException(411);//Client must specify content length
        }
        long contentLength = Long.parseLong(contentLengthString);

        if ( contentLength > MAX_FILE_SIZE ){
            throw new HTTPException(413);//Entity too large
        }
        try {
            byte[] bytes = getMessageBodyBytesFromInputStream(inputStream);
            if ( bytes != null && bytes.length > 0 ){
                fileRequest.createFileOrFolderWithBytes(bytes, MAX_FILE_SIZE);
            }
        }catch (SecurityException s){
            throw new HTTPException(409);//Conflict
        }catch ( IOException s){
            throw new HTTPException(500);//Internal server error
        }
    }

    public byte[] getMessageBodyBytesFromInputStream(InputStream inputStream) throws IOException
    {
        String contentLengthString = this.requestMessage.getHeaderFieldValue("Content-Length");
        if ( contentLengthString != null ){
            byte[] bytes = new byte[1024*1024];

            int totalBytes = inputStream.read(bytes);
            return Arrays.copyOfRange(bytes, 0, totalBytes);
        }
        return null;
//        if ( contentLengthString != null ){
//            long contentLength = Long.parseLong(contentLengthString);
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//            int b = inputStream.read();
//            byteArrayOutputStream.write(b);
//            while ( b != -1 && --contentLength > 0 ) {
//                b = inputStream.read();
//                byteArrayOutputStream.write(b);
//            }
//            requestMessageBody.messageBody = byteArrayOutputStream.toByteArray();
//
//        }
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
