package webserver.Controllers;

import webserver.FileRequest;
import webserver.MultiPartFormElement;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by cmitchelmore on 01/04/2014.
 */
public class Profile extends JavaWebController {

    private String responseString;

    public Profile(String rootDirectory, HashMap<String, String> urlParams, HashMap<String, Object> postParams){
        super(rootDirectory, urlParams, postParams);
        //Do stuff!
        String filename = (String)((HashMap<String, Object>)postParams.get("afileheaders")).get("filename");
        String name = (String)postParams.get("aname");
        String file = (String)postParams.get("afile");
        try {
            FileRequest fileRequest = new FileRequest(rootDirectory, "img/"+filename);
            byte[] a =  file.getBytes(MultiPartFormElement.ISO_8859_1);
            fileRequest.createFileOrFolderWithBytes(a);
            FileRequest fileRequest2 = new FileRequest(rootDirectory, "profiles/"+name+".html");

            fileRequest2.createFileOrFolderWithBytes(generateHTMLBytes(name, filename));
            responseString = "Profile created: <a href=\"/profiles/"+name+".html\">"+name+"</a>";
        }catch (SecurityException e){
            throw new HTTPException(409);//conflict
        } catch (IOException e) {
            throw new HTTPException(500);//o dear
        }


    }
    private byte[] generateHTMLBytes(String name, String filename)
    {
        String html = "<html>\n<head><title>" + name + "</title></head>\n<h1>" + name + "</h1><br><img src=\"/img/"+filename+"\"></html>";
        return html.getBytes();
    }


    @Override
    public byte[] responseBody() {
        return responseString.getBytes();
    }


}
