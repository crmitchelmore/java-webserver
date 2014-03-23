package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;

/**
 * Created by George on 11/03/14.
 */
public class GETHandler extends HEADHandler {


    public GETHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);
    }

    @Override
    public byte[] responseBody() throws HTTPException
    {
        try {
            byte[] bytes = this.fileRequest.getFileBytes();
            if ( bytes == null ){
                throw new HTTPException(404); //Not found
            }
            return bytes;

        }catch (IOException ioe){
            throw new HTTPException(500); //Internal server error
        }

    }




}
