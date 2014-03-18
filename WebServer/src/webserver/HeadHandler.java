package webserver;

import in2011.http.RequestMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
        return 0;
    }

    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public Map<String, String> getResponseHeaders()
    {
        // current date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String httpDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        super.headers.put("Date", httpDate);

        // content type
        super.headers.put("ContentType", "");

        // last modified
        super.headers.put("LastModified", "");

        return headers;
    }
}
