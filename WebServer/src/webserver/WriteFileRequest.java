package webserver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by cmitchelmore on 18/03/2014.
 */
public class WriteFileRequest extends FileRequest{



    public WriteFileRequest(String rootDirectory, String uri) throws URISyntaxException, SecurityException
    {
        super(rootDirectory, uri);
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
