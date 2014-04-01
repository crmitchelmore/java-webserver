package webserver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by cmitchelmore on 01/04/2014.
 */
public class BasicForm extends JavaWebClass{

    private String responseString;

    public BasicForm(String rootDirectory, HashMap<String, String> urlParams, HashMap<String, Object> postParams){
        super(rootDirectory, urlParams, postParams);
        //Do stuff!
        String filename = (String)((HashMap<String, Object>)postParams.get("afileheaders")).get("filename");
        String name = (String)postParams.get("aname");
        String file = (String)postParams.get("afile");
        try {
            FileRequest fileRequest = new FileRequest(rootDirectory, "img/"+filename);
            fileRequest.createFileOrFolderWithBytes(file.getBytes(Charset.forName("UTF-8")));//PROBLEM IS HERE GET BYTES DOES SOMETHING NOT GOOD
            FileRequest fileRequest2 = new FileRequest(rootDirectory, "profiles/"+name+".html");

            fileRequest2.createFileOrFolderWithBytes(generateHTMLBytes(name, filename));
            responseString = "Profile created: <a href=\"/profiles/"+name+".html\">"+name+"</a>";
        }catch (SecurityException e){
            e.printStackTrace();
            //We wont be looking in the wrong place here so no worries
        } catch (IOException e) {
            e.printStackTrace();
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
