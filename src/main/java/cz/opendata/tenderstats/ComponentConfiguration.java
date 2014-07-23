package cz.opendata.tenderstats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds component configuration needed for network communication with databases
 * and other values.
 *
 * @author Matej Snoha
 */
public class ComponentConfiguration implements Serializable {

    private static final long serialVersionUID = 4731162717682995972L;

    /**
     * Address for querying SPARQL endpoint for private dataspace
     */
    private String sparqlPrivateQuery;

    /**
     * Address for updating SPARQL endpoint for private dataspace
     */
    private String sparqlPrivateUpdate;

    /**
     * Address for querying SPARQL endpoint for public dataspace
     */
    private String sparqlPublicQuery;

    /**
     * Address for updating SPARQL endpoint for private dataspace
     */
    private String sparqlPublicUpdate;

    /**
     * JDBC connection string to access relational database
     */
    private String rdbAddress;

    /**
     * Name of the relational database
     */
    private String rdbDatabase;

    /**
     * Username to access relational database
     */
    private String rdbUsername;

    /**
     * Password to access relational database
     */
    private String rdbPassword;

    /**
     * Component preferences
     */
    private HashMap<String, String> preferences = new HashMap<>();
    private HashMap<String, String> prefixes = new HashMap<>();

    public void setPrefix(String id, String uri) {
        prefixes.put(id, uri);
    }

    public String getPrefix(String id) {
        return prefixes.get(id);
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    /**
     * Checks if there is a preference with specified name.
     *
     * @param name
     * @return True if there is a preference with specified name.
     * @see HashMap#containsKey(Object)
     */
    public boolean containsPreference(String name) {
        return preferences.containsKey(name);
    }

    /**
     * @param name
     * @return the value of preference with specified name or null if such
     * preference doesn't exist.
     */
    public String getPreference(String name) {
        return preferences.get(name);
    }

    /**
     * @return Component preferences.
     */
    public HashMap<String, String> getPreferences() {
        return preferences;
    }

    /**
     * @return JDBC connection string to access relational database
     */
    public String getRdbAddress() {
        return rdbAddress;
    }

    /**
     * @return Name of the relational database
     */
    public String getRdbDatabase() {
        return rdbDatabase;
    }

    /**
     * @return Password to access relational database
     */
    public String getRdbPassword() {
        return rdbPassword;
    }

    /**
     * @return Username to access relational database
     */
    public String getRdbUsername() {
        return rdbUsername;
    }

    /**
     * @return Address for querying SPARQL endpoint for private dataspace
     */
    public String getSparqlPrivateQuery() {
        return sparqlPrivateQuery;
    }

    /**
     * @return Address for updating SPARQL endpoint for private dataspace
     */
    public String getSparqlPrivateUpdate() {
        return sparqlPrivateUpdate;
    }

    /**
     * @return Address for querying SPARQL endpoint for public dataspace
     */
    public String getSparqlPublicQuery() {
        return sparqlPublicQuery;
    }

    /**
     * @return Address for updating SPARQL endpoint for private dataspace
     */
    public String getSparqlPublicUpdate() {
        return sparqlPublicUpdate;
    }

    /**
     * Sets a preference with specified name and value. Overwrites if needed.
     *
     * @param name
     * @param value
     */
    public void setPreference(String name, String value) {
        preferences.put(name, value);
    }

    /**
     * Sets component preferences.
     *
     * @param preferences Component preferences
     */
    public void setPreferences(HashMap<String, String> preferences) {
        this.preferences = preferences;
    }

    /**
     * @param rdbAddress JDBC connection string to access relational database
     */
    public void setRdbAddress(String rdbAddress) {
        this.rdbAddress = rdbAddress;
    }

    /**
     * @param rdbDatabase Name of the relational database
     */
    public void setRdbDatabase(String rdbDatabase) {
        this.rdbDatabase = rdbDatabase;
    }

    /**
     * @param rdbPassword Password to access relational database
     */
    public void setRdbPassword(String rdbPassword) {
        this.rdbPassword = rdbPassword;
    }

    /**
     * @param rdbUsername Username to access relational database
     */
    public void setRdbUsername(String rdbUsername) {
        this.rdbUsername = rdbUsername;
    }

    /**
     * @param sparqlPrivateQuery Address for querying SPARQL endpoint for
     * private dataspace
     */
    public void setSparqlPrivateQuery(String sparqlPrivateQuery) {
        this.sparqlPrivateQuery = sparqlPrivateQuery;
    }

    /**
     * @param sparqlPrivateUpdate Address for updating SPARQL endpoint for
     * private dataspace
     */
    public void setSparqlPrivateUpdate(String sparqlPrivateUpdate) {
        this.sparqlPrivateUpdate = sparqlPrivateUpdate;
    }

    /**
     * @param sparqlPublicQuery Address for querying SPARQL endpoint for public
     * dataspace
     */
    public void setSparqlPublicQuery(String sparqlPublicQuery) {
        this.sparqlPublicQuery = sparqlPublicQuery;
    }

    /**
     * @param sparqlPublicUpdate Address for updating SPARQL endpoint for
     * private dataspace
     */
    public void setSparqlPublicUpdate(String sparqlPublicUpdate) {
        this.sparqlPublicUpdate = sparqlPublicUpdate;
    }

}
