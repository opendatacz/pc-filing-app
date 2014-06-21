package cz.opendata.tenderstats.utils;

/**
 *
 * @author venca
 */
public class ServletUtils {
    
    public static String encodeURI(String uri, String... parts) {
        return ServletUtilsImpl.encodeURI(uri, parts);
    }
    
}
