package webserver;


import java.io.IOException;
import java.net.*;

public class WebServer {


    public static final int MAX_CONTENT_LENGTH = 1024*1024;
    public static final String LOG_FILE_NAME = "webserverIN2011.log";
    public static void main(String[] args) throws Exception {
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
        String loggingString = logging ? "with" : "without";
        System.out.println("Starting server at - "+rootDir+":"+port+" "+loggingString+" logging");
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
    public void start() throws Exception
    {
        ServerSocket serverSock = null;
        try {
            serverSock = new ServerSocket(port);
        } catch (BindException b){
            System.out.println("You already have a server running. Shut it down first");
            //throw new Exception();
        }
        if ( logging ){
            Logger.createNewLogFile(rootDir, LOG_FILE_NAME);
        }
    
            while (true) {
                Socket socket = serverSock.accept(); //Maybe catch this and recover sooner?
                System.out.println(c++);
                WebThread webThread = new WebThread(socket, rootDir, logging);
                Thread t = new Thread(webThread);
                t.start();
            }
        
    }


}
