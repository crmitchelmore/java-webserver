package webserver;

import com.sun.org.apache.xpath.internal.operations.*;
import in2011.http.RequestMessage;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by George on 11/03/14.
 */
public abstract class RequestHandler
{

    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_DATE = "Date";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static final String TIMEZONE = "GMT";
    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String STRING_ENCODING = "UTF-8";
    protected String rootDirectory;
    protected FileRequest fileRequest;
    protected SimpleDateFormat simpleDateFormat;
    protected RequestMessage requestMessage;
    protected HashMap<String, String> headers;

    protected HashMap<String, String> parameters;

    public HashMap<String, String> getParameters() {
        return parameters;
    }


    public RequestHandler(RequestMessage requestMessage, String rootDirectory) throws HTTPException
    {
        this.requestMessage = requestMessage;
        this.rootDirectory = rootDirectory;
        this.simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();

        try {
            String uri = requestMessage.getURI().substring(1); //Chop the leading
            //Extract params
            uri = extractParamsFromURI(uri);

            String decodedURI =  URLDecoder.decode(uri, STRING_ENCODING); //Remove hex Maybe use ISO-8859-1

            fileRequest = new FileRequest(rootDirectory, decodedURI);
        } catch( UnsupportedEncodingException e){
            throw new HTTPException(400); //Bad request e.g. the URI is not valid
        } catch (SecurityException se) {
            throw new HTTPException(403); //Forbidden. URI is not contained by rootDirectory
        }
    }

    public abstract int httpResponseCode();

    public abstract byte[] responseBody() throws HTTPException;

    public HashMap<String, String> buildResponseHeaders()
    {
        // current date
        String httpDate = this.simpleDateFormat.format(new Date(System.currentTimeMillis()));
        headers.put(HEADER_DATE, httpDate);
        return headers;
    }

    public String absoluteURI()
    {
        return fileRequest.absolutePath.toString();
    }

    protected HashMap<String, String> extractURLEncodedParamsFromString(String paramString) throws UnsupportedEncodingException
    {
        String[] params = paramString.split("&");
        HashMap<String, String> paramsHashMap = new HashMap<>();
        for ( String param : params ){
            String[] keyValue = param.split("=");
            String key = URLDecoder.decode(keyValue[0], STRING_ENCODING);
            String value = URLDecoder.decode(keyValue[1], STRING_ENCODING);
            paramsHashMap.put(key,value);
        }
        return paramsHashMap;
    }

    protected byte[] bodyBytesFromInputStream(InputStream inputStream) throws IOException
    {
        byte[] bytes = new byte[WebServer.MAX_CONTENT_LENGTH];

        String contentLengthString = this.requestMessage.getHeaderFieldValue(HEADER_CONTENT_LENGTH);
        if ( contentLengthString != null ){

            System.out.print("Start read...");
            int offset = 0;
            int totalBytes = 0;

            final int PageSize = 16384; //Have to read in pages because large files don't fit in buffer otherwise
            while ( (offset = inputStream.read(bytes, totalBytes, PageSize)) == PageSize ){
                totalBytes+=offset;
            }
            totalBytes+=offset;

            System.out.println(" Finish read");
            return  Arrays.copyOfRange(bytes, 0, Math.min(WebServer.MAX_CONTENT_LENGTH, totalBytes));

        }
        return null;
    }


    protected String extractParamsFromURI(String uri) throws UnsupportedEncodingException
    {
        int paramMarkerIndex = uri.indexOf("?");
        if ( paramMarkerIndex > 0 ){

            int fragmentMarkerIndex = uri.indexOf("#");
            int end = fragmentMarkerIndex > 0 ? fragmentMarkerIndex : uri.length();
            String paramString = uri.substring(paramMarkerIndex + 1, end);
            HashMap<String, String> getParams = extractURLEncodedParamsFromString(paramString);
            this.parameters.putAll(getParams);
            System.out.println("Get Params: "+ this.parameters);

            return uri.substring(0, paramMarkerIndex); //URI without params
        }
        return uri;
    }


    protected String bytesToString(byte[] bytes)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for ( byte b : bytes ){
            stringBuilder.append((char)b);
        }
        return stringBuilder.toString();
    }


}
