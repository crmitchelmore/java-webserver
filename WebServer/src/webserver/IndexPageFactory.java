package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
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
        File indexPage = null;
        for( int i = 0; i < acceptablePages.length; i++ ){
            indexPage = this.fileRequest.getFileAtPathIfExists(acceptablePages[i]);
            if ( indexPage != null ){
                return indexPage;
            }
        }
        return null;
    }


    private String directoryStructure() throws IOException {
        DirectoryStream<Path> stream = null;
        String s = null;
        try {
            StringBuilder builder = new StringBuilder("<html>\n<head><title>" + this.fileRequest.getDecodedURI() + "</title></head>\n");

            builder.append("<body>\n<h1>" + this.fileRequest.getDecodedURI() + "</h1><br>");

            stream = Files.newDirectoryStream( this.fileRequest.getAbsolutePath());
            for (Path file: stream) {
                builder.append("<a href=\"" + file.toString() + "\">" + file.toString() + "</a><br>");

                System.out.println(file.getFileName());
            }

            builder.append("</body>\n</html>");
            s = builder.toString();
        } finally {
            if ( stream != null ){
                stream.close();
            }
        }

        return s;
    }
}
