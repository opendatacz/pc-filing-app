package cz.opendata.tenderstats;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Holds user context information, such as username and user preferences.<br>
 * Calling set* methods currently affects only this instance and changed values are not written back to RDB.
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
	private int role;

	/**
	 * Name of user's graph in private dataspace
	 */
	private String namedGraph;

	/**
	 * User's preferences
	 */
	private HashMap<String, String> preferences = new HashMap<>();

	/**
	 * @return User's username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            Username to set
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
	 * @param preferences
	 *            User's preferences to set
	 */
	public void setPreferences(HashMap<String, String> preferences) {
		this.preferences = preferences;
	}

	/**
	 * @return User's role
	 */
	public int getRole() {
		return role;
	}

	/**
	 * @param role
	 *            User's role to set
	 */
	public void setRole(int role) {
		this.role = role;
	}

	/**
	 * @param name
	 * @return the value of preference with specified name or null if such preference doesn't exist.
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
	 * @param namedGraph
	 *            Name of user's graph in private dataspace to set
	 */
	public void setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
	}
}
