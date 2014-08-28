package cz.opendata.tenderstats;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cz.opendata.tenderstats.pcfapp.PCFappModelContract;

/**
 * Component for contract data retrieval
 * 
 * @author Ivan Kosdy
 */
public class ContractsComponent extends AbstractComponent {

	private static final long serialVersionUID = 6483888315801007529L;

	private PCFappModelContract modelContract = null;

	@Override
	public void init() throws ServletException {
		super.init();
		modelContract = new PCFappModelContract(config);
	}

	@Override
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String action = request.getParameter("action");
		String contractURI = request.getParameter("contractURI");

		if (!allDefined(contractURI, action)) {
			response.sendError(400);
			return;
		}

		boolean logged = isUserLoggedIn(request);

		JsonObject json = new JsonObject();

		switch (action) {
			case "getPublicContract":
				try {
					json.add("data", getPublicContract(contractURI));
				} catch (NullPointerException e) {
				}
				break;
				
			case "getPublicContractNew":
				try {					
					json.add("data", getPublicContractNew(contractURI));
					json.addProperty("success", true);
				} catch (NullPointerException e) {
				}
				break;

			case "getPrivateContract":
				if (!logged) {
					response.sendError(401);
					return;
				}
				UserContext uc = getUserContext(request);
				String graphURI = uc.getNamedGraph();
				try {
					json.add("data", getPrivateContract(contractURI, graphURI));
				} catch (NullPointerException e) {
				}
				break;

			default:
				response.sendError(404);
				return;
		}

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(json);

	}

	private JsonParser parser = new JsonParser();
	
	private JsonObject getPublicContractNew(String contractURI) {

		StringWriter sW = new StringWriter();					
		modelContract.getPublicContract(contractURI).write( sW , "RDF/JSON");		
		return (JsonObject) parser.parse(sW.toString());
	}
	
	private JsonObject getPublicContract(String contractURI) {
		return modelContract.getPublicContractAsJson(modelContract.getPublicContract(contractURI), contractURI);
	}

	private JsonObject getPrivateContract(String contractURI, String graphURI) {
		return modelContract.getContractAsJson(contractURI, graphURI);
	}

}
