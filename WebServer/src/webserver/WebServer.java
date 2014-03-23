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


    public static void main(String[] args) throws IOException {
        String usage = "Usage: java webserver.WebServer <port-number> <root-dir> (\"0\" | \"1\")";

        if ( args.length != 3 ) {
            throw new Error(usage);
        }
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new Error(usage + "\n" + "<port-number> must be an integer");
        }
        String rootDir = args[1];
        boolean logging;
        if ( args[2].equals("0") ) {
            logging = false;
        } else if ( args[2].equals("1") ) {
            logging = true;
        } else {
            throw new Error(usage);
        }
        WebServer server = new WebServer(port, rootDir, logging);
        server.start();
    }


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
            WebThread webThread = new WebThread(socket, rootDir, logging);
            Thread t = new Thread(webThread);
            t.start();
        }
    }


}
