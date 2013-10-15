package cz.opendata.tenderstats;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.google.gson.JsonObject;
//import com.google.gson.Gson;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * Component which handles user sessions, login, registration.
 * 
 * @author Matej Snoha
 */
public class SystemManager extends AbstractComponent {

	private static final long serialVersionUID = -5248667215568933536L;

	/**
	 * Checks if supplied username and password represent a valid user and returns his UserContext.
	 * 
	 * @param username
	 * @param password
	 * @return UserContext of user with specified username and password or null if no such user exist.
	 * @throws ServletException
	 *             If connection to database server failed.
	 */
	protected UserContext checkLogin(String username, String password) throws ServletException {

		try (Connection con =
				DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
						config.getRdbUsername(),
						config.getRdbPassword())) {
			PreparedStatement pst =
					con.prepareStatement("SELECT username, passwordhash, salt, role "
							+ "FROM users WHERE username=? AND active='1'");
			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();
			if (!rs.first()) {
				return null;
			}

			String passwordhash = rs.getString("passwordhash");
			String salt = rs.getString("salt");
			if (!DigestUtils.sha512Hex(password + salt).equals(passwordhash)) {
				return null;
			}

			UserContext uc = new UserContext();
			uc.setUserName(rs.getString("username"));
			uc.setRole(rs.getInt("role"));

			pst = con.prepareStatement("SELECT preference, value FROM user_preferences WHERE username=?");
			pst.setString(1, username);
			rs = pst.executeQuery();
			while (rs.next()) {
				uc.setPreference(rs.getString("preference"), rs.getString("value"));
			}
			uc.setNamedGraph(uc.getPreference("namedGraph"));
			return uc;
		} catch (SQLException e) {
			throw new ServletException("Error validating username and password", e);
		}
	}

	/**
	 * Registers a new user.<br>
	 * Writes login info and preferences to RDB and creates named graphs in private and public dataspaces.
	 * 
	 * @param username
	 * @param password
	 * @param businessName
	 * @param businessIC
	 * @param businessPlace
	 * @param role
	 * @param active
	 * @param cpv1
	 * @param cpv2
	 * @param cpv3
	 * @return True if user has been sucessfully registered, false if user is already existed.
	 * @throws ServletException
	 *             If connection to database server failed.
	 */
	protected boolean register(String username, String password, String businessName, String businessIC, String businessPlace,
			String role, String active, String cpv1, String cpv2, String cpv3) throws ServletException {

		try (Connection con =
				DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
						config.getRdbUsername(),
						config.getRdbPassword())) {
			PreparedStatement pst = con.prepareStatement("SELECT username FROM users WHERE username=?");
			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();
			if (rs.first()) {
				return false;
			} else {
				String salt = RandomStringUtils.randomAlphanumeric(128);
				pst =
						con.prepareStatement("INSERT INTO users (username, passwordhash, salt, role, active) "
								+ "VALUES (?, ?, ?, ?, ?)");
				pst.setString(1, username);
				pst.setString(2, DigestUtils.sha512Hex(password + salt));
				pst.setString(3, salt);
				pst.setString(4, role);
				pst.setString(5, active);
				pst.executeUpdate();

				pst = con.prepareStatement("INSERT INTO user_preferences (username, preference, value) " + "VALUES (?, ?, ?)");
				String namedGraph = config.getPreference("newNamedGraphURL") + username;
				pst.setString(1, username);
				pst.setString(2, "namedGraph");
				pst.setString(3, namedGraph);
				pst.executeUpdate();

				if (businessIC != null && !businessIC.trim().isEmpty()) {
					pst.setString(2, "businessIC");
					pst.setString(3, businessIC);
					pst.executeUpdate();
				}

				pst.setString(2, "businessName");
				pst.setString(3, businessName);
				pst.executeUpdate();

				if (role.equals("2")) {

					pst.setString(2, "businessPlace");
					pst.setString(3, businessPlace);
					pst.executeUpdate();

					if (cpv1 != null && !cpv1.isEmpty()) {
						pst.setString(2, "cpv1");
						pst.setString(3, (cpv1 + "-").substring(0, (cpv1 + "-").indexOf('-')));
						pst.executeUpdate();
					}
					if (cpv2 != null && !cpv2.isEmpty()) {
						pst.setString(2, "cpv2");
						pst.setString(3, (cpv2 + "-").substring(0, (cpv2 + "-").indexOf('-')));
						pst.executeUpdate();
					}
					if (cpv3 != null && !cpv3.isEmpty()) {
						pst.setString(2, "cpv3");
						pst.setString(3, (cpv3 + "-").substring(0, (cpv3 + "-").indexOf('-')));
						pst.executeUpdate();
					}
				}

				String beURL;
				if (businessIC != null && !businessIC.trim().isEmpty()) {
					beURL = config.getPreference("newBusinessEntityURL") + businessIC + "-" + UUID.randomUUID();
				} else {
					beURL = config.getPreference("newBusinessEntityURL") + UUID.randomUUID();
				}
				pst.setString(2, "businessEntity");
				pst.setString(3, beURL);
				pst.executeUpdate();

				/* @formatter:off */
				UpdateRequest request = UpdateFactory.create(
						config.getPreference("prefixes") +
						"INSERT DATA " +
						"{ " +
						"	GRAPH <" + namedGraph +"> { " +
						"		<" + beURL + ">			dc:title				\"" + businessName + "\"@en ;" +
						"								a 						gr:BusinessEntity ." +
						"	} " +
						"}");
				/* @formatter:on */

				System.out.println("###################################################");
				System.out.println(request);

				UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
				upr.execute();

				/* @formatter:off */
				request = UpdateFactory.create(
						config.getPreference("prefixes") +
						"INSERT DATA " +
						"{ " +
						"	GRAPH <" + config.getPreference("publicGraphName") +"> { " +
						"		<" + beURL + ">			dc:title				\"" + businessName + "\"@en ;" +
						"								a 						gr:BusinessEntity ." +
						"	} " +
						"}");
				/* @formatter:on */

				System.out.println("###################################################");
				System.out.println(request);

				upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
				upr.execute();

				return true;
			}
		} catch (SQLException e) {
			throw new ServletException("Error registering new user", e);
		}
	}

	public boolean updateUserPreference(UserContext uc, String preference, String value) {

		if (!preference.matches("user.*"))
			return false;

		try (Connection con =
				DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
						config.getRdbUsername(),
						config.getRdbPassword())) {
			PreparedStatement st;
			if (!uc.containsPreference(preference)) {
				st = con.prepareStatement("INSERT INTO user_preferences VALUES ( ? , ? , ? )");
				st.setString(1, uc.getUserName());
				st.setString(2, preference);
				st.setString(3, value);
			} else {
				st = con.prepareStatement("UPDATE user_preferences SET value = ? WHERE username = ? AND preference = ? ");
				st.setString(1, value);
				st.setString(2, uc.getUserName());
				st.setString(3, preference);
			}

			int res = st.executeUpdate();

			System.out.println(res);

			if (res > 0) {
				uc.setPreference(preference, value);
				return true;
			} else
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Provides RESTful service via HTTP GET and POST.<br>
	 * <br>
	 * <b>User login:</b><br>
	 * action=login, specify username and password<br>
	 * <b>User logout:</b><br>
	 * action=logout<br>
	 * <b>User registration:</b><br>
	 * action=register, specify username password, businessName and role<br>
	 * <b>Get user name of logged in user:</b><br>
	 * action=getuser<br>
	 * <b>Reload user preferences stored session from database:</b><br>
	 * action=reload
	 */
	@Override
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null) {
			response.sendError(400);
			return;
		}
		HttpSession session;
		UserContext uc;

		switch (action) {

			case "login":
				if (!allDefined(request.getParameter("username"), request.getParameter("password"))) {
					response.sendError(400);
					return;
				}
				session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				session = request.getSession(true);
				synchronized (session) {
					session.setMaxInactiveInterval(Integer.parseInt(config.getPreference("sessionTimeout")));
					uc = checkLogin(request.getParameter("username"), request.getParameter("password"));
					if (uc == null) {
						if (allDefined(request.getParameter("forward-if-fail"))) {
							try {
								response.sendRedirect(request.getParameter("forward-if-fail"));
							} catch (IllegalStateException unused) {
							}
						} else {
							response.sendError(403, "Username and password do not match.");
						}
					}
					session.setAttribute("UserContext", uc);
				}
				response.getWriter().println("User logged in.");
				break;

			case "logout":
				session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				response.getWriter().println("User logged out.");
				break;

			case "register":
				if (!allDefined(request.getParameter("username"),
						request.getParameter("password"),
						request.getParameter("businessName"),
						request.getParameter("role"))) {
					response.sendError(400);
					return;
				}
				if (register(request.getParameter("username"),
						request.getParameter("password"),
						request.getParameter("businessName"),
						request.getParameter("businessIC"),
						request.getParameter("businessPlace"),
						request.getParameter("role"),
						"1", // active
						request.getParameter("cpv1"),
						request.getParameter("cpv2"),
						request.getParameter("cpv3"))) {
					response.getWriter().println("User created.");
				} else {
					if (allDefined(request.getParameter("forward-if-fail"))) {
						try {
							response.sendRedirect(request.getParameter("forward-if-fail"));
						} catch (IllegalStateException unused) {
						}
					} else {
						response.sendError(409, "Username already exists.");
					}
				}
				break;

			// case "dump": // TODO this is only for testing
			// Gson gson = new Gson();
			// uc = getUserContext(request);
			// if (uc != null) {
			// response.getWriter().println(gson.toJson(uc));
			// } else {
			// response.getWriter().println("No user logged in.");
			// }
			// break;

			case "getuser":
				uc = getUserContext(request);
				if (uc != null) {
					response.getWriter().println('"' + uc.getUserName() + '"');
				}
				break;

			case "updateUserPreference":
				uc = getUserContext(request);
				if (uc != null && allDefined(request.getParameter("preference"), request.getParameter("value"))) {

					String preference = (String) request.getParameter("preference");
					String value = (String) request.getParameter("value");

					JsonObject json = new JsonObject();

					json.addProperty("success", updateUserPreference(uc, preference, value));

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(json.toString());
				} else {
					response.sendError(400);
				}
				break;

			case "getUserPreferences":
				uc = getUserContext(request);
				if (uc != null) {
					JsonObject json = new JsonObject();

					// user details
					json.addProperty("username", uc.getUserName());

					// user settings
					Iterator<Map.Entry<String, String>> users = uc.getPreferences().entrySet().iterator();
					while (users.hasNext()) {
						Map.Entry<String, String> pairs = (Map.Entry<String, String>) users.next();
						if (pairs.getKey().matches("user.*")) {
							json.addProperty(pairs.getKey(), pairs.getValue());
						}
					}
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(json.toString());
				} else {
					response.sendError(400);
				}
				break;

			case "reload":
				uc = getUserContext(request, true);
				session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				session = request.getSession(true);
				session.setAttribute("UserContext", uc);
				response.getWriter().println("User preferences and cached data were reloaded.");
				break;

			default:
				response.sendError(400);
				break;
		}
		if (allDefined(request.getParameter("forward")) && !response.isCommitted()) {
			try {
				response.sendRedirect(request.getParameter("forward"));
			} catch (IllegalStateException unused) {
			}
		}
	}
}
