package webserver;

import in2011.http.RequestMessage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public class HeadHandler extends RequestHandler {

    public HeadHandler(RequestMessage requestMessage, String rootDir)
    {
        super(requestMessage, rootDir);

    }

    @Override
    public String getMethod()
    {
        return "HEAD";
    }

    @Override
    public int getResponse() {
//        return lastModifiedFileDate.compareTo(dateLastModified) > 0;
        return 0;
    }

    @Override
    public byte[] getResponseBody() throws IOException{
        return null;
    }

    @Override
    public HashMap<String, String> getResponseHeaders()
    {
        // current date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String httpDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        super.headers.put("Date", httpDate);

        // content type
        super.headers.put("Content-Type", "");

        // last modified
      String lastModified = simpleDateFormat.format(fileRequest.lastModified());

        super.headers.put("Last-Modified", lastModified);

        return headers;
    }
}
