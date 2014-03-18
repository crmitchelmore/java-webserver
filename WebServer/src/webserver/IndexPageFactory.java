package webserver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * Created by cmitchelmore on 18/03/2014.
 */
public class IndexPageFactory {


    static final String defaultIndex = "index.html";
    private String[] acceptablePages;
    private  FileRequest fileRequest;

    public IndexPageFactory(FileRequest fileRequest, String[] acceptablePages) throws Exception
    {
        if ( fileRequest == null ){
            throw new Exception("Need a file request");
        }
        this.fileRequest = fileRequest;


        if ( acceptablePages != null ){
            this.acceptablePages = acceptablePages;
        }else {
            String[] defaultPages = { defaultIndex };
            this.acceptablePages = defaultPages;
        }

        //Make sure the request is actually for a directory

    }

    public IndexPageFactory(FileRequest fileRequest) throws Exception
    {
        this(fileRequest, null);


    }

    //Get what ever the index page is
    public File indexPage()
    {
        for
    }
}
