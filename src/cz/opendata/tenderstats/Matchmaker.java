package cz.opendata.tenderstats;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Component which handles matching events and suppliers.
 * 
 * @author Matej Snoha
 */
public class Matchmaker extends AbstractComponent {

	private static final long serialVersionUID = -4687104409088410200L;

	/**
	 * Holds some info about an event to be displayed as a similar events table row in frontend
	 * 
	 * @author Matej Snoha
	 */
	class SimilarContractTableRow implements Serializable {

		private static final long serialVersionUID = 274873199120238990L;

		String contractURL;
		String title;
		String description;
		String price;
		String currency;
		String place;

		public SimilarContractTableRow(String contractURL, String title, String description, String price, String currency,
				String place) {
			this.contractURL = contractURL;
			this.title = title;
			this.description = description;
			this.price = price;
			this.currency = currency;
			this.place = place;
		}
	}

	/**
	 * Holds some info about a supplier to be displayed as a suitable suppliers table row in frontend
	 * 
	 * @author Matej Snoha
	 */
	class SuitableSuppliersTableRow implements Serializable {

		private static final long serialVersionUID = 3466740279496631690L;

		String name;
		String place;
		String address;

		public SuitableSuppliersTableRow(String name, String place, String address) {
			this.name = name;
			this.place = place;
			this.address = address;
		}
	}

	/**
	 * Holds some info about a tender to be displayed as a suitable open tenders table row in frontend
	 * 
	 * @author Matej Snoha
	 */
	class SuitableTendersTableRow implements Serializable {

		private static final long serialVersionUID = -7519278622977102808L;

		String contractURL;
		String title;
		String description;
		String price;
		String currency;
		String cpv;
		String publicationDate;
		String deadline;

		public SuitableTendersTableRow(String contractURL, String title, String description, String price, String currency,
				String cpv, String publicationDate, String deadline) {
			this.contractURL = contractURL;
			this.title = title;
			this.description = description;
			this.price = price;
			this.currency = currency;
			this.cpv = cpv;
			this.publicationDate = publicationDate.contains("^") ? publicationDate.substring(0, publicationDate.indexOf('^'))
					: publicationDate;
			this.deadline = deadline.contains("T") ? deadline.substring(0, deadline.indexOf('T')) : deadline;
		}
	}

	/**
	 * Retrieves contracts similar to the one specified.
	 * 
	 * @param contractURL
	 * @param cpvString
	 * @throws ServletException
	 */
	protected List<SimilarContractTableRow> getSimilarContractsAsTable(String contractURL, String cpvString)
			throws ServletException {

		// TODO get contracts cpvs from endpoint, not param
		String[] cpvs = cpvString.split("\\, ");

		String cpvValues = "";
		String cpvValuesPattern = "<" + config.getPreference("cpvURL") + "%1$s> ";
		for (String cpv : cpvs) {
			cpvValues += String.format(cpvValuesPattern, cpv);
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT DISTINCT ?contractURI ?title ?description ?price ?currency ?place " +  
				"WHERE { {" + 
				"         ?contractURI 	rdf:type 			pc:Contract ;" + 
				"                      	pc:mainObject 		?cpv ;" +
				"					   	dcterms:title 		?title ." + 
				"	 	 OPTIONAL " + 
				"	       { ?contractURI dcterms:description ?description } " +
				"        OPTIONAL " + 
				"          { ?contractURI pc:estimatedPrice	?priceURI . " + 
				"            ?priceURI gr:hasCurrencyValue 	?price . " + 
				"            ?priceURI gr:hasCurrency 		?currency . " + 
				"          } " +
				//"        OPTIONAL {" + 
				"            ?contractURI pc:location 		?locationURL . " + 
				"            ?locationURL rdfs:label 		?place . " + 
				//"          } " +  
				"    	  VALUES ?cpv { " + cpvValues + " }" +
				"		} UNION {" +
				"         ?contractURI 	rdf:type 			pc:Contract ;" + 
				"                      	pc:additionalObject	?cpva ;" +
				"					   	dcterms:title 		?title ." +
				"	 	 OPTIONAL " + 
				"	       { ?contractURI dcterms:description ?description } " +
				"        OPTIONAL " + 
				"          { ?contractURI pc:estimatedPrice	?priceURI . " + 
				"            ?priceURI gr:hasCurrencyValue 	?price . " + 
				"            ?priceURI gr:hasCurrency 		?currency . " + 
				"          } " +
				//"        OPTIONAL {" + 
				"            ?contractURI pc:location 		?locationURL . " + 
				"            ?locationURL rdfs:label 		?place . " + 
				//"          } " +  
				"    	  VALUES ?cpva { " + cpvValues + " }" +
				"  }" +
				"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(query);

		List<SimilarContractTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
			while (rs.hasNext()) {
				QuerySolution row = rs.next();
				table.add(new SimilarContractTableRow(row.get("contractURI").toString(),
						row.get("title").asLiteral().getString(),
						(row.get("description") != null ? row.get("description").asLiteral().getString() : ""),
						(row.get("price") != null ? row.get("price").asLiteral().getString() : "(Not specified)"),
						(row.get("currency") != null ? row.get("currency").toString() : ""),
						(row.get("place") != null ? row.get("place").asLiteral().getString() : "")));
			}
		} catch (Exception e) {
			throw new ServletException("Can't get similar contracts from public SPARQL endpoint " + config.getSparqlPublicQuery(),
					e);
		}
		return table;
	}

	/**
	 * Gets suitable suppliers for the given cpv codes
	 * 
	 * @param cpvString
	 * @throws ServletException
	 */
	protected List<SuitableSuppliersTableRow> getSuitableSuppliers(String cpvString) throws ServletException {

		// TODO get contracts cpvs from endpoint, not param
		String[] cpvs = cpvString.split("\\, ");

		Set<SuitableSuppliersTableRow> table = new HashSet<>();

		String cpvValues = "";
		String cpvValuesPattern = "<" + config.getPreference("cpvURL") + "%1$s> ";
		for (String cpv : cpvs) {
			cpvValues += String.format(cpvValuesPattern, cpv);
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT DISTINCT ?name (concat(?locality, \", \", ?country) as ?place) " +
				"				(concat(?street, \", \", ?locality, \", \", ?zip, \", \", ?country) as ?address)"+  
				"WHERE {{" + 
				"         ?contractURI rdf:type pc:Contract ;" + 
				"                      pc:mainObject ?cpv ;" + 
				"                      pc:awardedTender ?awardedTender." + 
				"         ?awardedTender pc:supplier ?supplierURI ." + 
				"         ?supplierURI rdfs:label ?name ;" + 
				"                      s:address ?addressURI ." + 
				"         ?addressURI  s:streetAddress ?street ;" +
				"					   s:postalCode ?zip ;" +
				"					   s:addressLocality ?locality ;" + 
				"                      s:addressCountry ?country ." +
				"    	  VALUES ?cpv { " + cpvValues + " }" +
				"		 } UNION {" +
				"         ?contractURI rdf:type pc:Contract ;" + 
				"                      pc:additionalObject ?cpva ;" + 
				"                      pc:awardedTender ?awardedTender." + 
				"         ?awardedTender pc:supplier ?supplierURI ." + 
				"         ?supplierURI rdfs:label ?name ;" + 
				"                      s:address ?addressURI ." + 
				"         ?addressURI  s:streetAddress ?street ;" +
				"					   s:postalCode ?zip ;" +
				"					   s:addressLocality ?locality ;" + 
				"                      s:addressCountry ?country ." +
				"    	  VALUES ?cpva { " + cpvValues + " }" +
				"	}" +
				"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(query);

		try {
			ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
			while (rs.hasNext()) {
				QuerySolution row = rs.next();
				table.add(new SuitableSuppliersTableRow(row.get("name").asLiteral().getString(), row.get("place").asLiteral()
						.getString(), row.get("address").asLiteral().getString()));
			}
		} catch (Exception e) {
			throw new ServletException("Can't get suitable suppliers from public SPARQL endpoint "
					+ config.getSparqlPublicQuery(), e);
		}

		try (Connection con = DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
				config.getRdbUsername(),
				config.getRdbPassword())) {
			PreparedStatement pst = con
					.prepareStatement("SELECT DISTINCT username, preference, value from `user_preferences` where username in ("
							+ "SELECT DISTINCT username FROM `user_preferences` WHERE "
							+ "(preference='cpv1' or preference='cpv2' or preference='cpv3') and "
							+ "(value = ? or value = ? or value= ?) ) "
							+ "and preference='businessName' or preference='businessPlace'");

			pst.setString(1, (cpvs.length > 0 ? cpvs[0] : "all"));
			pst.setString(2, (cpvs.length > 1 ? cpvs[1] : "all"));
			pst.setString(3, (cpvs.length > 2 ? cpvs[2] : "all"));
			java.sql.ResultSet rs = pst.executeQuery();

			HashMap<String, String> names = new HashMap<>();
			HashMap<String, String> places = new HashMap<>();

			while (rs.next()) {
				if (rs.getString("preference").equals("businessName")) {
					names.put(rs.getString("username"), rs.getString("value"));
				} else if (rs.getString("preference").equals("businessPlace")) {
					places.put(rs.getString("username"), rs.getString("value"));
				}
			}

			for (String username : names.keySet()) {
				table.add(new SuitableSuppliersTableRow(names.get(username), places.get(username), "")); // TODO address in RDB
			}

			return new ArrayList<SuitableSuppliersTableRow>(table);

		} catch (SQLException e) {
			throw new ServletException("Error finding suitable suppliers", e);
		}

	}

	/**
	 * Retrieves user's calls for tenders from private dataspace as a collection of table rows.
	 * 
	 * @param uc
	 *            UserContext of current user
	 * @throws ServletException
	 */
	protected List<SuitableTendersTableRow> getSuitableTendersAsTable(UserContext uc) throws ServletException {

		List<String> cpvs = new ArrayList<>();
		if (uc.getPreference("cpv1") != null) {
			cpvs.add(uc.getPreference("cpv1"));
		}
		if (uc.getPreference("cpv2") != null) {
			cpvs.add(uc.getPreference("cpv2"));
		}
		if (uc.getPreference("cpv3") != null) {
			cpvs.add(uc.getPreference("cpv3"));
		}

		String cpvValues = "";
		String cpvValuesPattern = "<" + config.getPreference("cpvURL") + "%1$s> ";
		for (String cpv : cpvs) {
			cpvValues += String.format(cpvValuesPattern, cpv);
		}

		// String now = null;
		// try {
		// now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toXMLFormat();
		// } catch (DatatypeConfigurationException unused) {
		// }

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT DISTINCT ?contractURI ?title ?description ?price ?currency ?cpv ?publicationDate ?deadline " + 
				"WHERE {{" + 
				"    ?contractURI rdf:type pc:Contract ." + 
				"    ?contractURI pc:mainObject ?cpv ." + 
				"    ?contractURI dcterms:title ?title ." + 
				"	 OPTIONAL " + 
				"	   { ?contractURI dcterms:description ?description } " +
				"    OPTIONAL" + 
				"      { ?contractURI pc:estimatedPrice ?priceURI ." + 
				"        ?priceURI gr:hasCurrencyValue ?price ." + 
				"        ?priceURI gr:hasCurrency ?currency" + 
				"      }" + 
				"    ?contractURI pc:location ?locationURL ." + 
				"    ?locationURL rdfs:label ?place ." + 
				"    ?contractURI pc:notice ?notice ." + 
				"    ?notice pc:publicationDate ?publicationDate ." +
				"    ?contractURI pc:tenderDeadline ?deadline . " + 
				"    VALUES ?cpv { " + cpvValues + " } " +
				"    FILTER NOT EXISTS { ?contractURI pc:awardedTender ?awardedTender }" +
				//"    FILTER ( ?deadline > \"" + now  + "\"^^xsd:dateTime )" + 		// TODO enable when we have fresh data
//				"	 } UNION {" +
//				"    ?contractURI rdf:type pc:Contract ." + 
//				"    ?contractURI pc:additionalObject ?cpv ." + 
//				"    ?contractURI dcterms:title ?title ." + 
//				"	 OPTIONAL " + 
//				"	   { ?contractURI dcterms:description ?description } " +
//				"    OPTIONAL" + 
//				"      { ?contractURI pc:estimatedPrice ?priceURI ." + 
//				"        ?priceURI gr:hasCurrencyValue ?price ." + 
//				"        ?priceURI gr:hasCurrency ?currency" + 
//				"      }" + 
//				"    ?contractURI pc:location ?locationURL ." + 
//				"    ?locationURL rdfs:label ?place ." + 
//				"    ?contractURI pc:notice ?notice ." + 
//				"    ?notice pc:publicationDate ?publicationDate ." +
//				"    ?contractURI pc:tenderDeadline ?deadline . " + 
//				"    VALUES ?cpv { " + cpvValues + " } " +
//				"    FILTER NOT EXISTS { ?contractURI pc:awardedTender ?awardedTender }" +
//				//"    FILTER ( ?deadline > \"" + now  + "\"^^xsd:dateTime )" + 		// TODO enable when we have fresh data
				"  }" + 
				"} ORDER BY DESC (?publicationDate)");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(query);

		List<SuitableTendersTableRow> table = new ArrayList<>();
		try {
			ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
			while (rs.hasNext()) {
				QuerySolution row = rs.next();
				// String cpv = "";
				// int cpvCount = 0;
				// if (row.get("cpv1URL") != null) {
				// cpvCount++;
				// cpv += row.get("cpv1URL").toString().substring(row.get("cpv1URL").toString().lastIndexOf('/') + 1);
				// }
				// if (row.get("cpv2URL") != null) {
				// if (cpvCount++ > 0) {
				// cpv += ", ";
				// }
				// cpv += row.get("cpv2URL").toString().substring(row.get("cpv2URL").toString().lastIndexOf('/') + 1);
				// }
				// if (row.get("cpv3URL") != null) {
				// if (cpvCount++ > 0) {
				// cpv += ", ";
				// }
				// cpv += row.get("cpv3URL").toString().substring(row.get("cpv3URL").toString().lastIndexOf('/') + 1);
				// }

				String cpv = row.get("cpv").toString().substring(row.get("cpv").toString().lastIndexOf('/') + 1);
				table.add(new SuitableTendersTableRow(row.get("contractURI").toString(),
						row.get("title").asLiteral().getString(),
						(row.get("description") != null ? row.get("description").asLiteral().getString() : ""),
						(row.get("price") != null ? row.get("price").asLiteral().getString() : ""),
						(row.get("currency") != null ? row.get("currency").toString() : ""),
						cpv,
						(row.get("publicationDate") != null ? row.get("publicationDate").toString() : ""),
						(row.get("deadline") != null ? row.get("deadline").toString() : "")));
			}
		} catch (Exception e) {
			throw new ServletException("Can't get users suitable open tenders from public SPARQL endpoint "
					+ config.getSparqlPublicQuery(), e);
		}
		return table;
	}

	/**
	 * Provides RESTful service via HTTP GET and POST.<br>
	 * <br>
	 * <b>Get similar events:</b><br>
	 * action=getSimilarContracts, specify contractURL, cpvString, from, to<br>
	 * <b>Get similar events pagecount:</b><br>
	 * action=getSimilarContractsPages, specify contractURL, cpvString<br>
	 * <br>
	 * <b>Get suitable suppliers:</b><br>
	 * action=getSuitableSuppliers, specify cpvString, from, to<br>
	 * <b>Get suitable suppliers pagecount:</b><br>
	 * action=getSuitableSuppliersPages, specify cpvString<br>
	 * <br>
	 * <b>Get suitable open tenders:</b><br>
	 * action=getSuitableTenders, specify from, to<br>
	 * <b>Get suitable open tenders pagecount:</b><br>
	 * action=getSuitableTendersPages<br>
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (isUserLoggedIn(request)) {
			String action = request.getParameter("action");
			if (action == null) {
				response.sendError(400);
				return;
			}

			HttpSession session = request.getSession(false);
			Gson gson = new Gson();

			List<SimilarContractTableRow> similarContracts = null;
			List<SimilarContractTableRow> cpage = null;

			List<SuitableSuppliersTableRow> suitableSuppliers = null;
			List<SuitableSuppliersTableRow> spage = null;

			List<SuitableTendersTableRow> suitableTenders = null;
			List<SuitableTendersTableRow> tpage = null;

			// TODO refactor - split into methods
			switch (action) {

				case "getSimilarContracts":
					if (!allDefined(request.getParameter("from"),
							request.getParameter("to"),
							request.getParameter("contractURL"),
							request.getParameter("cpvString"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						similarContracts = (List<SimilarContractTableRow>) session.getAttribute("similarContracts"
								+ DigestUtils.sha256Hex(request.getParameter("contractURL")));
						if (similarContracts == null) {
							similarContracts = getSimilarContractsAsTable(request.getParameter("contractURL"),
									request.getParameter("cpvString"));
							session.setAttribute("similarContracts" + DigestUtils.sha256Hex(request.getParameter("contractURL")),
									similarContracts);
						}
					}

					cpage = new ArrayList<>();
					int from = 0;
					int to = similarContracts.size();
					try {
						from = Integer.parseInt(request.getParameter("from"));
						to = Integer.parseInt(request.getParameter("to"));
					} catch (NumberFormatException unused) {
					}
					for (int i = from; i >= 0 && i < to && i < similarContracts.size(); i++) {
						cpage.add(similarContracts.get(i));
					}
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(gson.toJson(cpage));
					break;

				case "getSimilarContractsPages":
					if (!allDefined(request.getParameter("contractURL"), request.getParameter("cpvString"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						similarContracts = (List<SimilarContractTableRow>) session.getAttribute("similarContracts"
								+ DigestUtils.sha256Hex(request.getParameter("contractURL")));
						if (similarContracts == null) {
							similarContracts = getSimilarContractsAsTable(request.getParameter("contractURL"),
									request.getParameter("cpvString"));
							session.setAttribute("similarContracts" + DigestUtils.sha256Hex(request.getParameter("contractURL")),
									similarContracts);
						}
					}

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println((int) Math.ceil(similarContracts.size() / 10d)); // TODO length from uc
					break;

				case "getSuitableSuppliers":
					if (!allDefined(request.getParameter("from"), request.getParameter("to"), request.getParameter("cpvString"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						suitableSuppliers = (List<SuitableSuppliersTableRow>) session.getAttribute("suitableSuppliers"
								+ DigestUtils.sha256Hex(request.getParameter("cpvString")));
						if (suitableSuppliers == null) {
							suitableSuppliers = getSuitableSuppliers(request.getParameter("cpvString"));
							session.setAttribute("suitableSuppliers" + DigestUtils.sha256Hex(request.getParameter("cpvString")),
									suitableSuppliers);
						}
					}

					spage = new ArrayList<>();
					from = 0;
					to = suitableSuppliers.size();
					try {
						from = Integer.parseInt(request.getParameter("from"));
						to = Integer.parseInt(request.getParameter("to"));
					} catch (NumberFormatException unused) {
					}
					for (int i = from; i >= 0 && i < to && i < suitableSuppliers.size(); i++) {
						spage.add(suitableSuppliers.get(i));
					}
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(gson.toJson(spage));
					break;

				case "getSuitableSuppliersPages":
					if (!allDefined(request.getParameter("cpvString"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						suitableSuppliers = (List<SuitableSuppliersTableRow>) session.getAttribute("suitableSuppliers"
								+ DigestUtils.sha256Hex(request.getParameter("cpvString")));
						if (suitableSuppliers == null) {
							suitableSuppliers = getSuitableSuppliers(request.getParameter("cpvString"));
							session.setAttribute("suitableSuppliers" + DigestUtils.sha256Hex(request.getParameter("cpvString")),
									suitableSuppliers);
						}
					}

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println((int) Math.ceil(suitableSuppliers.size() / 10d)); // TODO length from uc
					break;

				case "getSuitableTenders":
					if (!allDefined(request.getParameter("from"), request.getParameter("to"))) {
						response.sendError(400);
						return;
					}

					synchronized (session) {
						suitableTenders = (List<SuitableTendersTableRow>) session.getAttribute("callsForTenders");
						if (suitableTenders == null) {
							suitableTenders = getSuitableTendersAsTable(getUserContext(request));
							session.setAttribute("suitableTenders", suitableTenders);
						}
					}

					tpage = new ArrayList<>();
					from = 0;
					to = suitableTenders.size();
					try {
						from = Integer.parseInt(request.getParameter("from"));
						to = Integer.parseInt(request.getParameter("to"));
					} catch (NumberFormatException unused) {
					}
					for (int i = from; i >= 0 && i < to && i < suitableTenders.size(); i++) {
						tpage.add(suitableTenders.get(i));
					}
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(gson.toJson(tpage));
					break;

				case "getSuitableTendersPages":
					synchronized (session) {
						suitableTenders = (List<SuitableTendersTableRow>) session.getAttribute("callsForTenders");
						if (suitableTenders == null) {
							suitableTenders = getSuitableTendersAsTable(getUserContext(request));
							session.setAttribute("callsForTenders", suitableTenders);
						}
					}

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println((int) Math.ceil(suitableTenders.size() / 10d)); // TODO length from uc
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
