package cz.opendata.tenderstats;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import cz.opendata.tenderstats.AbstractComponent;
import cz.opendata.tenderstats.pcfapp.PCFappModel;
import cz.opendata.tenderstats.pcfapp.PCFappModelOntology;

/**
 * Component which handles all activities with HTML generated forms and saved specifications.
 * 
 * @author Patrik Kompus
 */
@MultipartConfig
public class FormApp extends AbstractComponent {

	private static final long serialVersionUID = 8835885186029723439L;

	private PCFappModelOntology modelOntology;
	JsonObject json = new JsonObject();

	@Override
	public void init() throws ServletException {
		super.init();
		modelOntology = new PCFappModelOntology(config);
	}
	
	/**
	 * Returns the form for specified cpv and contractURI
	 * 
	 */
	public void loadForm(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		if (!allDefined(request.getParameter("cpv"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("contractURI"))) {
			response.sendError(400);
			return;
		}
		session.setAttribute("cpv", request.getParameter("cpv"));
		session.setAttribute("contractURI", request.getParameter("contractURI"));
		String cpv = ((String) session.getAttribute("cpv")).substring(30);
		
		JsonObject ontologyJson = modelOntology.getOntologyByCpv(cpv, "http://ld.opendata.cz/tenderstats/namedgraph/admin");

		JsonObject jsonForm = modelOntology.getFormByCpv(cpv,
				"http://ld.opendata.cz/tenderstats/namedgraph/admin");
		if (jsonForm != null) {
			jsonForm.addProperty("cpv",
					"<input type=\"hidden\" name=\"cpv\" id=\"cpv\" value=\""
							+ cpv + "\"/>");
			jsonForm.addProperty("ontologyURI", ontologyJson.get("ontologyURI").getAsString());
		}

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(jsonForm);
	}

	/**
	 * Returns JSON with cpv and contractURI received in request
	 * 
	 */
	public void getCPV(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		json.addProperty("cpv", (String) session.getAttribute("cpv"));
		json.addProperty("contractURI",
				(String) session.getAttribute("contractURI"));
		session.removeAttribute("cpv");
		session.removeAttribute("contractURI");
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(json.toString());
	}

	/**
	 * Returns JSON with specification data
	 * 
	 */
	public void viewSpecification(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		if (!allDefined(request.getParameter("specificationURI"))) {
			response.sendError(400);
			return;
		}

		session.setAttribute("specURI",
				request.getParameter("specificationURI"));
		session.setAttribute("contractURI", request.getParameter("contractURI"));

		JsonObject jsonSpec = modelOntology.getSpecificationAsJson(
				request.getParameter("specificationURI"),
				getUserContext(request).getNamedGraph());

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(jsonSpec);
	}
	
	/**
	 * Calls modelOntology method addSpecification
	 * 
	 */
	public void addSpecification(HttpServletRequest request,
			HttpServletResponse response)
			throws IOException {
		if (!allDefined(request.getParameter("contractURI"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("root"))) {
			response.sendError(400);
			return;
		}	
		if (!allDefined(request.getParameter("cpv"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}
		String contractURL = URLDecoder.decode(
				request.getParameter("contractURI"), "UTF-8").toString();
		String answer =  modelOntology.addSpecification(getUserContext(request),
				contractURL, request.getParameter("cpv"), request.getParameter("root"), request.getParameter("ontologyURI"));
		
		response.setContentType("text/plain; charset=UTF-8");			
		response.getWriter().println(answer);
	}

	/**
	 * Calls modelOntology method deleteSpecification
	 * 
	 */
	public void deleteSpecification(HttpServletRequest request,
			HttpServletResponse response)
			throws IOException {
		if (!allDefined(request.getParameter("specificationURI"))) {
			response.sendError(400);
			return;
		}
		String specificationURL = URLDecoder.decode(
				request.getParameter("specificationURI"), "UTF-8").toString();
		
		modelOntology.deleteSpecification(getUserContext(request), specificationURL);

	}
	
	/**
	 * Provides RESTful service via HTTP GET and POST.<br>
	 * <br>
	 * <b>List user's private events:</b><br>
	 * action=getPrivateContracts, specify indexes from and to<br>
	 * <b>Get user's private events pagecount:</b><br>
	 * action=getPrivateContractsPages<br>
	 * <br>
	 * <b>List user's calls for tenders:</b><br>
	 * action=getCallsForTenders, specify indexes from and to<br>
	 * <b>Get user's calls for tenders pagecount:</b><br>
	 * action=getCallsForTendersPages<br>
	 * <br>
	 * <b>Add new event:</b><br>
	 * action=addPrivateContract, specify title<br>
	 * <br>
	 * <b>Delete private event:</b><br>
	 * action=deletePrivateContract, specify contractURL<br>
	 * <br>
	 * <b>Withdraw public event:</b><br>
	 * action=cancelContract, specify contractURL<br>
	 * <br>
	 * <b>Publish private event:</b><br>
	 * action=publishPrivateContract, specify contractURL<br>
	 */
	@Override
	protected void doGetPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		if (isUserLoggedIn(request)) {
			String action = request.getParameter("action");
			if (action == null) {
				response.sendError(400);
				return;
			}

			HttpSession session = request.getSession(false);

			switch (action) {
			case "loadForm":
				this.loadForm(request, response, session);
				break;
			case "getCPV":
				this.getCPV(request, response, session);
				break;
			case "viewSpecification":
				this.viewSpecification(request, response, session);
				break;
			case "deleteSpecification":
				this.deleteSpecification(request, response);
				break;				
			case "addSpecification":
				this.addSpecification(request, response);
				break;
			default:
				response.sendError(400);
				break;
			}

			if (allDefined(request.getParameter("forward"))
					&& !response.isCommitted()) {
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
