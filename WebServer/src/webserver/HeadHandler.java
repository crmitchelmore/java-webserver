package webserver;

/**
 * Created by George on 11/03/14.
 */
public class HeadHandler extends RequestHandler {

    public HeadHandler()
    {

    }

    @Override
    public String getMethod()
    {
        return "HEAD";
    }
}
