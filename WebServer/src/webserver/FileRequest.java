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

    private Path rootDirectory;
    private Path absolutePath;
    private URI decodedURI;
    private static ConcurrentHashMap<String, Integer> map;

    public void check(){

        Date lastModified = new Date(98l);
        try {

        }catch (URISyntaxException syntaxException){
            throw new HTTPException(400);
        } catch (SecurityException securityException){
            throw new HTTPException(403);
        }
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





    public FileRequest(String rootDirectory, String uri) throws URISyntaxException, SecurityException{ //throw new ;//Bad Request
        this.rootDirectory = Paths.get(rootDirectory);
        //The URI is not using the correct syntax .Throws URISyntaxException
        this.decodedURI = new URI(uri);
        this.absolutePath = this.rootDirectory.resolve(this.decodedURI.toString()).normalize();

        //File outside the scope of the server directory. Throws SecurityException
        if ( !this.absolutePath.startsWith(rootDirectory) ){
            throw new SecurityException("Access forbidden");
        }
    }

    public File getFile(){
        boolean isSymbolic = Files.isSymbolicLink(this.absolutePath);

        boolean isReadable = Files.isReadable(this.absolutePath);
        boolean fileExists = Files.exists(this.absolutePath) && Files.isRegularFile(this.absolutePath, LinkOption.NOFOLLOW_LINKS);

        if ( !isSymbolic ){ //403?
            if ( fileExists ){//Regular file
                if ( isReadable ){ //Only instantaneous check
                    return new File(this.absolutePath.toUri());
                }else {
                    throw new IOException;//??
                }

            }else if ( isDirectory() ){

            }
        }

        //Check if the file has no symbolic links and is not a directory
        if ( fileExists ){
            return new File(this.absolutePath.toUri());
        }
        throw new ;
    }

    public boolean isDirectory()
    {
        return Files.isDirectory(this.absolutePath);
    }


    public File getFileAtPathIfExists(String pathExtension)
    {
        if ( isDirectory() ){
            Path extendedPath = this.absolutePath.resolve(pathExtension);
            boolean exists = Files.exists(extendedPath) && Files.isRegularFile(extendedPath, LinkOption.NOFOLLOW_LINKS);
            if ( exists ){
                return File()
            }
        }
        return null;
    }


    public void createFileOrFolderWithBytes(byte[] bytes) throws IOException{
        //atomic...

        synchronized ( FileRequest.class ){


        }

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
