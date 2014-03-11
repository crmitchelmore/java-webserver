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

    class{
        statusCode : 404;
    }
    public void start() throws IOException {
        ServerSocket serverSock = new ServerSocket(port);
        int requestType,uri,responsecode = 0;
        while (true) {

          try {
            try {
                throw new EmptyMessageException("Empty....");
                this.addHeadersToResponse(msg);


               this.doStuffwithfile();



            } catch ( HTTPException e ){


            //e.getCause() =  \"File not found""


            // listen for a new connection on the server socket
            Socket conn = serverSock.accept();
            // get the output stream for sending data to the client
            OutputStream os = conn.getOutputStream();
            InputStream is = conn.getInputStream();
            RequestMessage m = null;
            try {
                 m = RequestMessage.parse(is);
            } catch (MessageFormatException ee) {
                throw new HTTPException(403);
            }
            EmptyMessageException em = new EmptyMessageException();
            StatusCodes s = new StatusCodes();
           int code =  httpexcetp.getcode


//            System.out.print(m.getStartLine());
//            int c = is.read();
//            while ( c > 0 ){
//
//             System.out.print((char)c);
//                c = is.read();
//            }
            // send a response
        responsecode = code;
            ResponseMessage msg = new OurResponseMessage(m, code);
             this.addHeadersToResponse(msg);
            msg.write(os);
            os.write(e.getLocalizedMessage().getBytes());

            conn.close();

            }
        }

        }

        catch (Exception e){
            //500
        } finally {
           if ( logging enabled ){ logThatShit(requestType,uri,responsecode);}
        }

    }
public void doStuffwithfile() throws HTTPException{
    try {
        FileHandle c = FileHandler(path); //throws 403 and 400
        c.save; //404
    } catch ( IOException asd ){
        throw new HTTPException(404);
    }
}

    public void addHeadersToResponse(ResponseMessage r){
        r.addHeaderField("Date","123123123");
    }
    public static void main(String[] args) throws IOException {
        String usage = "Usage: java webserver.WebServer <port-number> <root-dir> (\"0\" | \"1\")";

        if (args.length != 3) {
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
        if (args[2].equals("0")) {
            logging = false;
        } else if (args[2].equals("1")) {
            logging = true;
        } else {
            throw new Error(usage);
        }
        WebServer server = new WebServer(port, rootDir, logging);
        server.start();
    }
}
