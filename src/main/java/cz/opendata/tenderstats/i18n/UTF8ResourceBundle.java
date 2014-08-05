package cz.opendata.tenderstats.i18n;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author venca
 */
abstract public class UTF8ResourceBundle extends ResourceBundle {

    final private Properties prop = new Properties();

    public UTF8ResourceBundle() {
        try {
            prop.load(getClass().getResourceAsStream(this.getClass().getSimpleName().toLowerCase() + ".properties"));
        } catch (IOException ex) {
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        try {
            String property = prop.getProperty(key);
            return property == null ? null : new String(property.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return (Enumeration<String>) prop.propertyNames();
    }

}
