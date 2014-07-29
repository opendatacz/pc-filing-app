package cz.opendata.tenderstats;

import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.QuerySolution;
import cz.opendata.tenderstats.UserContext.Role;
import cz.opendata.tenderstats.sparql.FetchCondition;
import cz.opendata.tenderstats.utils.UriEncoder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Component which handles user sessions, login, registration.
 *
 * @author Matej Snoha
 */
public class SystemManager extends AbstractComponent {

    private static final long serialVersionUID = -5248667215568933536L;

    /**
     * Checks if supplied username and password represent a valid user and
     * returns his UserContext.
     *
     * @param username
     * @param role
     * @param password
     * @return UserContext of user with specified username and password or null
     * if no such user exist.
     */
    protected UserContext checkLogin(String username, int role, final String password) {
        UserContext.Role userRole = UserContext.Role.CONTRACTING_AUTHORITY;
        for (UserContext.Role r : UserContext.Role.values()) {
            if (r.getId() == role) {
                userRole = r;
            }
        }
        return UserContext.fetchUserByEmailAndRole(username, userRole, new FetchCondition() {

            @Override
            public boolean isValid(QuerySolution qs) {
                String passwordhash = qs.getLiteral("passwordhash").getString();
                String salt = qs.getLiteral("salt").getString();
                return DigestUtils.sha512Hex(password + salt).equals(passwordhash);
            }
        });
    }

    /**
     * Registers a new user.<br>
     * Writes login info and preferences to RDB and creates named graphs in
     * private and public dataspaces.
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
     * @return True if user has been sucessfully registered, false if user is
     * already existed.
     */
    protected boolean register(String username, String password, String businessName, String businessIC, String businessPlace,
            int role, String active, String cpv1, String cpv2, String cpv3) {

        if (!username.matches("\\S+@\\S+\\.\\S+")) {
            return false;
        }

        HashMap<String, Object> sparqlTemplateMap = new HashMap<>();
        String roleName = UserContext.Role.CONTRACTING_AUTHORITY.getName();
        for (UserContext.Role ur : UserContext.Role.values()) {
            if (ur.getId() == role) {
                roleName = ur.getName();
            }
        }
        String namedGraph = config.getPrefix("graph") + roleName + "/" + username;
        sparqlTemplateMap.put("graph-uri", namedGraph);

        if (Sparql.privateQuery(Mustache.getInstance().getBySparqlPath("graph_exists.mustache", sparqlTemplateMap)).execAsk()) {
            return false;
        }

        sparqlTemplateMap.clear();
        sparqlTemplateMap.put("active", "true");
        String beURL;
        if (businessIC != null && !businessIC.trim().isEmpty()) {
            beURL = config.getPrefix("be") + businessIC + "-" + UUID.randomUUID();
        } else {
            beURL = config.getPrefix("be") + UUID.randomUUID();
        }
        sparqlTemplateMap.put("business-entity", beURL);
        sparqlTemplateMap.put("email", username);
        sparqlTemplateMap.put("legal-name", businessName);
        String salt = RandomStringUtils.randomAlphanumeric(128);
        sparqlTemplateMap.put("salt", salt);
        sparqlTemplateMap.put("password-hash", DigestUtils.sha512Hex(password + salt));
        sparqlTemplateMap.put("private-graph", namedGraph);
        sparqlTemplateMap.put("role", (role == 1 ? "pcfapp:contracting-authority" : "pcfapp:bidder"));
        if (businessIC != null && !businessIC.trim().isEmpty()) {
            sparqlTemplateMap.put("ico", businessIC.trim());
        }
        if (role == 2) {
            cpv1 = getConfiguration().getPrefix("cpv") + (cpv1 + "-").substring(0, (cpv1 + "-").indexOf('-'));
            cpv2 = cpv2.isEmpty() ? null : getConfiguration().getPrefix("cpv") + (cpv2 + "-").substring(0, (cpv2 + "-").indexOf('-'));
            cpv3 = cpv3.isEmpty() ? null : getConfiguration().getPrefix("cpv") + (cpv3 + "-").substring(0, (cpv3 + "-").indexOf('-'));
            HashMap<String, Object> cpv = new HashMap<>();
            List<String> cpvs = new LinkedList<>();
            cpvs.add(cpv1);
            if (cpv2 != null) {
                cpvs.add(cpv2);
            }
            if (cpv3 != null) {
                cpvs.add(cpv3);
            }
            cpv.put("cpvs", cpvs);
            sparqlTemplateMap.put("cpv", cpv);
            if (businessPlace != null && !businessPlace.trim().isEmpty()) {
                sparqlTemplateMap.put("location", businessPlace.trim());
            }
        }

        Sparql.privateUpdate(Mustache.getInstance().getBySparqlPath("create_business_entity.mustache", sparqlTemplateMap)).execute();

        return true;
    }

    public boolean updateUserPreference(UserContext uc, String preference, String value) {
        return true;
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
                if (!allDefined(request.getParameter("username"), request.getParameter("role"), request.getParameter("password"))) {
                    response.sendError(400);
                    return;
                }
                Integer role;
                try {
                    role = Integer.valueOf(request.getParameter("role"));
                } catch (NumberFormatException ex) {
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
                    uc = checkLogin(request.getParameter("username"), role, request.getParameter("password"));
                    if (uc == null) {
                        if (allDefined(request.getParameter("forward-if-fail"))) {
                            try {
                                response.sendRedirect(UriEncoder.apply(request.getParameter("forward-if-fail")).part("m").encode());
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
                        Integer.valueOf(request.getParameter("role")),
                        "1", // active
                        request.getParameter("cpv1"),
                        request.getParameter("cpv2"),
                        request.getParameter("cpv3"))) {
                    response.getWriter().println("User created.");
                } else {
                    if (allDefined(request.getParameter("forward-if-fail"))) {
                        try {
                            response.sendRedirect(UriEncoder.apply(request.getParameter("forward-if-fail")).part("m").encode());
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

            case "updateAccount":
                uc = getUserContext(request);
                if (uc != null && allDefined(request.getParameter("cpv1"), request.getParameter("businessName"), request.getParameter("businessPlace"))) {

                    String cpv1 = (String) request.getParameter("cpv1");
                    String cpv2 = (String) request.getParameter("cpv2");
                    String cpv3 = (String) request.getParameter("cpv3");
                    String businessName = (String) request.getParameter("businessName");
                    String businessPlace = (String) request.getParameter("businessPlace");
                    String businessIC = (String) request.getParameter("businessIC");

                    HashMap<String, Object> sparqlTemplateMap = new HashMap<>();
                    sparqlTemplateMap.put("private-graph", uc.getNamedGraph());
                    sparqlTemplateMap.put("business-entity", uc.getPreference("businessEntity"));
                    sparqlTemplateMap.put("legal-name", businessName);
                    if (businessIC != null && !businessIC.trim().isEmpty()) {
                        sparqlTemplateMap.put("ico", businessIC.trim());
                    }
                    if (uc.getRole().equals(Role.BIDDER)) {
                        cpv1 = getConfiguration().getPrefix("cpv") + (cpv1 + "-").substring(0, (cpv1 + "-").indexOf('-'));
                        cpv2 = cpv2.isEmpty() ? null : getConfiguration().getPrefix("cpv") + (cpv2 + "-").substring(0, (cpv2 + "-").indexOf('-'));
                        cpv3 = cpv3.isEmpty() ? null : getConfiguration().getPrefix("cpv") + (cpv3 + "-").substring(0, (cpv3 + "-").indexOf('-'));
                        HashMap<String, Object> cpv = new HashMap<>();
                        List<String> cpvs = new LinkedList<>();
                        cpvs.add(cpv1);
                        if (cpv2 != null) {
                            cpvs.add(cpv2);
                        }
                        if (cpv3 != null) {
                            cpvs.add(cpv3);
                        }
                        cpv.put("cpvs", cpvs);
                        sparqlTemplateMap.put("cpv", cpv);
                        if (businessPlace != null && !businessPlace.trim().isEmpty()) {
                            sparqlTemplateMap.put("location", businessPlace.trim());
                        }
                    }
                    Sparql.privateUpdate(Mustache.getInstance().getBySparqlPath("update_business_entity.mustache", sparqlTemplateMap)).execute();

                    getUserContext(request, true);
                    
                    JsonObject json = new JsonObject();
                    json.addProperty("success", true);

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
                        json.addProperty(pairs.getKey(), pairs.getValue());
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
                response.sendRedirect(UriEncoder.apply(request.getParameter("forward")).part("m").encode());
            } catch (IllegalStateException unused) {
            }
        }
    }
}
