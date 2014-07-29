package cz.opendata.tenderstats;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import cz.opendata.tenderstats.sparql.FetchCondition;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds user context information, such as username and user preferences.<br>
 * Calling set* methods currently affects only this instance and changed values
 * are not written back to RDB.
 *
 * @author Matej Snoha
 *
 */
public class UserContext implements Serializable {

    private static final long serialVersionUID = 3428808328215503921L;

    /**
     * Username
     */
    private String userName;

    /**
     * User's role
     */
    private Role role;

    /**
     * Name of user's graph in private dataspace
     */
    private String namedGraph;

    /**
     * User's preferences
     */
    private HashMap<String, String> preferences = new HashMap<>();

    public enum Role {

        CONTRACTING_AUTHORITY("contracting-authority", 1),
        BIDDER("bidder", 2);
        private final String name;
        private final int id;

        Role(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

    }

    public static UserContext fetchUserByNamedGraph(String namedGraph) {
        Matcher matcher = Pattern.compile(".+/(.+)/(.+)").matcher(namedGraph);
        if (matcher.matches()) {
            for (Role role : Role.values()) {
                if (role.getName().equals(matcher.group(1))) {
                    return fetchUserByEmailAndRole(matcher.group(2), role, null);
                }
            }
        }
        return null;
    }

    public static UserContext fetchUserByEmailAndRole(String email, Role role, FetchCondition condition) {
        String namedGraph = Config.cc().getPrefix("graph") + role.getName() + "/" + email;
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("private-graph", namedGraph);
        ResultSet sparqlResult = Sparql.privateQuery(Mustache.getInstance().getBySparqlPath("select_business_entity.mustache", scopes)).execSelect();
        if (!sparqlResult.hasNext()) {
            return null;
        }
        QuerySolution businessEntity = sparqlResult.next();
        if (!businessEntity.varNames().hasNext() || condition != null && !condition.isValid(businessEntity)) {
            return null;
        }
        UserContext uc = new UserContext();
        uc.setUserName(email);
        uc.setRole(role);
        uc.setNamedGraph(namedGraph);
        uc.setPreference("businessName", businessEntity.getLiteral("businessName").getString());
        uc.setPreference("businessEntity", businessEntity.getResource("businessEntity").getURI());
        Literal businessIC = businessEntity.getLiteral("businessIC");
        if (businessIC != null) {
            uc.setPreference("businessIC", businessIC.getString());
        }
        Literal businessPlace = businessEntity.getLiteral("businessPlace");
        if (businessPlace != null) {
            uc.setPreference("businessPlace", businessPlace.getString());
        }
        Literal cpvs = businessEntity.getLiteral("cpvs");
        if (cpvs != null) {
            String[] cpvsArray = cpvs.getString().split(",");
            for (int i = 0; i < cpvsArray.length; i++) {
                uc.setPreference("cpv" + (i + 1), cpvsArray[i].replaceFirst(".+/", ""));
            }
        }
        return uc;
    }

    /**
     * @return User's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName Username to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return User's preferences
     */
    public HashMap<String, String> getPreferences() {
        return preferences;
    }

    /**
     * @param preferences User's preferences to set
     */
    public void setPreferences(HashMap<String, String> preferences) {
        this.preferences = preferences;
    }

    /**
     * @return User's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role User's role to set
     */
    public void setRole(Role role) {
        this.role = role;
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
     * Sets a preference with specified name and value. Overwrites if needed.
     *
     * @param name
     * @param value
     */
    public void setPreference(String name, String value) {
        preferences.put(name, value);
    }

    /**
     * Checks if there is a preference with specified name.
     *
     * @param name
     * @return True if there is a preference with specified name.
     */
    public boolean containsPreference(String name) {
        return preferences.containsKey(name);
    }

    /**
     * @return Name of user's graph in private dataspace
     */
    public String getNamedGraph() {
        return namedGraph;
    }

    /**
     * @param namedGraph Name of user's graph in private dataspace to set
     */
    public void setNamedGraph(String namedGraph) {
        this.namedGraph = namedGraph;
    }
}
