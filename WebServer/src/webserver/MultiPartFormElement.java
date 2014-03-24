package webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by cmitchelmore on 24/03/2014.
 */
public class MultiPartFormElement {

    private Object value;
    private String key;

    private HashMap<String, String>headers;

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public MultiPartFormElement()
    {
        this.headers = new HashMap<>();
    }

    //This is a total hack job first draft. Refactor me.
    private static MultiPartFormElement parse(String elementString)
    {
        MultiPartFormElement element = new MultiPartFormElement();

        String[] lines = elementString.split(Pattern.quote("\r\n"));
        boolean content = false;
        StringBuilder stringBuilder = new StringBuilder();
        for ( String line : lines ){
            if ( content ){ //Whatever is left is the content...

                stringBuilder.append(line); //Put it back together if it's broken

            }else if ( line.startsWith("Content-Disposition:") ){
                // Content-Disposition: form-data; name="files"
                // Content-Disposition: file; filename="file1.txt"
                String[] parts = line.split(";");
                for ( String part : parts ){
                    String [] dispositions = part.split(":");
                    if ( dispositions.length == 1 ){
                        dispositions = part.split("=");
                    }
                    element.headers.put(dispositions[0].trim(), dispositions[1]);
                }
            }else if ( line.equals("") ){
                content = true;
            }else{ //Other headers
                String [] header = line.split(":");
                element.headers.put(header[0], header[1]);
            }
        }

        //Multipart forms can include multi parts in them so we recurse
        String contentType = element.headers.get(RequestHandler.HEADER_CONTENT_TYPE);
        if (  contentType != null && contentType.equals("multipart/mixed") ){
            element.value = MultiPartFormElement.parse(stringBuilder.toString(), contentType);
        }else{
            element.value = stringBuilder.toString();
        }
        element.key = element.headers.get("name").replaceAll("\"", "");

        return element;
    }


    public static MultiPartFormElement[] parse(String bodyString, String contentType)
    {
        String boundaryMarker = "boundary=";
        int boundaryStartIndex = contentType.indexOf(boundaryMarker);
        String boundaryString = "--" + contentType.substring(boundaryStartIndex + boundaryMarker.length()) + "\r\n";
        String[] multiPartComponents = bodyString.split(Pattern.quote(boundaryString)); //Escape the boundary so it can safely be used in regex

        ArrayList<MultiPartFormElement> elements = new ArrayList<>();

        for ( int i = 1; i < multiPartComponents.length; i++ ){
            String component = multiPartComponents[i];
            MultiPartFormElement element = MultiPartFormElement.parse(component);
            elements.add(element);
        }

        //Do some java stuff just to get it back in array format
        MultiPartFormElement[] results = new MultiPartFormElement[elements.size()];
        int i = 0;
        for ( MultiPartFormElement e : elements ){
            results[i++] = e;
        }
        return results;
    }

    public static HashMap<String, Object> toParams(MultiPartFormElement[] array)
    {
        HashMap<String, Object> multiPartParams = new HashMap<>();
        for ( MultiPartFormElement element : array ){
            multiPartParams.put(element.getKey(), element.getValue());
        }
        return multiPartParams;
    }
}
