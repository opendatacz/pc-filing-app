package cz.opendata.tenderstats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Google GSON na generovanie JSON
import com.google.gson.Gson;

// Apache Jena na pracu s RDF
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Demo component from "Ako na novu komponentu" tutorial in /tutorials/.
 * 
 * @author Matej Snoha
 */
public class DemoComponent extends AbstractComponent {

	// tuto triedu pouzijeme na rychle generovanie JSON v pozadovanom formate
	class ContractTableRow {

		String contractURI;
		String title;
		String price;
		String currency;

		public ContractTableRow(String contractURI, String title, String price, String currency) {
			this.contractURI = contractURI;
			this.title = title;
			this.price = price;
			this.currency = currency;
		}
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init(); // zabezpeci inicializaciu statickej premennej config s konfiguraciou komponenty z databazy
		// tento kod bude zavolany raz pri starte servletu este pred prvou HTTP poziadavkou
	}

	@Override
	public void destroy() {
		// tento kod bude zavolany pri vypnuti servletu, teda napriklad pri normalnom vypnuti Servlet containeru
		// (ako Tomcat), pri migracii servletu na iny stroj, pri nahradeni beziaceho servletu novou verziou, ...
		super.destroy(); // momentalne nerobi vobec nic, ale ak by v buducnosti bolo treba :)
	}

	@Override
	protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// tento kod bude zavolany pri HTTP poziadavke (z roznych vlaken pre rozne poziadavky)
		// request obsahuje HTTP poziadavku od klienta - URL, parametre, hlavicky, cookies, session ...
		// response sluzi na posielanie dat klientovi, ako napr. HTTP status kod, ine hlavicky, telo
		if (isUserLoggedIn(request)) {
			String action = request.getParameter("action");
			if (action == null) {
				response.sendError(400);
				return;
			}
			switch (action) {
				case "getPrivateContracts":
					List<ContractTableRow> contracts = getPrivateContracts(request);
					Gson gson = new Gson();
					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println(gson.toJson(contracts));
					break;
				default:
					response.sendError(400);
					break;
			}
		} else {
			response.sendError(403, "No user logged in.");
		}
	}

	// pripoji sa na SPARQL endpoint a vrati zakazky aktualne prihlaseneho uzivatela
	protected List<ContractTableRow> getPrivateContracts(HttpServletRequest request) {
		UserContext uc = getUserContext(request);
		/* @formatter:off */
		Query query = QueryFactory.create( 
				"PREFIX gr:       <http://purl.org/goodrelations/v1#>  " + 
				"PREFIX pc:       <http://purl.org/procurement/public-contracts#>  " + 
				"PREFIX dc:       <http://purl.org/dc/terms/>  " +  
				"SELECT ?contractURI ?title ?price ?currency " +
				"WHERE " + 
				"{ " + 
				"	GRAPH <" + uc.getNamedGraph() + "> " + 
				"	{ " + 
				"		?contractURI 	a	 				pc:Contract; " + 
				"						dc:title			?title; " + 
				"						pc:estimatedPrice	?priceURI " + 
				"		. " + 
				"		?priceURI		gr:hasCurrencyValue	?price; " + 
				"						gr:hasCurrency		?currency " + 
				"	} " + 
				"}");
		/* @formatter:on */
		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();
		List<ContractTableRow> table = new ArrayList<>();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			table.add(new ContractTableRow(row.get("contractURI").toString(), row.get("title").toString(), row.get("price")
					.toString(), row.get("currency").toString()));
		}
		return table;
	}
}
