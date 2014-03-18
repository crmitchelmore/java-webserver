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
    static int c = 0;
    public void start() throws IOException
    {
        ServerSocket serverSock = new ServerSocket(port);

        while (true) {
            Socket socket = serverSock.accept();
            System.out.println(c++);
            WebThread webThread = new WebThread(socket, rootDir);
            Thread t = new Thread(webThread);
            t.start();
        }
    }


}
