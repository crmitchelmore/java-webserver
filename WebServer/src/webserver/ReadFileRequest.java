package webserver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Date;

/**
 * Created by cmitchelmore on 18/03/2014.
 */
public class ReadFileRequest extends FileRequest{


    public ReadFileRequest(String rootDirectory, String uri) throws URISyntaxException, SecurityException
    {
        super(rootDirectory, uri);
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

}
