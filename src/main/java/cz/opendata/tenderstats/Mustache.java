package cz.opendata.tenderstats;

import com.github.mustachejava.DefaultMustacheFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

/**
 *
 * @author venca
 */
public class Mustache {

    final private static Mustache instance = new Mustache();

    public static Mustache getInstance() {
        return instance;
    }

    public String getByRelativePath(String name, Map<String, Object> scopes) {
        try (StringWriter stringWriter = new StringWriter(); InputStream resourceAsStream = getClass().getResourceAsStream(name)) {
            if (resourceAsStream != null) {
                new DefaultMustacheFactory().compile(new InputStreamReader(resourceAsStream), name).execute(stringWriter, scopes).flush();
            }
            return stringWriter.toString();
        } catch (IOException ex) {
            return "";
        }
    }

    public String getBySparqlPath(String name, Map<String, Object> scopes) {
        return getByRelativePath("/cz/opendata/tenderstats/sparql/" + name, scopes);
    }

}
