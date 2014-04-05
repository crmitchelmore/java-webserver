package webserver;

import in2011.http.RequestMessage;
import webserver.Controllers.JavaWebController;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cmitchelmore on 19/03/2014.
 */
public class POSTHandler extends  RequestHandler
{

    private HashMap<String, Object> multiPartPostParams;
    private JavaWebController webClass;

    public POSTHandler(RequestMessage requestMessage, InputStream inputStream, String rootDirectory)
    {
        super(requestMessage, rootDirectory);
        this.multiPartPostParams = new HashMap<>();
        byte[] bodyBytes = null;
        try {
            bodyBytes = bodyBytesFromInputStream(inputStream);
        }catch (IOException e){
            throw new HTTPException(500);//Internal server error
        }
        try {
            String contentType = requestMessage.getHeaderFieldValue(HEADER_CONTENT_TYPE);
            HashMap<String, Object> params = extractParamsFromBodyWithContentType(bodyBytes, contentType);
            this.multiPartPostParams.putAll(params);
//            System.out.println("Post Params: "+ this.multiPartPostParams);
        }catch (UnsupportedEncodingException uee){
            throw new HTTPException(400); //Bad Request
        }
        webClass = Routes.route(rootDirectory, fileRequest.decodedURI, parameters, multiPartPostParams);
        if ( webClass == null ){
            throw new HTTPException(404);//If we can't route it then not found
        }

    }

    @Override
    public int httpResponseCode()
    {
        return webClass.responseCode(); //201 if created
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

    public HashMap<String, Object> getMultiPartPostParams()
    {
        return this.multiPartPostParams;
    }

    @Override
    public byte[] responseBody() throws HTTPException
    {

        return webClass.responseBody();
    }

    @Override
    public HashMap<String, String> buildResponseHeaders()
    {
        super.buildResponseHeaders();
        headers.put(HEADER_CONTENT_TYPE, "text/html");
        return headers;
    }

    protected HashMap<String, Object> extractParamsFromBodyWithContentType(byte[] bodyBytes, String contentType) throws UnsupportedEncodingException
    {

        if ( contentType.toLowerCase().equals("application/x-www-form-urlencoded") ){
            HashMap<String, Object> multiPartParams = new HashMap<>();
            String bodyString = new String(bodyBytes, MultiPartFormElement.ISO_8859_1);

            multiPartParams.putAll(extractURLEncodedParamsFromString(bodyString)); //Avoid compiler warning!
            return multiPartParams;
        }else if ( contentType.toLowerCase().startsWith("multipart/form-data") ){
            return extractMultiPartParamsFromString(bodyBytes, contentType);
        }

        throw new HTTPException(415);//Unsupported Media Type
    }

    protected HashMap<String, Object> extractMultiPartParamsFromString(byte[] bodyBytes, String contentType)
    {
        MultiPartFormElement[] elements = MultiPartFormElement.parse(bodyBytes, contentType); //We are losing information here...

        return MultiPartFormElement.toParams(elements);
    }
}
