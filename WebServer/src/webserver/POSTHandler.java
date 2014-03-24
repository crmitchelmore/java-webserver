package webserver;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MultipartDataSource;
import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by cmitchelmore on 19/03/2014.
 */
public class POSTHandler extends  RequestHandler
{

    private HashMap<String, Object> multiPartPostParams;

    public POSTHandler(RequestMessage requestMessage, InputStream inputStream, String rootDirectory)
    {
        super(requestMessage, rootDirectory);
        this.multiPartPostParams = new HashMap<>();
        String bodyString = null;
        try {
            byte[] bodyBytes = bodyBytesFromInputStream(inputStream);
            bodyString = bytesToString(bodyBytes);

        }catch (IOException e){
            throw new HTTPException(500);//Internal server error
        }
        try {
            String contentType = requestMessage.getHeaderFieldValue(HEADER_CONTENT_TYPE);
            HashMap<String, Object> params = extractParamsFromBodyWithContentType(bodyString, contentType);
            this.multiPartPostParams.putAll(params);
            System.out.println("Post Params: "+ this.multiPartPostParams);
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
        HashMap<String, String> stringParams = new HashMap<String, String>();
        for ( Map.Entry<String, Object> entry : this.multiPartPostParams.entrySet() ){
            if ( entry.getValue() instanceof String ){
                stringParams.put(entry.getKey(), (String)entry.getValue());
            }
        }
        return stringParams;
    }

    public HashMap<String, Object> multiPartPostParams()
    {
        return this.multiPartPostParams;
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

    protected HashMap<String, Object> extractParamsFromBodyWithContentType(String bodyString, String contentType) throws UnsupportedEncodingException
    {

        if ( contentType.toLowerCase().equals("application/x-www-form-urlencoded") ){
            HashMap<String, Object> multiPartParams = new HashMap<>();
            multiPartParams.putAll(extractURLEncodedParamsFromString(bodyString)); //Avoid compiler warning!
            return multiPartParams;
        }else if ( contentType.toLowerCase().startsWith("multipart/form-data") ){
            return extractMultiPartParamsFromString(bodyString, contentType);
        }

        throw new HTTPException(415);//Unsupported Media Type
    }

    protected HashMap<String, Object> extractMultiPartParamsFromString(String bodyString, String contentType)
    {
        MultiPartFormElement[] elements = MultiPartFormElement.parse(bodyString, contentType); //We are losing information here...

        return MultiPartFormElement.toParams(elements);
    }
}
