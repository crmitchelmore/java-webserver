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
    public WebThread(Socket sock, String rootDir)
    {
        this.sock = sock;
        this.rootDir = rootDir;
    }


    /**
     * run used by Runnable interface
     */
    @Override
    public void run()
    {
        // Thread has its own Input and Output Streams
        // Declare outside of the try catch scope. Initially null, to avoid 'non-initialise' errors.
        OutputStream outputStream = null;
        InputStream inputStream = null;

        System.out.println("Thread id: #" + Thread.currentThread().getId());
        try {
            // attempt to initialise streams
            outputStream = sock.getOutputStream();
            inputStream = sock.getInputStream();

            // Thread creates a response message by parsing the input stream
            RequestMessage requestMessage = null;
            ResponseMessage responseMessage = null;
            RequestHandler requestHandler = null;
            byte[] responseBodyBytes = null;
            try {


                try {
                    // We create a request handler, based on the request made by the user
                    requestMessage = RequestMessage.parse(inputStream);
                } catch (MessageFormatException mfe) {
                    throw new HTTPException(400); //Bad Request
                }


                requestHandler = RequestHandlerFactory.createRequest(requestMessage, rootDir);

                responseBodyBytes = requestHandler.responseBody();
                // We create a response message, by calling the method GetResponse
                // which handles parsing the given response
                responseMessage =   new ResponseMessage(requestHandler.httpResponseCode());

                // We add the headers to the response message
                for( Map.Entry<String, String> entry : requestHandler.responseHeaders().entrySet() ){

                    responseMessage.addHeaderField(entry.getKey(), entry.getValue());

                }


            }catch (HTTPException httpException){

                responseMessage = new ResponseMessage(httpException.getStatusCode());
                responseBodyBytes = ("<h1>ERROR : "+responseMessage.getStatusCode() + "</h1>").getBytes();

            }



            //Write the response message
            responseMessage.write(outputStream);
            outputStream.write(responseBodyBytes);

            // Close this and the thread ends.

        } catch (IOException ioe) {
            // this COULD be handled, to prevent further execution
            // but any issues will only occur on the users individual
            // thread, so wont affect the robustness of the server
        } finally {
            try {
                sock.close();
            }catch (IOException e){
                //Game over
                e.printStackTrace();
            }
        }
    }

}
