package cz.opendata.tenderstats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.RiotException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import cz.opendata.tenderstats.AbstractComponent;
import cz.opendata.tenderstats.admin.ClassHierarchy;
import cz.opendata.tenderstats.admin.RDFaBuilder;
import cz.opendata.tenderstats.pcfapp.PCFappModel;
import cz.opendata.tenderstats.pcfapp.PCFappModelOntology;

/**
 * Component which handles amdinistration interface.
 * 
 * @author Patrik Kompus
 */
@MultipartConfig
public class AdminApp extends AbstractComponent {

	private static final long serialVersionUID = 8835885186029723439L;
	private PCFappModelOntology modelOntology;
	URL url;
	Gson gson;
	OntModel m;
	ClassHierarchy ch;
	String[] opValues, dpValues, cpvValues, opRequired, dpRequired;
	RDFaBuilder rdfaBldr;
	JsonObject json = new JsonObject();

	@Override
	public void init() throws ServletException {
		super.init();
		modelOntology = new PCFappModelOntology(config);
	}
	
	/**
	 * Returns the selected class as JSON object
	 * 
	 */
	private void getClasses(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}

		try {

			url = new URL(request.getParameter("ontologyURI"));

			this.m = ModelFactory.createOntologyModel(
					OntModelSpec.OWL_MEM_RDFS_INF, null);
			m.read(url.toString());
			ch = new ClassHierarchy();

			String[] classes = ch.generateHierarchy(m);

			JsonArray formClasses = modelOntology.getFormsByOntologyAsJson(
					request.getParameter("ontologyURI"),
					getUserContext(request).getNamedGraph());
			String[] formClassesArray = new String[formClasses.size()];

			for (int j = 0; j < formClasses.size(); j++) {
				formClassesArray[j] = formClasses
						.get(j)
						.getAsJsonObject()
						.get("formClass")
						.toString()
						.substring(
								1,
								formClasses.get(j).getAsJsonObject()
										.get("formClass").toString().length() - 1);
			}

			JsonArray results = new JsonArray();

			if (classes.length > 0) {

				for (int i = 0; i < classes.length - 1; i++) {
					JsonObject tempJson = new JsonObject();
					tempJson.addProperty("className", classes[i]);

					if (Arrays.asList(formClassesArray).contains(
							classes[i].substring(classes[i].indexOf("http"))))
						tempJson.addProperty("hasForm", true);
					else
						tempJson.addProperty("hasForm", false);

					results.add(tempJson);
				}
				json.addProperty("success", true);
				json.add("values", results);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json);
			} else {
				List<String> responseList = new ArrayList<String>();
				responseList
						.add("Ontology contains no classes corresponding with GoodRelations.");
				gson = new Gson();
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(gson.toJson(responseList));
			}

		} catch (MalformedURLException ex) {
			List<String> responseList = new ArrayList<String>();
			responseList.add("Bad URI! <br />" + ex.getMessage());
			gson = new Gson();
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(gson.toJson(responseList));
		} catch (RiotException ex) {
			List<String> responseList = new ArrayList<String>();
			responseList.add("Bad ontology! <br />" + ex.getMessage());
			gson = new Gson();
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(gson.toJson(responseList));
		} catch (HttpException | SocketException ex) {
			List<String> responseList = new ArrayList<String>();
			responseList.add("URI offline! <br />" + ex.getMessage());
			gson = new Gson();
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(gson.toJson(responseList));
		} catch (NullPointerException ex) {
			List<String> responseList = new ArrayList<String>();
			responseList.add("Bad URI! <br />" + ex.getMessage());
			gson = new Gson();
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(gson.toJson(responseList));
		}
	}

	/**
	 * Returns objectProperties of the selected class as JSON object
	 * 
	 */
	private void getObjectProperties(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("selClass"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined((Object[]) request.getParameterValues("cpvValues[]"))
				&& request.getParameterValues("cpvValues[]").length < 1) {
			response.sendError(400);
			return;
		}

		cpvValues = request.getParameterValues("cpvValues[]");

		boolean matches = false;
		String[] cpvMatching = new String[100];
		int cpvCnt = 0;

		for (int i = 0; i < cpvValues.length; i++) {
			if (!modelOntology.checkCPV(cpvValues[i], getUserContext(request)
					.getNamedGraph())) {
				matches = true;
				cpvMatching[cpvCnt] = cpvValues[i];
				cpvCnt++;
			}
		}

		if (matches) {

			try {
				json.add("classes", modelOntology.getClassesByCpvAsJson(
						cpvMatching, getUserContext(request).getNamedGraph()));
				json.addProperty("success", false);
			} catch (Exception e) {
				json.addProperty("success", false);
				json.addProperty("error", e.getMessage());
			} finally {
				response.setContentType("application/json; charset=UTF-8");
				System.out.println(json);
				response.getWriter().print(json);
			}
		} else {

			url = new URL(request.getParameter("ontologyURI"));

			m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF,
					null);
			m.read(url.toString());
			ch = new ClassHierarchy();

			String[] objectProperties = ch.getObjectProperties(
					request.getParameter("selClass"), m);
			List<String> objectPropertiesList = new ArrayList<String>();

			for (int i = 0; i < objectProperties.length; i++) {
				objectPropertiesList.add(objectProperties[i]);
			}

			gson = new Gson();

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(gson.toJson(objectPropertiesList));
		}
	}

	/**
	 * Returns classes of selected ontology which already have form generated
	 * 
	 */
	private void getClassesToCopy(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}

		url = new URL(request.getParameter("ontologyURI"));

		this.m = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_RDFS_INF, null);
		m.read(url.toString());
		ch = new ClassHierarchy();

		String[] classes = ch.generateHierarchy(m);

		JsonArray formClasses = modelOntology.getFormsByOntologyAsJson(request
				.getParameter("ontologyURI"), getUserContext(request)
				.getNamedGraph());
		String[] formClassesArray = new String[formClasses.size()];

		for (int j = 0; j < formClasses.size(); j++) {
			formClassesArray[j] = formClasses
					.get(j)
					.getAsJsonObject()
					.get("formClass")
					.toString()
					.substring(
							1,
							formClasses.get(j).getAsJsonObject()
									.get("formClass").toString().length() - 1);
		}

		JsonArray results = new JsonArray();

		for (int i = 0; i < classes.length - 1; i++) {
			JsonObject tempJson = new JsonObject();

			if (Arrays.asList(formClassesArray).contains(
					classes[i].substring(classes[i].indexOf("http")))) {
				tempJson.addProperty("className", classes[i]);
				results.add(tempJson);
			}
		}

		json.addProperty("success", true);
		json.add("values", results);

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(json);

	}

	/**
	 * Returns datatypeProperties of selected ontology
	 * 
	 */
	private void getDatatypeProperties(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}

		url = new URL(request.getParameter("ontologyURI"));

		opValues = request.getParameterValues("opValues[]");
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF,
				null);
		m.read(url.toString());
		ch = new ClassHierarchy();

		String[] datatypeProperties = ch.getDatatypeProperties(m);
		List<String> datatypePropertiesList = new ArrayList<String>();

		for (int i = 0; i < datatypeProperties.length; i++) {
			datatypePropertiesList.add(datatypeProperties[i]);
		}

		gson = new Gson();

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(gson.toJson(datatypePropertiesList));
	}

	/**
	 * Returns pre-RDFa form of selected properties and class
	 * 
	 */
	private void getRDFa(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}
		/*
		 * if (!allDefined((Object[]) request.getParameterValues("opValues[]")))
		 * { response.sendError(400); return; }
		 */
		/*
		 * if (!allDefined((Object[]) request.getParameterValues("dpValues[]")))
		 * { response.sendError(400); return; }
		 */
		if (!allDefined(request.getParameter("selClass"))) {
			response.sendError(400);
			return;
		}
		url = new URL(request.getParameter("ontologyURI"));

		opValues = request.getParameterValues("opValues[]");
		dpValues = request.getParameterValues("dpValues[]");
		opRequired = request.getParameterValues("opRequired[]");
		dpRequired = request.getParameterValues("dpRequired[]");

		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF,
				null);
		m.read(url.toString());
		rdfaBldr = new RDFaBuilder();

		gson = new Gson();

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(
				gson.toJson(rdfaBldr.generateRDFaSeller(url, m, opValues,
						dpValues, opRequired, dpRequired)));
	}

	/**
	 * Calls modelOntology method addClass()
	 * 
	 */
	private void saveForms(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("form"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined((Object[]) request.getParameterValues("cpvValues[]"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("selClass"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined((Object[]) request.getParameterValues("opValues[]"))) {
			response.sendError(400);
			return;
		}

		modelOntology.addClass(getUserContext(request), request);

	}

	/**
	 * Calls modelOntology methods checkOntology() to check GoodRelations classes and addOntology()
	 * 
	 */
	private void addOntology(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontoName"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("ontoURI"))) {
			response.sendError(400);
			return;
		}

		if (!modelOntology.checkOntology(request.getParameter("ontoURI"),
				getUserContext(request).getNamedGraph()))
			modelOntology.addOntology(getUserContext(request), request);
	}

	/**
	 * Calls modelOntology method editClass()
	 * 
	 */
	private void editOntology(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!allDefined(request.getParameter("ontoName"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("ontoURI"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("oldOntoURI"))) {
			response.sendError(400);
			return;
		}

		modelOntology.editOntology(getUserContext(request), request);
	}

	/**
	 * Calls modelOntology method deleteOntology()
	 * 
	 */
	private void deleteOntology(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}
		session.removeAttribute("ontologyURI");
		modelOntology.deleteOntology(getUserContext(request),
				request.getParameter("ontologyURI"));
	}

	/**
	 * Calls modelOntology method deleteClass()
	 * 
	 */
	private void deleteClass(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		if (!allDefined(request.getParameter("formClass"))) {
			response.sendError(400);
			return;
		}
		if(session.getAttribute("formClass")!=null) session.removeAttribute("formClass");
		modelOntology.deleteClass(getUserContext(request),
				request.getParameter("formClass"));
	}

	/**
	 * Returns form as JSON object
	 * 
	 */
	public void loadForm(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {

		if (!allDefined(request.getParameter("selClass"))) {
			response.sendError(400);
			return;
		}

		JsonObject jsonForm = modelOntology.getFormAsJson(request
				.getParameter("selClass"), getUserContext(request)
				.getNamedGraph());

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(jsonForm);
	}
	
	/**
	 * Returns form to copy as JSON object
	 * 
	 */
	public void loadFormToCopy(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {

		if (!allDefined(request.getParameter("selClassCopy"))) {
			response.sendError(400);
			return;
		}
		
		String classURI = request.getParameter("selClassCopy").substring(
				request.getParameter("selClassCopy").indexOf("http"));

		JsonObject jsonForm = modelOntology.getFormAsJson(classURI, getUserContext(request)
				.getNamedGraph());

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().println(jsonForm);
	}

	/**
	 * Calls modelOntology method addClassCopy()
	 * 
	 */
	public void saveCopyForms(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		
		if (!allDefined(request.getParameter("form"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("ontologyURI"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined((Object[]) request.getParameterValues("cpvValues[]"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("selClass"))) {
			response.sendError(400);
			return;
		}
		if (!allDefined(request.getParameter("selClassCopy"))) {
			response.sendError(400);
			return;
		}

		modelOntology.addClassCopy(getUserContext(request), request);

	}
	
	@Override
	protected void doGetPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);

		if (action == null) {
			System.out.println("action");
			response.sendError(400);
			return;
		}
		switch (action) {
		case "getClasses":
			this.getClasses(request, response);
			break;
		case "getClassesToCopy":
			this.getClassesToCopy(request, response);
			break;
		case "loadForm":
			this.loadForm(request, response, session);
			break;
		case "loadFormToCopy":
			this.loadFormToCopy(request, response, session);
			break;			
		case "getObjectProperties":
			this.getObjectProperties(request, response);
			break;
		case "getDatatypeProperties":
			this.getDatatypeProperties(request, response);
			break;
		case "getRDFa":
			this.getRDFa(request, response);
			break;
		case "saveForms":
			this.saveForms(request, response);
			break;
		case "saveCopyForms":
			this.saveCopyForms(request, response, session);
			break;
		case "addOntology":
			this.addOntology(request, response);
			break;
		case "editOntology":
			this.editOntology(request, response);
			break;
		case "deleteOntology":
			this.deleteOntology(request, response, session);
			break;
		case "deleteClass":
			this.deleteClass(request, response, session);
			break;
		case "getOntologyJson":
			if (!allDefined(request.getParameter("ontologyURL"))) {
				response.sendError(400);
				return;
			}
			String ontologyURL = request.getParameter("ontologyURL");

			json = modelOntology.getOntologyAsJson(ontologyURL,
					getUserContext(request).getNamedGraph());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(json.toString());
			break;

		case "getFormJson":
			if (!allDefined(request.getParameter("formClass"))) {
				response.sendError(400);
				return;
			}
			String formClass = request.getParameter("formClass");
			
			json = modelOntology.getFormAsJson(formClass,
					getUserContext(request).getNamedGraph());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().println(json.toString());
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
	}
}
