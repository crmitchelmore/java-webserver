package webserver;

import javax.xml.ws.http.HTTPException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cmitchelmore on 11/03/2014.

 */
public class FileRequest {

    protected Path rootDirectory;
    protected Path absolutePath;
    protected URI decodedURI;

    public URI getDecodedURI() {
        return decodedURI;
    }

    public Path getAbsolutePath() {
        return absolutePath;
    }

/*
    public void check(){


        Date lastModified = new Date(98l);

         if ( true ) { // get or head
            if ( !fileExists() ){
                throw new HTTPException(404);//Not Found
            }else if ( lastModified && !isFileModifiedSince(lastModified) ){
                //304 Not modified
            }else if ( !canReadFile() ){
                throw new HTTPException(500);//Can't read file. Maybe should be something else
            }else{
                //200
                //if get
                byte[] bytes = theFile();
                //add header
                String mimeType = mimeType();
                String fileSize = "" + bytes.length;

            }
        }else if ( true ){//put
            byte[] bytes = null;
            createFileOrFolderWithBytes(bytes);
        }
    }

*/



    public FileRequest(String rootDirectory, String uri) throws URISyntaxException, SecurityException //throw new ;//Bad Request
    {
        this.rootDirectory = Paths.get(rootDirectory);
        //The URI is not using the correct syntax .Throws URISyntaxException
        this.decodedURI = new URI(uri);
        this.absolutePath = this.rootDirectory.resolve(this.decodedURI.toString()).normalize();

        //File outside the scope of the server directory. Throws SecurityException
        if ( !this.absolutePath.startsWith(rootDirectory) ){
            throw new SecurityException("Access forbidden");
        }
    }







}
