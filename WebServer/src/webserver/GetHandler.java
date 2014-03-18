package webserver;

import javax.print.DocFlavor;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class GetHandler extends RequestHandler {

    private Date lastModified;

    public GetHandler(InputStream is)
    {
        super(is);
        this.lastModified
    }

    @Override
    public String getMethod()
    {
        return "GET";
    }

    @Override
    public Map<String, String> getReponseHeaders()
    {

    }
}
