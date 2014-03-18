package webserver;

import in2011.http.MessageFormatException;
import in2011.http.RequestMessage;
import in2011.http.ResponseMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Created by cmitchelmore on 18/03/2014.
 */
public class WebThread implements Runnable {

    private Socket sock;
    private String rootDir;
    public WebThread(Socket sock, String rootDir){
        this.sock = sock;
        this.rootDir = rootDir;
    }


    /**
     * Probably a method which is fired as a new thread?
     */
    @Override
    public void run()
    {
        // Thread has its own Input and Output Streams
        // initially null, to avoid 'non-initialise' errors.
        OutputStream os = null;
        InputStream is = null;

        System.out.println("Thread id: #" + Thread.currentThread().getId());
        try
        {
            // attempt to initialise streams
            os = sock.getOutputStream();
            is = sock.getInputStream();

            // Thread creates a response message by parsing the input stream
            RequestMessage requestMessage = RequestMessage.parse(is);


            ResponseMessage rspMsg = null;
            // We create a request handler, based on the request made by the user
            RequestHandler requestHandler = null;
            byte[] body = null;
            try {



                requestHandler = RequestHandlerFactory.createRequest(requestMessage, rootDir);

                body = requestHandler.responseBody();
                // We create a response message, by calling the method GetResponse
                // which handles parsing the given response
                rspMsg =   new ResponseMessage(requestHandler.httpResponseCode());

                // We add the headers to the response message
                for(Map.Entry<String, String> ent : requestHandler.responseHeaders().entrySet())
                {
                    rspMsg.addHeaderField(ent.getKey(), ent.getValue());
                }


            }catch (HTTPException httpException){

                rspMsg = new ResponseMessage(httpException.getStatusCode());
                body = ("<h1>ERROR : "+rspMsg.getStatusCode() + "</h1>").getBytes();

            }



            //Write the response message
            rspMsg.write(os);
            os.write(body);

            // Close this and the thread ends.

        }

        catch (IOException ioe)
        {
            // this COULD be handled, to prevent further execution
            // but any issues will only occur on the users individual
            // thread, so wont affect the robustness of the server
        }
        catch (MessageFormatException mfe)
        {
            // something
        }finally {
            try {
                sock.close();
            }catch (IOException e){
                //Game over
            }
        }
    }

}
