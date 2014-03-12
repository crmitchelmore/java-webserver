package webserver;

import com.sun.org.apache.regexp.internal.recompile;
import org.apache.http.client.utils.DateUtils;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Date;

/**
 * Created by cmitchelmore on 11/03/2014.
 */
public class FileHandler {

    private Path rootDirectory;
    private Path absolutePath;
    private URI decodedURI;

    public void check(){

        Date lastModified = new Date(98l);
        if ( !isValid() ){
            throw new HTTPException(403);//Forbidden
        }else if ( true ) { // get or head
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


    public FileHandler(String rootDirectory, String uri) throws URISyntaxException { //throw new HTTPException(400);//Bad Request
        this.rootDirectory = Paths.get(rootDirectory);
        //URLDecoder.decode(uri, "ISO-8859-1");
        this.decodedURI = new URI(uri);
        this.absolutePath = this.rootDirectory.resolve(this.decodedURI.toString()).normalize();
    }



    private boolean isValid(){
        return this.absolutePath.startsWith(rootDirectory);
    }

    public boolean fileExists(){
        return Files.exists(this.absolutePath) & Files.isRegularFile(this.absolutePath, LinkOption.NOFOLLOW_LINKS);
    }

    public boolean canReadFile(){
        return Files.isReadable(this.absolutePath);
    }

    public boolean isDirectory(){
        return Files.isDirectory(this.absolutePath);
    }

    private boolean hasIndex(){
        if ( isDirectory() ){
            Path indexPath = this.absolutePath.resolve("index.html");
            return Files.exists(indexPath) & Files.isRegularFile(indexPath, LinkOption.NOFOLLOW_LINKS) & Files.isReadable(indexPath);

        }
        return false;
    }

    public void createFileOrFolderWithBytes(byte[] bytes) throws IOException{
        //atomic...
        Path decodedURIPath = Paths.get(this.decodedURI);
        int pathComponents = decodedURIPath.getNameCount();
        if ( pathComponents > 1 ){
            Path directoryStructure = decodedURIPath.subpath(0, pathComponents-1);
            Files.createDirectories(this.rootDirectory.resolve(directoryStructure));
        }
        if ( true ){//file
            if ( bytes != null ){
                Files.write(this.absolutePath, bytes);
            }
        }else{//folder
            Files.createDirectory(this.absolutePath);
        }

    }

    public byte[] theFile() throws IOException{
        if ( isDirectory() ){
            if ( hasIndex() ){
                return Files.readAllBytes(this.absolutePath.resolve("index.html"));
            }else{
                return directoryStructure().getBytes();
            }
        }
        return Files.readAllBytes(this.absolutePath);
    }

    public boolean isFileModifiedSince(Date dateLastModified) throws IOException{
        FileTime lastModifiedFileTime = Files.getLastModifiedTime(this.absolutePath);
        Date lastModifiedFileDate = new Date(lastModifiedFileTime.toMillis());
        return lastModifiedFileDate.compareTo(dateLastModified) > 0; //Returns True if lastModifiedFileDate is after dateLastModified
    }


    public String mimeType(){
        String type = "text/html";
        try {

            if ( !isDirectory() ){
                type = Files.probeContentType(this.absolutePath);
            }

            if (type == null) {
              type = "unknown";
            }

        } catch (IOException x) {
            System.err.println(x);
        }
        return type;
    }


    public String directoryStructure() throws IOException{
        DirectoryStream<Path> stream = null;
        String s = null;
        try {
            StringBuilder builder = new StringBuilder("<html>\n<head><title>" + this.decodedURI + "</title></head>\n");

            builder.append("<body>\n<h1>" + this.decodedURI + "</h1><br>");

            stream = Files.newDirectoryStream(this.absolutePath);
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
