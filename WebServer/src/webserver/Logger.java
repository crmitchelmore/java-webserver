package webserver;

import java.io.File;
import java.io.IOException;

/**
 * Created by George on 11/03/14.
 */
public class Logger {

    private static int id = 0;
    private static FileRequest logFile = null;

    public static void createNewLogFile(String rootDir) throws IOException
    {
        logFile = new FileRequest(rootDir, WebServer.LOG_FILE_NAME);
        //Delete old one first
        logFile.createFileOrFolderWithBytes(new byte[0]);
    }

    public static void addLogSynchronous(String method, String absoluteURI, int responseStatus)
    {
        synchronized (Logger.class){
            logFile.appendLineToFile(++id + ":" + method + ":" + absoluteURI + ":" + responseStatus + "\n");
        }

    }

}
