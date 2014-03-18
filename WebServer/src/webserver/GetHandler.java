package webserver;

import in2011.http.RequestMessage;

import javax.print.DocFlavor;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * Created by George on 11/03/14.
 */
public class GetHandler extends HeadHandler {

    private Date lastModified;

    public GetHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

    }
    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public String getMethod()
    {
        return "GET";
    }


}
