package webserver;

import in2011.http.RequestMessage;
import in2011.http.ResponseMessage;
import in2011.http.StatusCodes;
import in2011.http.EmptyMessageException;
import in2011.http.MessageFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.*;
import java.text.MessageFormat;
import java.util.Date;
import org.apache.http.client.utils.DateUtils;

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
    public void start() throws IOException, MessageFormatException {
        while (true) {

            Socket sohkahtoa = this.waitForConnection();

            if (sohkahtoa != null)
            {
                // perhaps this part here starts a new thread, within the while loop.
                // thread.run(this.acceptConnection);
                this.acceptConnection(sohkahtoa);
            }
            else
            {
                throw new IOException();
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
    private void acceptConnection(Socket sock) throws IOException,  MessageFormatException
    {
        // Thread has its own Input and Output Streams4
        OutputStream os = sock.getOutputStream();
        InputStream is = sock.getInputStream();

        // Thread creates a response message by parsing the input stream
        RequestMessage msg = RequestMessage.parse(is);

        // We create a request handler, based on the request made by the user
        RequestHandler thisOne = RequestHandlerFactory.createRequest(msg, rootDir);

        // We create a response message, by calling the method GetResponse
        // which handles parsing the given response
        ResponseMessage rspMsg = new ResponseMessage(thisOne.getResponse());

        // We add the headers to the response message
        thisOne.getResponseHeaders();

        thisOne.getResponseBody();

        // We write the message to the OutputStream
        msg.write(os);

        // Close this and the thread ends.
        sock.close();
    }

    /**
     * Method which handles File stuff, for any requests involving files.
     * @throws HTTPException
     */
    public void doStuffwithfile() throws HTTPException{
        try {
            FileRequest c = FileRequest(path); //throws 403 and 400
            c.save; //404
        } catch ( IOException asd ){
            throw new HTTPException(404);
        }
    }

    /**
     * Method which handles adding the body to the ResponseMessage.
     */
    private void addBodyToResponse(RequestMessage message)
    {
        // how da fuq?
    }

    /**
     * Method which handles adding headers to the returned response.
     * @param message
     */
    public void addHeadersToResponse(RequestMessage message){
        message.addHeaderField("Date", "123123123");
    }
}
