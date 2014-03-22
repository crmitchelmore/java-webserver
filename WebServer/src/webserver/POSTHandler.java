package webserver;

import in2011.http.MessageFormatException;
import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

        try {
            extractParams(inputStream);
        }catch (IOException e){
            throw new HTTPException(500);
        }
    }

    @Override
    public int httpResponseCode() {
        return 200; //??
    }

    protected void extractParams(InputStream inputStream)
            throws IOException
    {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024*1024];

        int totalBytes = inputStream.read(bytes);
        if ( totalBytes > 0 ){
            byte[] allBytes = Arrays.copyOfRange(bytes, 0, totalBytes);
            for ( byte b : allBytes ){
                sb.append((char)b);

            }
            System.out.println(sb.toString());
        }
    }



    public HashMap<String, String> getPostParams()
    {
        return this.postParams;
    }

    @Override
    public byte[] responseBody() throws HTTPException {
        return new byte[0];
    }

    @Override
    public HashMap<String, String> responseHeaders() {
        super.responseHeaders();
        return headers;
    }
}
