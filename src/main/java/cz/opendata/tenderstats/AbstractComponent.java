package cz.opendata.tenderstats;

import java.io.IOException;

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
     * Holds component configuration needed for network communication with
     * databases and other values.<br>
     * Needs to be initialized first with value returned by
     * {@link #getConfiguration()} before first use.<br>
     * If the {@link #init()} method is called, it happens automatically. <br>
     * <br>
     * Values stored here apply only for this instance of the servlet and are
     * not automatically synchronized with the relational database, deployment
     * descriptor or concurrently running instances.
     */
    protected ComponentConfiguration config;

    /**
     * Gets component configuration which contains values from deployment
     * descriptor (web.xml) and relational database.
     *
     * @return resulting {@link ComponentConfiguration} object
     */
    protected ComponentConfiguration getConfiguration() {
        ComponentConfiguration cc = Config.cc();
        return cc;
    }

    /**
     * Servlet initializer.<br>
     * In derived classes, call to {@code super.init()} initializes static
     * variable {@link #config} with component configuration.
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
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
     * Called by container before taking servlet out of service. You can use
     * this method to free resources or save state.
     */
    @Override
    public void destroy() {
    }

    /**
     * This methods checks if a user is logged in.
     *
     * @param request
     * @return True if a user is logged in.
     */
    public boolean isUserLoggedIn(HttpServletRequest request) {
        return getUserContext(request) != null;
    }

    /**
     * This methods checks if a user is logged in and returns an
     * {@link UserContext} object associated with session
     *
     * @param request HTTP Request from user
     * @return {@link UserContext} associated with logged in user or null if no
     * such user exists.
     */
    public UserContext getUserContext(HttpServletRequest request) {
        return getUserContext(request, false);
    }

    /**
     * This methods checks if a user is logged in and returns an
     * {@link UserContext} object associated with session.<br>
     *
     * @param request HTTP Request from user
     * @param reloadFromRdb If set, UserContext in session is first updated from
     * relational database.
     *
     * @return {@link UserContext} associated with logged in user or null if no
     * such user exists.
     */
    protected UserContext getUserContext(HttpServletRequest request, boolean reloadFromRdb) {
        UserContext uc = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            uc = (UserContext) session.getAttribute("UserContext");
            if (reloadFromRdb && uc != null) {
                uc = UserContext.fetchUserByEmailAndRole(uc.getUserName(), uc.getRole(), null);
                session.setAttribute("UserContext", uc);
            }
        }
        return uc;
    }

    /**
     * Checks if all objects are defined (not null and their string
     * representation is not an empty string)
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
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGetPost(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGetPost(request, response);
    }

    /**
     * Processes GET or POST request and returns response. Override this method
     * to process the input data and return response.<br>
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
