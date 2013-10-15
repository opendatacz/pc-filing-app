package cz.opendata.tenderstats;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import cz.opendata.tenderstats.pcfapp.PCFappException;
import cz.opendata.tenderstats.pcfapp.PCFappModel;
import cz.opendata.tenderstats.pcfapp.PCFappModelContract;
import cz.opendata.tenderstats.pcfapp.PCFappModelTender;
import cz.opendata.tenderstats.pcfapp.PCFappUtils;

/**
 * Component which handles reading and writing private and public events and
 * associated actions.
 * 
 * @author Matej Snoha
 */
@MultipartConfig
public class PCFilingApp extends AbstractComponent {

	private static final long serialVersionUID = 8835885186029723439L;

	private PCFappModel model;
	private PCFappModelContract modelContract;
	private PCFappModelTender modelTender;
	private PCFappUtils utils;

	@Override
	public void init() throws ServletException {
		super.init();
		
		model = new PCFappModel(config);
		modelContract = new PCFappModelContract(config);
		modelTender = new PCFappModelTender(config);		
		utils = new PCFappUtils(config);
	}

	/**
	 * Holds info about an event to be displayed as tenders table row in
	 * frontend
	 * 
	 * @author Ivan Kosdy
	 */
	class TendersTableRow implements Serializable {

		private static final long serialVersionUID = 5601810594357757380L;

		String tenderURL;
		String buyerURL;
		String contractURL;
		String title;
		String price;
		String currency;
		String cpv;
		String published;
		String deadline;

		public TendersTableRow(String tenderURL, String buyerURL,
				String contractURL, String title, String price,
				String currency, String cpv, String published, String deadline) {
			this.tenderURL = tenderURL;
			this.buyerURL = buyerURL;
			this.contractURL = contractURL;
			this.title = title;
			this.price = price;
			this.currency = currency;
			this.cpv = cpv;
			this.published = published.contains("T") ? published.substring(0,
					published.indexOf('T')) : published;
			this.deadline = deadline.contains("T") ? deadline.substring(0,
					deadline.indexOf('T')) : deadline;
		}
	}

	/**
	 * Holds some info about an event to be displayed as a Submitted tenders
	 * table row in frontend
	 * 
	 * @author Ivan Kosdy
	 */
	class STendersTableRow implements Serializable {

		private static final long serialVersionUID = 5601810594357757380L;

		String tenderURL;
		String supplier;
		String supplierName;
		String currency;
		String price;
		String submitted;

		public STendersTableRow(String tenderURL, String supplier,
				String supplierName, String currency, String price,
				String submitted) {
			this.tenderURL = tenderURL;
			this.supplier = supplier;
			this.supplierName = supplierName;
			this.currency = currency;
			this.price = price;
			this.submitted = submitted.contains("T") ? submitted.substring(0,
					submitted.indexOf('T')) : submitted;
		}
	}
		
	protected ResultSet actionTablePrivateContracts(HttpServletRequest request, UserContext uc) {		
		return modelContract.getPrivateContracts(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTablePublishedCalls(HttpServletRequest request, UserContext uc) {		
		return modelContract.getContracts(uc.getNamedGraph());
	}
	
	protected ResultSet actionTableWithdrawnCalls(HttpServletRequest request, UserContext uc) {		
		return modelContract.getWithdrawnContracts(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableAwardedContracts(HttpServletRequest request, UserContext uc) {		
		return modelContract.getAwardedContracts(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableCompletedContracts(HttpServletRequest request, UserContext uc) {		
		return modelContract.getCompletedContracts(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableCanceledCalls(HttpServletRequest request, UserContext uc) {		
		return modelContract.getCanceledContracts(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableContractSubmittedTenders(HttpServletRequest request, UserContext uc) {		
		String contractURI = request.getParameter("contractURI");
//		TODO: security check ...		
//		Model contract = modelContract.getPrivateContract(contractURI, uc.getNamedGraph(), null);
//		Resource contractRes = contract.getResource(contractURI);		
		return modelTender.getSubmittedTenders(uc.getNamedGraph(), contractURI);		
	}
	
	protected ResultSet actionTablePublicSupplierData(HttpServletRequest request, UserContext uc) {		
		return model.getPublicSupplierData(request.getParameter("entity"));		
	}
	
	protected ResultSet actionTableDashboard(HttpServletRequest request, UserContext uc) {				
		return model.getBuyerActivityData(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTablePrivateTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierPreparedTenders(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableSubmittedTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierSubmittedTenders(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableWithdrawnTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierWithdrawnTenders(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableRejectedTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierRejectedTenders(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableAwardedTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierAwardedTenders(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableTendersForCancelled(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierTendersForCancelled(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableTendersForWithdrawn(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierTendersForWithdrawn(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableCompletedTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierCompletedTenders(uc.getNamedGraph());		
	}
	
	protected ResultSet actionTableNonAwardedTenders(HttpServletRequest request, UserContext uc) {		
		return modelTender.getSupplierNonAwardedTenders(uc.getNamedGraph());		
	}

	protected JsonArray resultSetAsJson(ResultSet resultSet) {

		JsonArray array = new JsonArray();
		List<String> vars = resultSet.getResultVars();
		QuerySolution resultRow;

		while (resultSet.hasNext()) {

			resultRow = resultSet.next();
			Iterator<String> i = vars.iterator();
			JsonObject row = new JsonObject();

			while (i.hasNext()) {
				String var = i.next();
				RDFNode node = resultRow.get(var);

				if (node != null) {
					row.addProperty(var, (node.isLiteral()) ? node.asLiteral()
							.getValue().toString() : node.toString());
				}
			}

			array.add(row);
		}

		return array;

	}

	protected List<HashMap<String, Object>> serializeResultSet(
			ResultSet resultSet) {

		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		List<String> vars = resultSet.getResultVars();

		while (resultSet.hasNext()) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			QuerySolution resultRow = resultSet.next();
			Iterator<String> i = vars.iterator();

			while (i.hasNext()) {
				String var = i.next();
				RDFNode node = resultRow.get(var);

				if (node != null) {
					row.put(var, (node.isLiteral()) ? node.asLiteral()
							.getValue() : node.toString());
				}
			}

			list.add(row);
		}

		return list;
	}

	/**
	 * Retrieves user's tenders from private SPARQL endpoint as a collection of
	 * table rows.
	 * 
	 * @param uc
	 *            UserContext of current user
	 * @throws ServletException
	 */
	protected List<TendersTableRow> getSupplierCompletedTendersAsTable(
			UserContext uc) throws ServletException {

		List<TendersTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = modelTender.getSupplierCompletedTenders(uc.getNamedGraph());

			while (rs.hasNext()) {
				QuerySolution row = rs.next();
				table.add(new TendersTableRow(row.get("tenderURI").toString(),
						row.get("buyerURI").toString(), row.get("contractURI")
								.toString(), row.get("title").asLiteral()
								.getString(), (row.get("price") != null ? row
								.get("price").asLiteral().getString() : ""),
						(row.get("currency") != null ? row.get("currency")
								.toString() : ""), joinCPV(row.get("cpv1URL"),
								row.get("cpvAdd")),
						(row.get("publicationDate") != null ? row
								.get("publicationDate").asLiteral().getString()
								: ""), (row.get("endDate") != null ? row
								.get("endDate").asLiteral().getString() : "")));

			}
		} catch (Exception e) {
			throw new ServletException(
					"Can't get users calls for tenders from private SPARQL endpoint "
							+ config.getSparqlPrivateQuery(), e);
		}
		return table;
	}

	/**
	 * Retrieves user's tenders from private SPARQL endpoint as a collection of
	 * table rows.
	 * 
	 * @param uc
	 *            UserContext of current user
	 * @throws ServletException
	 */
	protected List<TendersTableRow> getSupplierRejectedTendersAsTable(
			UserContext uc) throws ServletException {

		List<TendersTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = modelTender.getSupplierRejectedTenders(uc.getNamedGraph());
			while (rs.hasNext()) {
				QuerySolution row = rs.next();

				table.add(new TendersTableRow(row.get("tenderURI").toString(),
						row.get("buyerURI").toString(), row.get("contractURI")
								.toString(), row.get("title").asLiteral()
								.getString(), (row.get("price") != null ? row
								.get("price").asLiteral().getString() : ""),
						(row.get("currency") != null ? row.get("currency")
								.toString() : ""), joinCPV(row.get("cpv1URL"),
								row.get("cpvAdd")),
						(row.get("publicationDate") != null ? row
								.get("publicationDate").asLiteral().getString()
								: ""), (row.get("deadline") != null ? row.get(
								"deadline").toString() : "")));

			}
		} catch (Exception e) {
			throw new ServletException(
					"Can't get users calls for tenders from private SPARQL endpoint "
							+ config.getSparqlPrivateQuery(), e);
		}
		return table;
	}

	/**
	 * Retrieves user's tenders from private SPARQL endpoint as a collection of
	 * table rows.
	 * 
	 * @param uc
	 *            UserContext of current user
	 * @throws ServletException
	 */
	protected List<TendersTableRow> getSupplierNonAwardedTendersAsTable(
			UserContext uc) throws ServletException {

		List<TendersTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = modelTender.getSupplierNonAwardedTenders(uc
					.getNamedGraph());
			while (rs.hasNext()) {
				QuerySolution row = rs.next();

				table.add(new TendersTableRow(row.get("tenderURI").toString(),
						row.get("buyerURI").toString(), row.get("contractURI")
								.toString(), row.get("title").asLiteral()
								.getString(), (row.get("price") != null ? row
								.get("price").asLiteral().getString() : ""),
						(row.get("currency") != null ? row.get("currency")
								.toString() : ""), joinCPV(row.get("cpv1URL"),
								row.get("cpvAdd")),
						(row.get("publicationDate") != null ? row
								.get("publicationDate").asLiteral().getString()
								: ""), (row.get("deadline") != null ? row.get(
								"deadline").toString() : "")));

			}
		} catch (Exception e) {
			throw new ServletException(
					"Can't get users calls for tenders from private SPARQL endpoint "
							+ config.getSparqlPrivateQuery(), e);
		}
		return table;
	}

	protected List<STendersTableRow> getSubmittedTendersAsTable(UserContext uc,
			String contractURI) throws ServletException {

		List<STendersTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = modelTender.getSubmittedTenders(uc.getNamedGraph(),
					contractURI);

			while (rs.hasNext()) {
				QuerySolution row = rs.next();

				table.add(new STendersTableRow(row.get("tenderURL").toString(),
						row.get("grSupplier").toString(), row
								.get("supplierName").asLiteral().getString(),
						row.get("currency").toString(), row.get("price")
								.asLiteral().getString(),
						(row.get("submitted") != null ? row.get("submitted")
								.toString() : "")));
			}

		} catch (Exception e) {
			throw new ServletException(
					"Can't get users calls for tenders from private SPARQL endpoint "
							+ config.getSparqlPrivateQuery(), e);
		}
		return table;
	}
	
	private void writeJsonResponse(HttpServletResponse response, JsonObject json) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(json);
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

			String tenderURL;
			HttpSession session = request.getSession(false);			
			JsonObject json = new JsonObject();			
			
			// TODO refactor - split into methods

			System.out.println(action);

			UserContext uc = getUserContext(request);

			switch (action) {

			case "buyerStats":				
				json = model.getBuyerStats(uc.getNamedGraph(),false);
				writeJsonResponse(response, json);
				break;
			
			case "table":								
				try {					
					json = getTableData(request, response, uc, session);
					json.addProperty("success", true);					
				} catch (PCFappException e) {					
					json.addProperty("success", false);
					json.addProperty("error", e.getMessage());										
				} finally {					
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().print(json);					
				}				
				break;

			case "addPrivateContract":
				if (!allDefined(request.getParameter("title"))) {
					response.sendError(400);
					return;
				}
				modelContract.addPrivateContract(getUserContext(request),
						request.getParameter("title"),
						request.getParameter("description"),
						request.getParameter("cpv1"),
						request.getParameter("cpv2"),
						request.getParameter("cpv3"),
						request.getParameter("projectID"),
						request.getParameter("deadline"),
						request.getParameter("estimatedPrice"),
						request.getParameter("estimatedPriceCurrency"),
						request.getParameter("priceIsConfidential"),
						request.getParameter("estimatedStartDate"),
						request.getParameter("estimatedEndDate"),
						request.getParameter("location"),
						request.getParameter("nuts"),
						request.getParameter("evalPrice"),
						request.getParameter("evalTech"),
						request.getParameter("evalDate"),
						request.getParameter("contactPerson"),
						request.getParameter("contactEmail"),
						request.getParameter("contactPhone"),
						request.getParameter("contactDescription"),
						request.getParameter("eventReference"),
						request.getParameter("procurementMethod"),
						request.getParameter("eventType"),
						request.getParameter("tendersSealed"),
						null,
						null,						
						request,
						null);

				session.removeAttribute("privateContracts");
				session.removeAttribute("callsForTenders");
				break;

			case "deletePrivateContract":
				if (!allDefined(request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				session.removeAttribute("privateContracts");
				session.removeAttribute("callsForTenders");
				modelContract.deletePrivateContract(getUserContext(request),
						request.getParameter("contractURL"), true);
				break;

			case "finalizeContract":
				if (!allDefined(request.getParameter("contractURL"),
						request.getParameter("publishTenders"),
						request.getParameter("actualEndDate"),
						request.getParameter("satisfiedField"),
						request.getParameter("actualPrice"),
						request.getParameter("actualPriceCurrency"))) {
					response.sendError(400);
					return;
				}
				modelContract.finalizeContract(getUserContext(request),
						request.getParameter("contractURL"),
						request.getParameter("publishTenders"),
						request.getParameter("actualEndDate"),
						request.getParameter("satisfiedField"),
						request.getParameter("actualPriceCurrency"),
						request.getParameter("actualPrice"));
				session.removeAttribute("awardedCallsForTenders");
				session.removeAttribute("completedCallsForTenders");
				break;

			case "cancelContract":
				if (!allDefined(request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				session.removeAttribute("privateContracts");
				session.removeAttribute("callsForTenders");
				session.removeAttribute("withdrawnCallsForTenders");
				//model.deletePublicContract(getUserContext(request),
				//		request.getParameter("contractURL"));
				
				modelContract.cancelContract(getUserContext(request),request.getParameter("contractURL"));
				break;
			
			case "withdrawContract":
				if (!allDefined(request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				session.removeAttribute("privateContracts");
				session.removeAttribute("callsForTenders");
				session.removeAttribute("withdrawnCallsForTenders");
				//model.deletePublicContract(getUserContext(request),
				//		request.getParameter("contractURL"));
				
				modelContract.withdrawContract(getUserContext(request),request.getParameter("contractURL"));
				break;

			case "publishPrivateContract":
				if (!allDefined(request.getParameter("contractURI"))) {
					response.sendError(400);
					return;
				}
				try {
					if (!modelContract.publishContract(getUserContext(request),
							request.getParameter("contractURI"))) {
						response.sendError(409,
								"This contract has already been published.");
						return;
					}
				} catch (PCFappException e) {
					throw new ServletException(e);
				}
				session.removeAttribute("privateContracts");
				session.removeAttribute("callsForTenders");
				session.removeAttribute("withdrawnCallsForTenders");
				break;

			case "addPrivateTender":
				if (!allDefined(request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				try {
					modelTender.addPrivateTender(getUserContext(request),
							request.getParameter("contractURL"),
							request.getParameter("buyerURL"),
							request.getParameter("description"),
							request.getParameter("price"),
							request.getParameter("currency"),
							request.getParameter("startDate"),
							request.getParameter("endDate"),
							request.getParameter("inputFileCerts"),
							request.getParameter("inputFileProfile"),
							request.getParameter("inputFileFinStatements"),
							request);
				} catch (ServletException exception) {
					response.sendError(409, exception.getLocalizedMessage());
					return;
				}
				session.removeAttribute("preparedTenders");
				break;

			case "submitPrivateTender":
				if (!allDefined(request.getParameter("tenderURL"),
						request.getParameter("contractURL"),
						request.getParameter("buyerURL"))) {
					System.out.println(request.getParameter("tenderURL"));
					System.out.println(request.getParameter("contractURL"));
					System.out.println(request.getParameter("buyerURL"));					
					response.sendError(400);
					return;
				}
				if (!modelTender.submitTender(getUserContext(request),
						request.getParameter("tenderURL"),
						request.getParameter("buyerURL"),
						request.getParameter("contractURL"))) {
					response.sendError(409,
							"This tender has already been published.");
					return;
				}
				session.removeAttribute("preparedTenders");
				session.removeAttribute("supplierSubmittedTenders");
				break;

			case "deletePrivateTender":
				if (!allDefined(request.getParameter("tenderURL"))) {
					response.sendError(400);
					return;
				}
				modelTender.deletePrivateTender(getUserContext(request),
						request.getParameter("tenderURL"));
				session.removeAttribute("preparedTenders");
				break;

			case "withdrawTender":
				if (!allDefined(request.getParameter("tenderURL"),
						request.getParameter("contractURL"),
						request.getParameter("buyerURL"))) {
					response.sendError(400);
					return;
				}
				modelTender.withdrawTender(getUserContext(request),
						request.getParameter("tenderURL"),
						request.getParameter("buyerURL"),
						request.getParameter("contractURL"));
				session.removeAttribute("preparedTenders");
				session.removeAttribute("supplierSubmittedTenders");
				break;

			case "awardTender":
				if (!allDefined(request.getParameter("tenderURL"),
						request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				modelTender.awardTender(getUserContext(request),
						request.getParameter("tenderURL"),
						request.getParameter("contractURL"));
				session.removeAttribute("submittedTenders");
				session.removeAttribute("awardedCallsForTenders");
				session.removeAttribute("callsForTenders");
				break;

			case "rejectTender":
				if (!allDefined(request.getParameter("tenderURL"),
						request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				modelTender.rejectTender(getUserContext(request),
						request.getParameter("tenderURL"),
						request.getParameter("contractURL"),
						request.getParameter("rejectNote"));
				session.removeAttribute("submittedTenders");
				session.removeAttribute("callsForTenders");
				break;

			case "getBusinessEntitySupplier":
				if (!allDefined(request.getParameter("entity"))) {
					response.sendError(400);
					return;
				}

				System.out.println(request.getParameter("entity"));
				
				Model supplier = model.getSupplier(request
						.getParameter("entity"));
				Resource supplierResource = supplier.getResource(request
						.getParameter("entity"));

				supplier.write(System.out);
				
				Statement supplierName = supplierResource.getProperty(PCFappModel.dc_title);
				if (supplierName != null) {
					json.addProperty("success", true);
					json.addProperty("name", supplierName.getString());
				} else {
					json.addProperty("success", false);					
				}

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				break;

			case "getBusinessEntityBuyer":
				if (!allDefined(request.getParameter("entity"))) {
					response.sendError(400);
					return;
				}

				Model buyer = model.getSupplier(request.getParameter("entity"));
				Resource buyerResource = buyer.getResource(request
						.getParameter("entity"));

				Statement buyerName = buyerResource
						.getProperty(PCFappModel.dc_title);
				if (buyerName != null) {
					json.addProperty("success", true);
					json.addProperty("name", buyerName.getString());
				} else {
					json.addProperty("success", false);
				}

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				break;

			case "getContractJson":
				if (!allDefined(request.getParameter("copyContractURL"))) {
					response.sendError(400);
					return;
				}
				String copyURL = request.getParameter("copyContractURL");

				try {
					json = modelContract.getContractAsJson(copyURL, getUserContext(request)
							.getNamedGraph());
				} catch (PCFappException e) {
					throw new ServletException(e);
				}

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				break;
			
			case "getPublicContractJson":
				if (!allDefined(request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				String contractURI = request.getParameter("contractURL");
				
				JsonObject ret;
				try {
					ret = modelContract.getPublicContractAsJson(model.getPublicContract(contractURI),contractURI);
				} catch (PCFappException e) {
					throw new ServletException(e);
				} 
				json.add("data", ret);
				json.addProperty("success", true);
				
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				break;

			case "getContractJsonSupplier":
				if (!allDefined(request.getParameter("contractURL"),
						request.getParameter("buyerURL"))) {
					response.sendError(400);
					return;
				}
				contractURI = request.getParameter("contractURL");
				String ns = request.getParameter("buyerURL");

				try {
					json = modelContract.getContractAsJson(contractURI, ns);
				} catch (PCFappException e) {
					throw new ServletException(e);
				}

				if (json.has("confidential")
						&& json.get("confidential").getAsBoolean()) {
					json.remove("price");
					json.remove("currency");
				}

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				break;

			case "getTenderJson":
				if (!allDefined(request.getParameter("editTenderURL"))) {
					response.sendError(400);
					return;
				}

				tenderURL = request.getParameter("editTenderURL");
				tenderURL = URLDecoder.decode(tenderURL, "UTF-8").toString();

				json = modelTender.getTenderAsJson(tenderURL, getUserContext(request)
						.getNamedGraph());

				Model t = modelTender.getPrivateTender(tenderURL, uc.getNamedGraph());
				System.out.println("-----------");
				t.write(System.out);
				System.out.println("-----------");
				t.removeAll(null, PCFappModel.pcf_created, null);

				t.removeAll(null, PCFappModel.pcf_created, null);
				t.removeAll(null, PCFappModel.pcf_modified, null);
				t.removeAll(null, PCFappModel.pcf_submitted, null);

				NodeIterator i = t
						.listObjectsOfProperty(PCFappModel.pcf_document);
				while (i.hasNext()) {
					t.removeAll(i.next().asResource(), null, null);
				}
				t.removeAll(null, PCFappModel.pcf_document, null);
				t.write(System.out);
				System.out.println("-----------");

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				break;

			case "editTender":

				if (!allDefined(request.getParameter("tenderURL"))) {
					response.sendError(400);
					return;
				}

				tenderURL = URLDecoder.decode(
						request.getParameter("tenderURL"), "UTF-8").toString();

				modelTender.editPrivateTender(getUserContext(request), tenderURL,
						request.getParameter("description"),
						request.getParameter("price"),
						request.getParameter("currency"),
						request.getParameter("startDate"),
						request.getParameter("endDate"),
						request.getParameter("inputFileCerts"),
						request.getParameter("inputFileProfile"),
						request.getParameter("inputFileFinStatements"), request);

				break;

			case "editEvent":
				if (!allDefined(request.getParameter("editContractURL"))) {
					response.sendError(400);
					return;
				}

				contractURI = request.getParameter("editContractURL");
				Model contractModel = modelContract.getPrivateContract(contractURI,
						uc.getNamedGraph(), "all");
				Resource contractRes = contractModel.getResource(contractURI);

				modelContract.deletePrivateContract(uc, contractURI, false);
				modelContract.addPrivateContract(getUserContext(request),
						request.getParameter("title"),
						request.getParameter("description"),
						request.getParameter("cpv1"),
						request.getParameter("cpv2"),
						request.getParameter("cpv3"),
						request.getParameter("projectID"),
						request.getParameter("deadline"),
						request.getParameter("estimatedPrice"),
						request.getParameter("estimatedPriceCurrency"),
						request.getParameter("priceIsConfidential"),
						request.getParameter("estimatedStartDate"),
						request.getParameter("estimatedEndDate"),
						request.getParameter("location"),
						request.getParameter("nuts"),
						request.getParameter("evalPrice"),
						request.getParameter("evalTech"),
						request.getParameter("evalDate"),
						request.getParameter("contactPerson"),
						request.getParameter("contactEmail"),
						request.getParameter("contactPhone"),
						request.getParameter("contactDescription"),
						request.getParameter("eventReference"),
						request.getParameter("procurementMethod"),
						request.getParameter("eventType"),
						request.getParameter("tendersSealed"), contractRes
								.getProperty(PCFappModel.pcf_created)
								.getLiteral().getString(), contractURI, request, null);

				session.removeAttribute("privateContracts");
				
				break;

			case "document":
				utils.retreiveDocument(request, response);
				break;

			case "unlinkContractDocument":
				if (!allDefined(request.getParameter("contractURL"),
						request.getParameter("token"))) {
					response.sendError(400);
					return;
				}

				model.unlinkDocument(uc, request.getParameter("contractURL"),
						request.getParameter("token"));

				json.addProperty("success", true);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());

				break;

			case "unlinkTenderDocument":
				if (!allDefined(request.getParameter("tenderURL"),
						request.getParameter("token"))) {
					response.sendError(400);
					return;
				}
				tenderURL = URLDecoder.decode(
						request.getParameter("tenderURL"), "UTF-8").toString();
				model.unlinkDocument(uc, tenderURL,
						request.getParameter("token"));

				json.addProperty("success", true);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());

				break;

			case "supplierDocsUpload":
				model.addSupplierDocs(uc, request);
				break;
			
			case "openTenders":
				if (!allDefined(request.getParameter("contractURL"))) {
					response.sendError(400);
					return;
				}
				
				json.addProperty("success", modelContract.openTenders(request.getParameter("contractURL"),uc.getNamedGraph()));
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().println(json.toString());
				
				break;

			case "getSupplierDocs":
				json.add("docs", getDocumentsArray(uc));
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

		} else {
			response.sendError(403, "No user logged in.");
		}

	}

	private JsonArray getDocumentsArray(UserContext uc) {

		ResultSet rs = model.getUserDocuments(uc.getNamedGraph(), true);

		JsonArray array = new JsonArray();

		while (rs.hasNext()) {
			QuerySolution row = rs.next();

			JsonObject obj = new JsonObject();
			obj.addProperty("documentURI", row.get("documentURI").toString());
			obj.addProperty("fileName", row.get("fileName").asLiteral()
					.getString());
			obj.addProperty("token", row.get("token").asLiteral().getString());
			obj.addProperty("docType", row.get("docType").asResource()
					.getLocalName());
			array.add(obj);

		}

		return array;

	}

	private class TableLimit {
		
		public TableLimit(int pageIn, int itemsIn) {
			this.page = pageIn;
			this.items = itemsIn;
		}
		
		public int page;
		public int items;
		
	}
	
	private TableLimit checkTableInput(Object pageObj, Object itemsObj, Object tableNameObj) throws PCFappException {
		
		if (!allDefined(pageObj,itemsObj, tableNameObj)) {
			throw new PCFappException("Values are not set.");
		}
		
		int page;
		int items;		
		
		try {			
			page = Integer.parseInt((String) pageObj);
			items = Integer.parseInt((String) itemsObj);			
			if ( page < 0 || items < 1 ) throw new NumberFormatException();					
			
		} catch ( NumberFormatException e ) {			
			throw new PCFappException("Invalid values");
		}
		
		return new TableLimit(page, items);
	}
	
	private JsonObject getTableData(HttpServletRequest request, HttpServletResponse response, UserContext uc, HttpSession session ) throws PCFappException, IOException {		
		
		TableLimit limits = checkTableInput(request.getParameter("page"),request.getParameter("items"),request.getParameter("tableName"));				
		
		JsonArray resource = null;		
		Method actionTable = null;
		
		try {
			actionTable = this.getClass().getDeclaredMethod("actionTable"+request.getParameter("tableName"),HttpServletRequest.class, UserContext.class);
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new PCFappException("No such table");
			
		}
		
		ResultSet set;
		JsonParser p = new JsonParser();
		synchronized (session) {
		
			String resourceString = (String) session.getAttribute(request.getParameter("tableName"));							
			if ( request.getParameter("reload") != null || resourceString == null ) {					
				
				try {
					set = (ResultSet) actionTable.invoke(this, request, uc);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new PCFappException("Unable to load table data. Server error");
				}
				
				resource = resultSetAsJson(set);
				session.setAttribute(request.getParameter("tableName"),resource.toString());						
			}
			else
			{
				resource = (JsonArray) p.parse(resourceString);
			}
			
		}
		
		JsonObject responseJson = new JsonObject();
		JsonArray data = new JsonArray();		
		
		if ( request.getParameter("reload") != null ) {
			responseJson.addProperty("pages",Math.ceil( (float) resource.size() / limits.items ));
			responseJson.addProperty("items",resource.size() );					
		}
		
		for (int i = (limits.page*limits.items); i >= 0 && i < ((limits.page+1)*limits.items) && i < resource.size(); i++) {
			data.add(resource.get(i));
		}
		
		responseJson.add("data", data);		
		
		return responseJson;		
	}
	
	String joinCPV(RDFNode main, RDFNode other) {

		String cpv = main.toString()
				+ (other != null ? " " + other.toString() : "");
		cpv = cpv.replaceAll("http://purl.org/weso/cpv/2008/", "");
		cpv = cpv.replaceAll(" ", ", ");
		return cpv;

	}
}
