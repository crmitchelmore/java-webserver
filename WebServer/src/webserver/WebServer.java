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
    public void start() throws IOException {
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
                // handle what happens when the socket fails?
            }
        }
    }

    /**
     * The method which waits for the connection, and returns TRUE if a connection is found.
     *
     * Or perhaps it should return the actual connection, or some useful information, to then pass into "AcceptConnection"
     * @return -- Not sure just yet.
     */
    private Socket waitForConnection() {
        try {
            ServerSocket serverSock = new ServerSocket(port);
            Socket sock = serverSock.accept();
            return sock;
        }
        catch(IOException e)
        {
            // something went wrong with creating the server socket.
        }

        // only happens when exception is thrown, so perhaps could be handled better.
        return null;
    }

    /**
     * Probably a method which is fired as a new thread?
     */
    private void acceptConnection(Socket sock) throws IOException,  MessageFormatException
    {
        // thread gets its own request handler.
        RequestHandler requestHandler;

        // Thread has its own Input and Output Streams4
        OutputStream os = sock.getOutputStream();
        InputStream is = sock.getInputStream();

        RequestMessage msg = RequestMessage.parse(is);
        RequestHandler thisOne = RequestHandlerFactory.createRequest(msg);

        ResponseMessage rspMsg = new ResponseMessage(thisOne.getResponse());



        // HANDLE ANY ERRORS AND RETURNS ETC....
        try {
            m = RequestMessage.parse(is);
        } catch (MessageFormatException ee) {
            throw new HTTPException(403);
        }

        this.addHeadersToResponse(msg);
        msg.write(os);
        os.write(e.getLocalizedMessage().getBytes());

        // Close this and the thread ends.
        sock.close();
    }

    /**
     * Method which handles File stuff, for any requests involving files.
     * @throws HTTPException
     */
    public void doStuffwithfile() throws HTTPException{
        try {
            FileHandle c = FileHandler(path); //throws 403 and 400
            c.save; //404
        } catch ( IOException asd ){
            throw new HTTPException(404);
        }
    }

    /**
     * Method which handles adding headers to the returned response.
     * @param r
     */
    public void addHeadersToResponse(ResponseMessage r){
        r.addHeaderField("Date","123123123");
    }
}
