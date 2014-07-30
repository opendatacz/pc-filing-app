package cz.opendata.tenderstats.pcfapp;

import cz.opendata.tenderstats.Config;
import cz.opendata.tenderstats.UserContext;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;

/**
 *
 * @author venca
 */
public class Document {
    
    public final static String DOCUMENT_PREFIX = Config.cc().getPrefix("pcfapp") + "document/";
    private final File file;
    private final String name;
    private final String contentType;
    private final UserContext owner;
    
    public Document(File file, String name, String contentType, UserContext owner) {
        this.file = file;
        this.name = name;
        this.contentType = contentType;
        this.owner = owner;
    }
    
    public UserContext getOwner() {
        return owner;
    }
    
    public String getUri() {
        return DOCUMENT_PREFIX + getId();
    }
    
    public String getId() {
        return file.getName();
    }
    
    public File getFile() {
        return file;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void deleteContentFile() throws IOException {
        LogManager.getLogger("Documents").debug("Deleting of document: " + getFile().getAbsolutePath());
        FileUtils.forceDelete(getFile());
    }
    
}
