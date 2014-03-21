package webserver;

import in2011.http.MessageFormatException;
import in2011.http.Message;
import in2011.http.RequestMessage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cmitchelmore on 20/03/2014.
 */
public class RequestMessageBody extends Message {

    private RequestMessage requestMessage;

    public byte[] getMessageBody() {
        return messageBody;
    }

    private byte[] messageBody;

    public RequestMessageBody(String method, String uri, String version)
    {
        this.requestMessage = new RequestMessage(method, uri, version);
    }




    public static RequestMessageBody parse(InputStream inputStream)
            throws IOException, MessageFormatException
    {
        RequestMessage requestMessage = RequestMessage.parse(inputStream);

        RequestMessageBody requestMessageBody = new RequestMessageBody(requestMessage.getMethod(), requestMessage.getURI(), requestMessage.getVersion());
        requestMessageBody.requestMessage = requestMessage;


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

//        if ( inputStream.available() > 0 ){
            int b = inputStream.read();
            while ( b != -1 ) {

                byteArrayOutputStream.write(b);
                b = inputStream.read();
                System.out.println(b);
            }

            requestMessageBody.messageBody = byteArrayOutputStream.toByteArray();
//        }
        return requestMessageBody;
    }

    @Override
    public String getHeaderFieldValue(String fieldName)
    {
        return this.requestMessage.getHeaderFieldValue(fieldName);
    }

    @Override
    protected String getStartLine() {
        return this.requestMessage.getStartLine();
    }

    public String getMethod()
    {
        return this.requestMessage.getMethod();
    }

    public String getURI()
    {
        return this.requestMessage.getURI();
    }
}
