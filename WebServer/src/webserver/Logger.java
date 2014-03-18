package webserver;

/**
 * Created by George on 11/03/14.
 */
public class Logger {

    private static int id = 0;

    public Logger()
    {

    }

    public static void AddLog(String method, String Uri, String responseStatus)
    {

        id++;
    }

}
