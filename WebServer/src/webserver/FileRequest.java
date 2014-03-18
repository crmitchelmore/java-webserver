package webserver;

import javax.xml.ws.http.HTTPException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cmitchelmore on 11/03/2014.

 */
public class FileRequest {

    protected Path rootDirectory;
    protected Path absolutePath;
    protected String decodedURI;





    public FileRequest(String rootDirectory, String uri) throws SecurityException, UnsupportedEncodingException //throw new ;//Bad Request
    {
        this.rootDirectory = Paths.get(rootDirectory);
        uri = uri.substring(1); //Chop the leading
        this.decodedURI = URLDecoder.decode(uri, "UTF-8"); //Remove hex Maybe use ISO-8859-1

        this.absolutePath = this.rootDirectory.resolve(this.decodedURI).normalize();

        //File outside the scope of the server directory. Throws SecurityException
        if ( !this.absolutePath.startsWith(rootDirectory) ){
            throw new SecurityException();
        }
    }

    //Returns a file if the absolute path points to an actual file (not a folder) and is not a sym link. Else returns null
    public byte[] getFileBytes() throws IOException
    {
        boolean isSymbolic = Files.isSymbolicLink(this.absolutePath);

        if ( isSymbolic ){
            return null;
        }

        if ( fileExists() ){
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

            builder.append("<body>\n<h1>");
            builder.append("<a href=\"/\">home</a>");

            Path relativeDirectory = this.rootDirectory.relativize(this.absolutePath);
            Iterator it = relativeDirectory.iterator();
            Path partial = Paths.get("/");
            while( it.hasNext() )
            {
                Path p = (Path)it.next();
                partial = partial.resolve(p);
                if ( it.hasNext() ){
                    builder.append(" / <a href=\""+partial+"\"> "+p.toString()+"</a> ");
                }else {
                    builder.append(" / "+p.toString());
                }

            }

            builder.append("</h1><br>");

            stream = Files.newDirectoryStream(this.absolutePath);


            int absolutePathCount = this.absolutePath.getNameCount();
            for (Path file: stream) {
                Path resolved = file.subpath(absolutePathCount, file.getNameCount());

                Path resolvedTarget = this.rootDirectory.relativize(file);
                String directory = Files.isDirectory(file) ? "Dir: " : "";
                if ( !Files.isSymbolicLink(resolved) ){
                    builder.append(directory + "<a href=\"/" + resolvedTarget.toString() + "\">" + resolved.toString() + "</a><br>");
                }

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
            type = Files.probeContentType(this.absolutePath);
        } catch (IOException x) {
            type = "Unknown";
            x.printStackTrace();
        }
        if ( type == null ){
            type = URLConnection.guessContentTypeFromName(this.decodedURI);
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

    public long fileSize()
    {
        try {
            return Files.size(this.absolutePath);
        }catch (IOException e){
            return 0;
        }
    }




    public void createFileOrFolderWithBytes(byte[] bytes, long maxLength) throws IOException, SecurityException {
        //atomic...

        if ( fileExists() ){
            throw new SecurityException();
        }

        Path decodedURIPath = Paths.get(this.decodedURI);
        int pathComponents = decodedURIPath.getNameCount();
        if ( pathComponents > 1 ){
            Path directoryStructure = decodedURIPath.subpath(0, pathComponents-1);
            Files.createDirectories(this.rootDirectory.resolve(directoryStructure));
        }

        if ( isDirectory() ){//file
            Files.createDirectory(this.absolutePath);
        }else{//folder
            if ( bytes != null ){ //Write max of maxLength
                Files.write(this.absolutePath, bytes);
            }
        }

    }





}
