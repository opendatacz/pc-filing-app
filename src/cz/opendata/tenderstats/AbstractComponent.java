package cz.opendata.tenderstats;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Abstract servlet component to be used in cz.opendata.tenderstats project.
 * 
 * @author Matej Snoha
 */
public abstract class AbstractComponent extends HttpServlet {

	private static final long serialVersionUID = -847208829562960899L;

	/**
	 * Holds component configuration needed for network communication with databases and other values.<br>
	 * Needs to be initialized first with value returned by {@link #getConfiguration()} before first use.<br>
	 * If the {@link #init()} method is called, it happens automatically. <br>
	 * <br>
	 * Values stored here apply only for this instance of the servlet and are not automatically synchronized with the relational
	 * database, deployment descriptor or concurrently running instances.
	 */
	protected ComponentConfiguration config;

	/**
	 * Gets component configuration which contains values from deployment descriptor (web.xml) and relational database.
	 * 
	 * @return resulting {@link ComponentConfiguration} object
	 */
	protected ComponentConfiguration getConfiguration() {
		ComponentConfiguration cc = new ComponentConfiguration();
		cc.setComponentName(getClass().getSimpleName());
		if (allDefined(getInitParameter("rdbAddress"),
				getInitParameter("rdbDatabase"),
				getInitParameter("rdbUsername"),
				getInitParameter("rdbPassword"))) {
			cc.setRdbAddress(getInitParameter("rdbAddress"));
			cc.setRdbDatabase(getInitParameter("rdbDatabase"));
			cc.setRdbUsername(getInitParameter("rdbUsername"));
			cc.setRdbPassword(getInitParameter("rdbPassword"));
		} else {
			ServletContext context = getServletContext();
			cc.setRdbAddress(context.getInitParameter("rdbAddress"));
			cc.setRdbDatabase(context.getInitParameter("rdbDatabase"));
			cc.setRdbUsername(context.getInitParameter("rdbUsername"));
			cc.setRdbPassword(context.getInitParameter("rdbPassword"));
		}
		try (Connection con =
				DriverManager.getConnection(cc.getRdbAddress() + cc.getRdbDatabase(), cc.getRdbUsername(), cc.getRdbPassword())) {
			PreparedStatement st =
					con.prepareStatement("SELECT preference, value FROM component_preferences " + "WHERE componentname=?");
			st.setString(1, "all");
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				cc.setPreference(rs.getString("preference"), rs.getString("value"));
			}
			st.setString(1, cc.getComponentName());
			rs = st.executeQuery();
			while (rs.next()) {
				cc.setPreference(rs.getString("preference"), rs.getString("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		cc.setSparqlPrivateQuery(cc.getPreference("sparql_private_query"));
		cc.setSparqlPrivateUpdate(cc.getPreference("sparql_private_update"));
		cc.setSparqlPublicQuery(cc.getPreference("sparql_public_query"));
		cc.setSparqlPublicUpdate(cc.getPreference("sparql_public_update"));
		return cc;
	}

	/**
	 * Servlet initializer.<br>
	 * In derived classes, call to {@code super.init()} initializes static variable {@link #config} with component configuration.
	 */
	@Override
	public void init() throws ServletException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		int i = 0;
		while (i++ < 5) {
			config = getConfiguration();
			if (config == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException unused) {
				}
			} else {
				break;
			}
		}
		if (config == null) {
			throw new UnavailableException("Error getting component configuration from relational database.", 0);
		}
	}

	/**
	 * Called by container before taking servlet out of service. You can use this method to free resources or save state.
	 */
	@Override
	public void destroy() {
	}

	/**
	 * This methods checks if a user is logged in.
	 * 
	 * @return True if a user is logged in.
	 */
	protected boolean isUserLoggedIn(HttpServletRequest request) {
		return getUserContext(request) != null;
	}

	/**
	 * This methods checks if a user is logged in and returns an {@link UserContext} object associated with session
	 * 
	 * @param request
	 *            HTTP Request from user
	 * @return {@link UserContext} associated with logged in user or null if no such user exists.
	 */
	protected UserContext getUserContext(HttpServletRequest request) {
		return getUserContext(request, false);
	}

	/**
	 * This methods checks if a user is logged in and returns an {@link UserContext} object associated with session.<br>
	 * 
	 * @param request
	 *            HTTP Request from user
	 * @param reloadFromRdb
	 *            If set, UserContext in session is first updated from relational database.
	 * 
	 * @return {@link UserContext} associated with logged in user or null if no such user exists.
	 */
	protected UserContext getUserContext(HttpServletRequest request, boolean reloadFromRdb) {
		UserContext uc = null;
		HttpSession session = request.getSession(false);
		if (session != null) {

			uc = (UserContext) session.getAttribute("UserContext");

			if (reloadFromRdb && uc != null) {
				try (Connection con =
						DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
								config.getRdbUsername(),
								config.getRdbPassword())) {
					PreparedStatement pst =
							con.prepareStatement("SELECT preference, value FROM user_preferences WHERE username=?");
					pst.setString(1, uc.getUserName());
					ResultSet rs = pst.executeQuery();
					while (rs.next()) {
						uc.setPreference(rs.getString("preference"), rs.getString("value"));
					}
					uc.setNamedGraph(uc.getPreference("namedGraph"));
					session.setAttribute("UserContext", uc);
				} catch (SQLException unused) {
				}
			}
		}
		return uc;
	}

	/**
	 * Checks if all objects are defined (not null and their string representation is not an empty string)
	 * 
	 * @param objects
	 * @return True if all objects are defined.
	 */
	protected boolean allDefined(Object... objects) {
		boolean check = true;
		for (Object o : objects) {
			check = check && o != null && !o.toString().isEmpty() && !o.toString().equals("null");
		}
		return check;
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGetPost(request, response);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGetPost(request, response);
	}

	/**
	 * Processes GET or POST request and returns response. Override this method to process the input data and return response.<br>
	 * Alternatively, override what you want in the following chains:<br>
	 * Servlet Container --> service --> doGet --> doGetPost<br>
	 * Servlet Container --> service --> doPost --> doGetPost<br>
	 * Servlet Container --> service --> doOptions<br>
	 * ...
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	}
}
