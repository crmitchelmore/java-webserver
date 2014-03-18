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
        uri = uri.substring(1);
        //The URI is not using the correct syntax .Throws URISyntaxException
        this.decodedURI = new URI(uri);
        this.absolutePath = this.rootDirectory.resolve(this.decodedURI.toString()).normalize();

        //File outside the scope of the server directory. Throws SecurityException
        if ( !this.absolutePath.startsWith(rootDirectory) ){
            throw new SecurityException("Access forbidden");
        }
    }

    //Returns a file if the absolute path points to an actual file (not a folder) and is not a sym link. Else returns null
    public byte[] getFileBytes() throws IOException
    {
        boolean isSymbolic = Files.isSymbolicLink(this.absolutePath);

        if ( isSymbolic ){
            return null;
        }

        if ( fileExists() ){ //403?
            return Files.readAllBytes(this.absolutePath);
        }
        if ( isDirectory() ){
            File indexHTML = this.indexHTML();
            if ( indexHTML != null ){
                return Files.readAllBytes(indexHTML.toPath());
            }
            return directoryStructure().getBytes();
        }
        return null;
    }

    //Returns true if the absolute path exists, is a directory and is not a sym link
    public boolean isDirectory()
    {
        return Files.isDirectory(this.absolutePath, LinkOption.NOFOLLOW_LINKS);
    }

    public Date lastModified()
    {
        File f = indexHTML();
        if ( f != null ){
            return new Date(f.lastModified());
        }else if ( fileExists() || isDirectory() ){
            return new Date(new File(this.absolutePath.toUri()).lastModified());
        }
        return null;
    }

    private File indexHTML()
    {
        return this.getFileAtPathIfExists("index.html");
    }

    private boolean fileExists()
    {
        return Files.exists(this.absolutePath) && Files.isRegularFile(this.absolutePath, LinkOption.NOFOLLOW_LINKS);
    }



    private String directoryStructure() throws IOException {
        DirectoryStream<Path> stream = null;
        String s = null;
        try {
            StringBuilder builder = new StringBuilder("<html>\n<head><title>" + this.decodedURI + "</title></head>\n");

            builder.append("<body>\n<h1>" + this.getDecodedURI() + "</h1><br>");

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


    public String mimeType(){
        String type = "text/html";
        File f = indexHTML();
        if ( f != null || isDirectory() ){
            return type;
        }
        try {
            return Files.probeContentType(this.absolutePath);
        } catch (IOException x) {
            System.err.println(x);
        }
        return type;
    }




    //If there is a file at the path extension then return it. No sym links.
    private File getFileAtPathIfExists(String pathExtension)
    {
        if ( isDirectory() ){
            Path extendedPath = this.absolutePath.resolve(pathExtension);
            boolean exists = Files.exists(extendedPath) && Files.isRegularFile(extendedPath, LinkOption.NOFOLLOW_LINKS);
            if ( exists ){
                return new File(extendedPath.toUri());
            }
        }
        return null;
    }






















    public void createFileOrFolderWithBytes(byte[] bytes) throws IOException {
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





}
