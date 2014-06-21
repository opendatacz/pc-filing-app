package cz.opendata.tenderstats;

import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import java.net.URLDecoder;
import java.sql.Connection;

/**
 * Servlet implementation class InvitationComponent
 */
@WebServlet("/InvitationComponent")
public class InvitationComponent extends AbstractComponent {

	private static final long serialVersionUID = 1L;
	private Connection con = null;

	/**
	 * Holds some info about an event to be displayed as a Invitations table row in frontend
	 * 
	 * @author Ivan Kosdy
	 */
	class InvitationTableRow implements Serializable {

		private static final long serialVersionUID = 4700810594357757380L;

		String invitationURL;
		String contractURL;
		String buyerURL;
		String title;
		String price;
		String currency;
		String cpv;
		String published;
		String deadline;
		String ownerEntity;
		String ownerName;

		public InvitationTableRow(String invitationURL, String contractURL, String title, String price, String currency,
				String cpv, String published, String deadline, String buyerURL, String ownerEntity, String ownerName) {
			this.invitationURL = invitationURL;
			this.contractURL = contractURL;
			this.title = title;
			this.price = price;
			this.currency = currency;
			this.cpv = cpv;
			this.published = published.contains("^") ? published.substring(0, published.indexOf('^')) : published;
			this.deadline = deadline.contains("T") ? deadline.substring(0, deadline.indexOf('T')) : deadline;
			this.buyerURL = buyerURL;
			this.ownerEntity = ownerEntity;
			this.ownerName = ownerName;
		}
	}

	private void getConnection() {
		if (con == null) {
			try {
				con =
						DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase()
								+ "?useUnicode=yes&characterEncoding=UTF-8", config.getRdbUsername(), config.getRdbPassword());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void addInvitationEntry(String inv_id, String email, String contractURI) {

		getConnection();

		Connection con;
		try {
			con =
					DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
							config.getRdbUsername(),
							config.getRdbPassword());

			String select = "INSERT INTO invitations (`id`,`email`,`contractURI`) VALUES (?,?,?)";
			PreparedStatement insertStatement = con.prepareStatement(select);

			insertStatement.setString(1, inv_id);
			insertStatement.setString(2, email);
			insertStatement.setString(3, contractURI);

			insertStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected String getInvitationContract(String inv_id, String email) {

		String contract = null;

		getConnection();

		String select = "SELECT * FROM invitations WHERE id = ? AND email = ?";
		PreparedStatement selectStatement;
		try {
			selectStatement = con.prepareStatement(select);

			selectStatement.setString(1, inv_id);
			selectStatement.setString(2, email);

			java.sql.ResultSet result = selectStatement.executeQuery();

			if (result.next()) {
				contract = result.getString("contractURI");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return contract;

	}

	/**
	 * Returns private invitation as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	protected Model getPrivateInvitation(String invitationURI, String namedGraph) {
		/* @formatter:off */
		Query query = QueryFactory.create(
				config.getPreference("prefixes") + 
				"CONSTRUCT  " + 
				"  { ?invitationURI ?p1 ?o1 . " + 
				"    ?o1 ?p2 ?o2 . " + 
				"    ?o2 ?p3 ?o3 . " + 
				"    ?o3 ?p4 ?o4 . " + 
				"    ?o4 ?p5 ?o5 .} " + 
				"FROM <" + namedGraph + "> " + 
				"WHERE " + 
				"  { ?invitationURI ?p1 ?o1 . " + 
				"    ?invitationURI a pcInv:Invitation " + 
				"    OPTIONAL " + 
				"      { ?o1 ?p2 ?o2 " + 
				"        OPTIONAL " + 
				"          { ?o2 ?p3 ?o3 " + 
				"            OPTIONAL " + 
				"              { ?o3 ?p4 ?o4 " + 
				"                OPTIONAL " + 
				"                  { ?o4 ?p5 ?o5 } " + 
				"              } " + 
				"          } " + 
				"      } " + 
				"  } " +
				"VALUES ?invitationURI { <" + invitationURI + "> }");
		/* @formatter:on */
		Model contract = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

		// System.out.println("###################################################");
		// contract.write(System.out, "Turtle");

		return contract;
	};

	@Override
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.doGetPost(request, response);

		if (isUserLoggedIn(request)) {
			String action = request.getParameter("action");
			if (action == null) {
				response.sendError(400);
				return;
			}

			JsonObject resp = new JsonObject();
			String contractURL = request.getParameter("contractURL");
			String email;

			HttpSession session = request.getSession(false);
			Gson gson = new Gson();

			int from = 0, to = 0;
			List<InvitationTableRow> invitations = null;
			List<InvitationTableRow> itpage = null;

			switch (action) {

				case "deleteInvitation":
					if (!allDefined(request.getParameter("invURL"))) {
						response.sendError(400);
						return;
					}
					deleteInvitation(getUserContext(request), request.getParameter("invURL"));
					break;

				case "obtain":
					String inv_id = request.getParameter("inv_id");
					email = request.getParameter("email");

					// System.out.println("::obtaining invitation #"+inv_id + " @ "+ email);

					contractURL = getInvitationContract(inv_id, email);

					if (contractURL != null) {

						addInvitation(getUserContext(request), inv_id, contractURL);
						session.removeAttribute("invitations");
						response.sendRedirect(request.getParameter("forward"));
					} else {
						response.sendRedirect(request.getParameter("forward-if-fail"));
					}
					break;

				case "send":

					email = request.getParameter("email");
					String name = request.getParameter("name");
					String contract = request.getParameter("contract");

					String message = "";

					boolean sent = false;
					if (email != null) {

						// System.out.println("..."+contractURL);

						inv_id = UUID.randomUUID().toString();
						String mail_content =
								name + " invited you to participate in the bidding process for a public contract \"" + contract
										+ "\" using TenderStats.\n\n"
										+ "Try out TenderStats Beta at http://tenderstats.xrg.cz/obtain.html?inv_id=" + inv_id
										+ "&email=" + email;
						System.out.println(mail_content);

						addInvitationEntry(inv_id, URLDecoder.decode(email, "UTF-8"), URLDecoder.decode(contractURL, "UTF-8"));
						sent = new Mailer(config.getPreference("invitationEmail"), email, "New invitation", mail_content).send();
						message = (sent) ? "Message sent" : "Internal error";
						response.setContentType("application/json; charset=UTF-8");

					} else {
						message = "Invalid parameters";
						sent = false;
					}

					response.setContentType("application/json; charset=UTF-8");
					resp.addProperty("sent", sent);
					resp.addProperty("message", message);
					response.getWriter().println(resp);
					break;

				case "getInvitations":
					if (!allDefined(request.getParameter("from"), request.getParameter("to"))) {
						response.sendError(400);
						return;
					}

					synchronized (session) {
						invitations = (List<InvitationTableRow>) session.getAttribute("invitations");
						if (invitations == null) {
							invitations = getInvitationsAsTable(getUserContext(request));
							session.setAttribute("invitations", invitations);
						}
					}

					itpage = new ArrayList<>();
					from = 0;
					to = invitations.size();
					try {
						from = Integer.parseInt(request.getParameter("from"));
						to = Integer.parseInt(request.getParameter("to"));
					} catch (NumberFormatException unused) {
					}
					for (int i = from; i >= 0 && i < to && i < invitations.size(); i++) {
						itpage.add(invitations.get(i));
					}
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(gson.toJson(itpage));
					break;

				case "getInvitationsPages":
					synchronized (session) {
						invitations = null;// (List<InvitationTableRow>) session.getAttribute("invitations");
						if (invitations == null) {
							invitations = getInvitationsAsTable(getUserContext(request));
							session.setAttribute("invitations", invitations);
						}
					}

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println((int) Math.ceil(invitations.size() / 10d)); // TODO length from uc
					break;

			}
		} else {
			response.sendError(403, "No user logged in.");
		}

	}

	/**
	 * Retrieves user's calls for tenders from private SPARQL endpoint as a collection of table rows.
	 * 
	 * @param uc
	 *            UserContext of current user
	 * @throws ServletException
	 */
	protected List<InvitationTableRow> getInvitationsAsTable(UserContext uc) throws ServletException {
		/* @formatter:off */
		Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
				config.getPreference("prefixes") +
				"SELECT DISTINCT ?invitationURI ?contractURI ?title ?published ?deadline ?price ?conf ?currency ?ownerGraph ?cpv1URL ?cpvAdd ?buyerName ?buyerEntity " +
				"WHERE " + 
				"  { GRAPH <" + uc.getNamedGraph() + "> " + 
				"      {      " + 
				"           ?invitationURI 		rdf:type 			pcInv:Invitation " +
				".		    ?invitationURI 		pcInv:Contract 		?contractURI" +
				"		} " +						    								
				"    GRAPH ?ownerGraph {" +
				"			?contractURI 		rdf:type 			pc:Contract" +
				".			?contractURI 		dcterms:title 		?title		" +
				".			?contractURI 		pc:notice 			?notice " +		
				".			?contractURI		pcfapp:status		pcfapp:Published  " +
				".          ?notice 			pc:publicationDate 	?published " +
				".			?contractURI 		pc:mainObject       ?cpv1URL " +
				".			OPTIONAL { ?contractURI	pcfapp:confidentialPrice ?conf }" +
				"			OPTIONAL { { "+
				"			SELECT ?contractURI (group_concat( distinct ?cpv) as ?cpvAdd) " +
				"			WHERE { OPTIONAL { GRAPH ?g { ?contractURI pc:additionalObject ?cpv } } } "+
				"			GROUP BY ?contractURI "+
				"			} }" +
				"			OPTIONAL " + 
				"              { ?contractURI 	pc:tenderDeadline 	?deadline } " +
				"			OPTIONAL " + 
				"              { ?contractURI 	pc:contractingAuthority 	?buyerEntity" +
				".				 ?buyerEntity	a 							gr:BusinessEntity" +
				".				 ?buyerEntity	dc:title					?buyerName } " +
				"			OPTIONAL " + 
				"              { ?contractURI 	pc:estimatedPrice 	?priceURI . " + 
				"                ?priceURI 		gr:hasCurrencyValue ?price . " + 
				"                ?priceURI 		gr:hasCurrency 		?currency " + 
				"              } " +
				"			FILTER NOT EXISTS { ?contractURI pc:awardedTender ?awarded } " +
				"			FILTER NOT EXISTS { ?contractURI pcfapp:withdrawn \"true\"^^xsd:boolean } " +
				"		}" + 
				"  }");
		/* @formatter:on */

		// System.out.println("###################################################");
		System.out.println(query);

		List<InvitationTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();
			while (rs.hasNext()) {
				QuerySolution row = rs.next();

				String cpv =
						row.get("cpv1URL").toString() + (row.get("cpvAdd") != null ? " " + row.get("cpvAdd").toString() : "");
				cpv = cpv.replaceAll("http://purl.org/weso/cpv/2008/", "");
				cpv = cpv.replaceAll(" ", ", ");

				table.add(new InvitationTableRow(row.get("invitationURI").toString(),
						row.get("contractURI").toString(),
						row.get("title").asLiteral().getString(),
						(!row.contains("conf")) ? (row.get("price") != null ? row.get("price").asLiteral().getString() : "")
								: "confidential",
						(!row.contains("conf")) ? (row.get("currency") != null ? row.get("currency").toString() : "") : "",
						cpv,
						(row.get("published") != null ? row.get("published").toString() : ""),
						(row.get("deadline") != null ? row.get("deadline").toString() : ""),
						row.get("ownerGraph").toString(),
						(row.get("buyerEntity") != null ? row.get("buyerEntity").toString() : ""),
						(row.get("buyerName") != null ? row.get("buyerName").asLiteral().getString() : "")));
			}
		} catch (Exception e) {
			throw new ServletException("Can't get users calls for tenders from private SPARQL endpoint "
					+ config.getSparqlPrivateQuery(), e);
		}
		return table;
	}

	/**
	 * Creates new tender with binding to public contract
	 * 
	 * @param string
	 * @param userContext
	 * 
	 * @param uc
	 * @param contractURL
	 * @throws ServletException
	 */
	protected void addInvitation(UserContext uc, String invitationURL, String contractURI) throws ServletException {

		String supplierNG = uc.getNamedGraph();
		String newInvitationURL = supplierNG + "/invitation/" + invitationURL;

		// System.out.println("addingInvitation");

		// System.out.println(contractURI);

		UpdateRequest request =
				UpdateFactory.create(config.getPreference("prefixes") + "INSERT DATA" + "{ " + "	GRAPH <" + supplierNG + "> { "
						+ "		<" + newInvitationURL + ">	   		 a					pcInv:Invitation " + ";			   								 pcInv:Contract		<"
						+ contractURI + "> " + "	}" + "}");

		// System.out.println("request:");
		System.out.println(request);
		// System.out.println(config.getSparqlPrivateUpdate());
		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	protected void deleteInvitation(UserContext uc, String invitationURL) {

		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create(	// TODO This works for our URIs, but might not for others
			config.getPreference("prefixes") +
			"WITH <" + uc.getNamedGraph() + "> " + 
			"DELETE " + 
			"{ ?s ?p ?o }" + 
			"WHERE" + 
			"{" + 
			"   ?s ?p ?o ." + 
			"   FILTER ( CONTAINS(str(?s), \"" + invitationURL + "\") )" +  
			"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(request);

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

	}

}
