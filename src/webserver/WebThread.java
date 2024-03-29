package webserver;

import in2011.http.MessageFormatException;
import in2011.http.RequestMessage;
import in2011.http.ResponseMessage;
import in2011.http.StatusCodes;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Created by cmitchelmore on 18/03/2014.
 */
public class WebThread implements Runnable {

    private Socket sock;
    private String rootDir;
    private boolean logging;

    public WebThread(Socket sock, String rootDir, boolean logging)
    {
        this.logging = logging;
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
        String requestMethod = null;
        String requestURI = null;
        System.out.println("Thread id: #" + Thread.currentThread().getId());
        try {
            // Attempt to initialise streams. We could do them separately and write 500 if input fails but output succeeds.
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
                    requestMethod = requestMessage.getMethod();
                    requestURI = requestMessage.getURI(); //Get these in case there is an exception somewhere
                } catch (MessageFormatException mfe) {
                    throw new HTTPException(400); //Bad Request
                } catch (IOException ioe){
                    throw new HTTPException(500);//Internal server error
                }


                requestHandler = RequestHandlerFactory.createRequest(requestMessage, inputStream, rootDir);


                responseBodyBytes = requestHandler.responseBody();
                // We create a response message, by calling the method GetResponse
                // which handles parsing the given response
                responseMessage =   new ResponseMessage(requestHandler.httpResponseCode());

                // We add the headers to the response message
                for( Map.Entry<String, String> entry : requestHandler.buildResponseHeaders().entrySet() ){

                    responseMessage.addHeaderField(entry.getKey(), entry.getValue());

                }


            }catch (HTTPException httpException){

                int responseCode = httpException.getStatusCode();
                responseMessage = new ResponseMessage(responseCode);
                responseBodyBytes = ("<html><h1>ERROR : "+ responseCode + "</h1>\n <h2>"+ StatusCodes.reasonPhraseFromCode(responseCode) + "</h2></html>").getBytes();

            }

            final int responseCode = responseMessage.getStatusCode();

            System.out.println("Status code: "+responseCode);
            //Write the response message
            responseMessage.write(outputStream);

            //By default all messages should return a body even if it's 0 length (except 1**, 204 and 304)
            if ( !(responseCode/100 == 1 || responseCode == 204 || responseCode == 304) ){
                outputStream.write(responseBodyBytes);
            }

            if ( logging && requestMethod != null && requestURI != null ){
                final String absoluteURI = rootDir + requestURI;
                final String method = requestMethod;
                //Logging is done synchronously but we don't want to hold up closing this thread
                new Thread(new Runnable() {
                    public void run() {
                        Logger.addLogSynchronous(method, absoluteURI, responseCode);

                    }
                }).start();

            }
            // Close this and the thread ends.


        } catch (IOException ioe) {
            ioe.printStackTrace();
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
