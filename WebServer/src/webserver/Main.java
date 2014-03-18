package webserver;

import java.io.IOException;

/**
 * Created by George on 11/03/14.
 */
public class Main {

    public static void main(String[] args) throws IOException {
//        String usage = "Usage: java webserver.WebServer <port-number> <root-dir> (\"0\" | \"1\")";
//
//        if (args.length != 3) {
//            throw new Error(usage);
//        }
        int port = 1091;
//        try {
//            port = Integer.parseInt(args[0]);
//        } catch (NumberFormatException e) {
//            throw new Error(usage + "\n" + "<port-number> must be an integer");
//        }
        String rootDir = "/Users/cmitchelmore/Documents";// args[1];
//        boolean logging;
//        if (args[2].equals("0")) {
//            logging = false;
//        } else if (args[2].equals("1")) {
//            logging = true;
//        } else {
//            throw new Error(usage);
//        }
        WebServer server = new WebServer(port, rootDir, false);
        server.start();
    }
}
