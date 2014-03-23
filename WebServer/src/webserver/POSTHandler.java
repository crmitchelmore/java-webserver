package webserver;

import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by cmitchelmore on 19/03/2014.
 */
public class POSTHandler extends  RequestHandler
{

    private HashMap<String, String> postParams;

    public POSTHandler(RequestMessage requestMessage, InputStream inputStream, String rootDirectory)
    {
        super(requestMessage, rootDirectory);
        this.postParams = new HashMap<>();
        String bodyString = null;
        try {
            byte[] bodyBytes = bodyBytesFromInputStream(inputStream);
            bodyString = bytesToString(bodyBytes);

        }catch (IOException e){
            throw new HTTPException(500);//Internal server error
        }
        try {
            this.postParams.putAll(extractURLEncodedParamsFromString(bodyString));
            System.out.println("Post Params: "+ this.postParams);
        }catch (UnsupportedEncodingException uee){
            throw new HTTPException(400); //Bad Request
        }

    }

    @Override
    public int httpResponseCode()
    {
        return 200; //204 if there is no content or 201 if created
    }


    public HashMap<String, String> getPostParams()
    {
        return this.postParams;
    }

    @Override
    public byte[] responseBody() throws HTTPException
    {
        return new byte[0];
    }

    @Override
    public HashMap<String, String> buildResponseHeaders()
    {
        super.buildResponseHeaders();
        return headers;
    }
}
