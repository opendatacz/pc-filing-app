package cz.opendata.tenderstats;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementing miscelaneous utilities, such as mail sending.
 * 
 * @author Matej Snoha
 */
public class Utils extends AbstractComponent {

	private static final long serialVersionUID = -1053462839036836365L;

	/**
	 * Provides RESTful service via HTTP GET and POST.<br>
	 * <br>
	 * <b>Send invite notification to seller:</b><br>
	 * action=sendNotification, specify contractName, contractDescription, recipient<br>
	 */
	@Override
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String action = request.getParameter("action");
		if (action == null) {
			response.sendError(400);
			return;
		} else if (isUserLoggedIn(request) || action.equals("geoEnhancer")) {

			switch (action) {

				case "sendNotification":
					if (!allDefined(request.getParameter("contractName"),
							request.getParameter("contractDescription"),
							request.getParameter("recipient"))) {
						response.sendError(400);
						return;
					}

					Mailer mailer =
							new Mailer("\"TenderStats Beta\" <no-reply@xrg15.projekty.ms.mff.cuni.cz>",
									request.getParameter("recipient"),
									"Try out TenderStats – public procurement matchmaker!",
									getUserContext(request).getUserName()
											+ " invited you to participate in the bidding process for a public contract "
											+ request.getParameter("contractName")
											+ " using TenderStats.\r\n\r\n"
											// + "Description: " + request.getParameter("contractDescription") + "\r\n\r\n"
											+ "Try out TenderStats Beta at http://xrg15.projekty.ms.mff.cuni.cz:8080/tenderstats !");

					response.setContentType("application/json; charset=UTF-8");
					if (mailer.send()) {
						response.getWriter().println("true");
					} else {
						response.getWriter().println("false");
					}
					break;

				case "geoEnhancer":

					if (!allDefined(request.getParameter("queryURL"), request.getParameter("updateURL"))) {
						response.sendError(400);
						return;
					}

					System.out.println("[NOTICE] GeoEnhancer start");

					if (allDefined(request.getParameter("submit-add"))) {
						BusinessEntityGeoEnhancer.addMissingGeoPoints(response,
								request.getParameter("queryURL"),
								request.getParameter("queryUsername"),
								request.getParameter("queryPassword"),
								request.getParameter("querySPARQL"),
								request.getParameter("updateURL"),
								request.getParameter("updateUsername"),
								request.getParameter("updatePassword"),
								request.getParameter("graphURL"),
								request.getParameter("geoProvider"),
								request.getParameter("useCache"));
						response.getWriter().println("Work finished. You can close this page now or go back.");
					} else if (allDefined(request.getParameter("submit-n3"))) {
						BusinessEntityGeoEnhancer.downloadN3(response,
								request.getParameter("queryURL"),
								request.getParameter("queryUsername"),
								request.getParameter("queryPassword"),
								request.getParameter("querySPARQL"),
								request.getParameter("geoProvider"),
								request.getParameter("useCache"));
					}

					System.out.println("[NOTICE] GeoEnhancer stop");

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
		} else {
			response.sendError(403, "No user logged in.");
		}
	}
}
