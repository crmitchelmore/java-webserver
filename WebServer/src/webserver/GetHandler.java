package webserver;

import in2011.http.RequestMessage;

import javax.print.DocFlavor;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class GetHandler extends HeadHandler {

    private Date lastModified;

    public GetHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

    }
    @Override
    public byte[] responseBody() throws HTTPException
    {
        byte[] bytes = null;
        try {
            bytes = this.fileRequest.getFileBytes();
            if ( bytes == null ){
                throw new HTTPException(404);
            }
        }catch (IOException ioe){
            throw new HTTPException(500);
        }
        return bytes;
    }




}
