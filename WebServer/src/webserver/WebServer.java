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
                WebThread webThread = new WebThread(socky, rootDir);
                Thread t = new Thread(webThread);
                t.start();


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


}
