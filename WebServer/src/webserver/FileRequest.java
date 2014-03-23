package webserver;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by cmitchelmore on 11/03/2014.

 */
public class FileRequest {

    protected Path rootDirectory;
    protected Path absolutePath;
    protected String decodedURI;
    protected String[] indexExtensions = {"index.html"};

    public FileRequest(String rootDirectory, String uri) throws SecurityException //throw new ;//Bad Request
    {
        this.rootDirectory = Paths.get(rootDirectory);
        this.decodedURI = uri;
        this.absolutePath = this.rootDirectory.resolve(this.decodedURI).normalize();

        //File outside the scope of the server directory. Throws SecurityException
        if ( !this.absolutePath.startsWith(rootDirectory) ){
            throw new SecurityException();
        }
    }

    //REturns yes if there is some kind of content avaialble
    public boolean hasContent()
    {
        try {
            byte[] bytes = fileBytes();
            return bytes != null && bytes.length > 0;
        }catch (IOException e){
            return false;
        }
    }

    public byte[] fileBytes() throws IOException
    {
        boolean isSymbolic = Files.isSymbolicLink(this.absolutePath);

        if ( isSymbolic ){
            return null;
        }

        if ( fileExists() ){
            return Files.readAllBytes(this.absolutePath);
        }

        if ( isDirectory() ){
            File indexHTML = this.indexPage();
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
        File f = indexPage();
        // If we are using the index HTML page get the last modified date of that
        if ( f != null ){
            return new Date(f.lastModified());
        }else if ( fileExists() || isDirectory() ){
            // Otherwise return the last mofied date for the file or folder
            return new Date(new File(this.absolutePath.toUri()).lastModified());
        }
        return null;
    }


    public long fileSize()
    {
        try {
            byte[] bytes = fileBytes();
            return bytes.length; //Should find a better way to do this
        }catch (IOException e){
            return 0;
        }
    }


    // The byte array will all be written to file. Any management of file size should be done before calling this mehtod.
    public void createFileOrFolderWithBytes(byte[] bytes) throws IOException, SecurityException
    {

        if ( fileExists() ){
            throw new SecurityException();
        }

        Path decodedURIPath = Paths.get(this.decodedURI);
        int pathComponents = decodedURIPath.getNameCount();

        // Create any parent directories if they don't exist. No error is thrown if they do
        // The parent directory is all path components up to the file name
        if ( pathComponents > 1 ){
            Path directoryStructure = decodedURIPath.subpath(0, pathComponents-1);
            Files.createDirectories(this.rootDirectory.resolve(directoryStructure));
        }

        if ( isDirectory() ){// Create a directory
            Files.createDirectory(this.absolutePath);
        }else{
            if ( bytes != null ){ //Write max of maxLength
                //Use the CREATE_NEW option for atomic file creation
                Files.write(this.absolutePath, bytes, StandardOpenOption.CREATE_NEW);
            }
        }

    }


    public String mimeType()
    {
        File file = indexPage();
        String type = null;

        // If it's a directory and there's no index page then it's the html we build
        if ( isDirectory() && file == null ){
            return "text/html";
        }

        try {
            // If we have index page then probe its path else probe our absolute path
            Path path = file == null ? this.absolutePath : Paths.get(file.getAbsolutePath());
            type = Files.probeContentType(path);

        } catch (IOException x) {
            x.printStackTrace();
        }
        if ( type == null ){
            // If we haven't had any luck so far try guessing the content type
            String fileName = file == null ? this.decodedURI : file.getPath();
            type = URLConnection.guessContentTypeFromName(fileName);
        }
        return type;
    }


    public void delete() throws IOException
    {
        Files.delete(this.absolutePath);
    }

//This is currently only used by logger so no protection is given
    public boolean appendLineToFile(String line)
    {
        try {
            Files.write(this.absolutePath, line.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e){
            return false;
        }
        return true;
    }


    // Search through the index extensions and return the first that is found or null.
    private File indexPage()
    {
        for ( String indexExtension : this.indexExtensions ){
            File file = fileFromCurrentDirectoryWithPathExtension(indexExtension);
            if ( file != null ){
                return file;
            }
        }
        return null;
    }


    private boolean fileExists()
    {
        return Files.exists(this.absolutePath) && Files.isRegularFile(this.absolutePath, LinkOption.NOFOLLOW_LINKS);
    }


    // Compile a directory listing for the direcotyr
    private String directoryStructure() throws IOException
    {
        try (  DirectoryStream<Path> stream = Files.newDirectoryStream(this.absolutePath) ){

            StringBuilder builder = new StringBuilder("<html>\n<head><title>" + this.decodedURI + "</title></head>\n");

            builder.append("<body>\n<h1>\n");

            // Build a bread crumb
            builder.append("<a href=\"/\">home</a>");
            Path relativeDirectory = this.rootDirectory.relativize(this.absolutePath);
            Iterator it = relativeDirectory.iterator();
            Path partial = Paths.get("/");

            while( it.hasNext() ) {
                Path p = (Path)it.next();
                partial = partial.resolve(p);
                if ( it.hasNext() ){
                    builder.append(" / <a href=\""+partial+"\"> "+p.toString()+"</a> ");
                }else {
                    builder.append(" / "+p.toString());
                }
            }
            builder.append("</h1><br>");
            // End of bread crumb


            // Build out directory structure
            int absolutePathCount = this.absolutePath.getNameCount();
            for ( Path file: stream ) {
                Path resolved = file.subpath(absolutePathCount, file.getNameCount());

                Path resolvedTarget = this.rootDirectory.relativize(file);

                // Add 'Dir' for directories
                String directory = Files.isDirectory(file) ? "Dir: " : "";

                // Don't show sym links or hidden files
                if ( !Files.isSymbolicLink(resolved) && !Files.isHidden(resolved) ){
                    builder.append(directory + "<a href=\"/" + resolvedTarget.toString() + "\">" + resolved.toString() + "</a><br>");
                }

            }
            builder.append("</body>\n</html>");

            // Send back everything as a string.
            return builder.toString();
        }

    }


    //If there is a file at the path extension then return it. No sym links.
    private File fileFromCurrentDirectoryWithPathExtension(String pathExtension)
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
