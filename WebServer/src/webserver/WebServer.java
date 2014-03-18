package webserver;

import in2011.http.RequestMessage;
import in2011.http.ResponseMessage;
import in2011.http.MessageFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

public class WebServer {

    private int port;
    private String rootDir;
    private boolean logging;

    public WebServer(int port, String rootDir, boolean logging) {
        this.port = port;
        this.rootDir = rootDir;
        this.logging = logging;
    }

    /**
     * Method which runs continuously and fires of new threads as and when required.
     * @throws IOException
     */
    public void start() {
        while (true) {

            Socket socky = null;

            try {
                socky = this.waitForConnection();
            }
            catch(IOException ioe)
            {
                // nothing needs to happen really, as the loop
                // will continue and no execution is affected
                // by this brief connection error.
            }

            // only execute behaviour if the connection found is not null
            if (socky != null)
            {
                // thread.run(this.acceptConnection);
                this.acceptConnection(socky);
            }
        }
    }

    /**
     * The method which waits for the connection, and returns TRUE if a connection is found.
     *
     * Or perhaps it should return the actual connection, or some useful information, to then pass into "AcceptConnection"
     * @return -- Not sure just yet.
     */
    private Socket waitForConnection() throws IOException {
        ServerSocket serverSock = new ServerSocket(port);
        Socket sock = serverSock.accept();
        return sock;
    }

    /**
     * Probably a method which is fired as a new thread?
     */
    private void acceptConnection(Socket sock)
    {
        // Thread has its own Input and Output Streams
        // initially null, to avoid 'non-initialise' errors.
        OutputStream os = null;
        InputStream is = null;

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
