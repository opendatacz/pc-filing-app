package cz.opendata.tenderstats.matchmaker;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimeZone;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.web.ServletRequestConfiguration;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import cz.opendata.tenderstats.AbstractComponent;
import cz.opendata.tenderstats.Geocoder;
import cz.opendata.tenderstats.UserContext;

import static cz.opendata.tenderstats.Prefixes.*;

/**
 * Component which handles matching events and suppliers.
 * 
 * @author Matej Snoha
 */
public class Matchmaker extends AbstractComponent {

	private static final long serialVersionUID = -6687104409088410200L;

	/**
	 * Holds some info about an event to be displayed as a similar events table row in frontend
	 * 
	 * @author Matej Snoha
	 */
	class SimilarContractTableRow implements Serializable {

		private static final long serialVersionUID = 674825197120238990L;

		String contractURL;
		String title;
		String description;
		String price;
		String currency;
		String place;
		double percent;
		String triplesURL;
		Map<Comparer, List<String>> comparerMessages;
		String country;
		double distance;
		String publicationDate;

		public SimilarContractTableRow(String contractURL, String title, String description, String price, String currency,
				String place, double percent, String triplesURL, Map<Comparer, List<String>> comparerMessages, String country,
				double distance, String publicationDate) {
			this.contractURL = contractURL;
			this.title = title;
			this.description = description;
			this.price = price;
			this.currency = currency;
			this.place = place;
			this.percent = percent;
			this.triplesURL = triplesURL;
			this.comparerMessages = comparerMessages;
			this.country = country;
			this.distance = distance;
			this.publicationDate = publicationDate;
		}
	}

	/**
	 * Holds some info about a supplier to be displayed as a suitable suppliers table row in frontend
	 * 
	 * @author Matej Snoha
	 */
	class SuitableSuppliersTableRow implements Serializable {

		private static final long serialVersionUID = 5466740279496631690L;

		String beURL;
		String name;
		String place;
		double percent;
		String triplesURL;
		long contracts;
		long contractsSameCPV;
		long volumeOfContracts;
		long volumeOfContractsSameCPV;
		long contractingAuthorities;
		long contractingAuthoritiesSameCPV;

		public SuitableSuppliersTableRow(String beURL, String name, String place, double percent, String triplesURL,
				long contracts, long contractsSameCPV, long volumeOfContracts, long volumeOfContractsSameCPV,
				long contractingAuthorities, long contractingAuthoritiesSameCPV) {
			this.beURL = beURL;
			this.name = name;
			this.place = place;
			this.percent = percent;
			this.triplesURL = triplesURL;
			this.contracts = contracts;
			this.contractsSameCPV = contractsSameCPV;
			this.volumeOfContracts = volumeOfContracts;
			this.volumeOfContractsSameCPV = volumeOfContractsSameCPV;
			this.contractingAuthorities = contractingAuthorities;
			this.contractingAuthoritiesSameCPV = contractingAuthoritiesSameCPV;
		}

	}

	@Override
	public void init() throws ServletException {
		super.init();
		Geocoder.loadCacheIfEmpty("cache/geocoder.cache");
	}

	@Override
	public void destroy() {
		Geocoder.saveCache("cache/geocoder.cache");
		super.destroy();
	}

	/**
	 * Returns private contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	protected Model getPrivateContract(String contractURI, String namedGraph) {
		/* @formatter:off */
		Query query = QueryFactory.create(
				config.getPreference("prefixes") + 
				"CONSTRUCT  " + 
				"  { ?contractURI ?p1 ?o1 . " + 
				"    ?o1 ?p2 ?o2 . " + 
				"    ?o2 ?p3 ?o3 . " + 
				"    ?o3 ?p4 ?o4 . " + 
				"    ?o4 ?p5 ?o5 .} " + 
				"FROM <" + namedGraph + "> " + 
				"WHERE " + 
				"  { ?contractURI ?p1 ?o1 . " + 
				"    ?contractURI a pc:Contract " + 
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
				"VALUES ?contractURI { <" + contractURI + "> }");
		/* @formatter:on */

		Model contract = null;
		try {
			contract = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();
			// System.out.println("###################################################");
			// contract.write(System.out, "Turtle");

		} catch (Exception e) {
		}
		return contract;
	};

	/**
	 * Returns public contract as JENA model
	 * 
	 * @param contractURI
	 */
	protected Model getPublicContract(String contractURI) {
		/* @formatter:off */
		Query query = QueryFactory.create(
				config.getPreference("prefixes") + 
				"CONSTRUCT  " + 
				"  { ?contractURI ?p1 ?o1 . " + 
				"    ?o1 ?p2 ?o2 . " + 
				"    ?o2 ?p3 ?o3 . " + 
				"    ?o3 ?p4 ?o4 . " + 
				"    ?o4 ?p5 ?o5 .} " + 
				"WHERE " + 
				"  { ?contractURI ?p1 ?o1 . " + 
				"    ?contractURI a pc:Contract " + 
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
				"VALUES ?contractURI { <" + contractURI + "> }");
		/* @formatter:on */

		Model contract = null;
		try {
			contract = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execConstruct();
		} catch (Exception e) {
		}
		// if (contract != null) {
		// System.out.print('.');
		// } else {
		// System.out.print("#"/* (" + contractURI + ")" */);
		// }

		// System.out.println("###################################################");
		// contract.write(System.out, "Turtle");
		return contract;
	};

	protected List<String> getPossiblySimilarContractUrls(Contract base) throws ServletException {
		return getPossiblySimilarContractUrls(base, 1_000_000, 2 * 365);
	}

	/**
	 * Retrieves URLs of contracts similar to the one specified
	 * 
	 * @param base
	 * @return
	 * @throws ServletException
	 */
	protected List<String> getPossiblySimilarContractUrls(Contract base, long limit, int publicationDateRangeDays)
			throws ServletException {

		List<String> urls = new ArrayList<>();

		try {
			Set<String> cpvStart = new HashSet<>();

			String mainCpv =
					base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(PC, "mainObject")).getResource().toString();
			cpvStart.add(mainCpv.substring(mainCpv.lastIndexOf('/') + 1, mainCpv.lastIndexOf('/') + 4));

			NodeIterator it =
					base.rdf.listObjectsOfProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(PC, "additionalObject"));
			while (it.hasNext()) {
				for (RDFNode additionalCpvNode : it.toSet()) {
					String additionalCpv = additionalCpvNode.asResource().toString();
					cpvStart.add(additionalCpv.substring(additionalCpv.lastIndexOf('/') + 1, additionalCpv.lastIndexOf('/') + 4));
				}
			}

			String cpvStartStr = new String();
			for (String cpv : cpvStart) {
				cpvStartStr += " \"" + config.getPreference("cpvURL") + cpv + "\"";
			}

			String lowDate = "";
			String highDate = "";
			if (publicationDateRangeDays > 0) {
				String baseDate = null;
				if (base.rdf
						.contains(ResourceFactory.createResource(base.getUrl()), ResourceFactory.createProperty(PC, "notice"))) {
					Resource baseDateResource =
							base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
									ResourceFactory.createProperty(PC, "notice")).getResource();
					if (base.rdf.contains(baseDateResource, ResourceFactory.createProperty(PC, "publicationDate"))) {
						baseDate =
								base.rdf.getProperty(baseDateResource, ResourceFactory.createProperty(PC, "publicationDate"))
										.getString();
					}
				}
				Calendar cal;
				if (baseDate != null) {
					cal = DatatypeConverter.parseDate(baseDate);
				} else {
					cal = new GregorianCalendar(); // current time if not published
				}
				cal.add(Calendar.DATE, -publicationDateRangeDays);
				cal.setTimeZone(TimeZone.getTimeZone("GMT"));
				lowDate = DatatypeConverter.printDate(cal);
				if (baseDate != null) {
					cal = DatatypeConverter.parseDate(baseDate);
				} else {
					cal = new GregorianCalendar(); // current time if not published
				}
				cal.add(Calendar.DATE, publicationDateRangeDays);
				cal.setTimeZone(TimeZone.getTimeZone("GMT"));
				highDate = DatatypeConverter.printDate(cal);
			}

			/* @formatter:off */
//			Query query = QueryFactory.create( 
//					config.getPreference("prefixes") +
//					"SELECT DISTINCT ?url " + 
//					"WHERE {" +
//					"{" +
//					"    ?url rdf:type pc:Contract ;" + 
//					"         pc:mainObject ?mainCpv ." + 
//					"    OPTIONAL { ?url pc:notice ?notice . " +
//					"				?notice pc:publicationDate ?publicationDate . }" +
//					"    FILTER ( " +
//					"	   ?publicationDate >= \"" + lowDate + "\"^^xsd:date &&" +
//					"	   ?publicationDate <= \"" + highDate + "\"^^xsd:date" +
//					"	 )" +
//					"}" +
//					"    OPTIONAL { ?url pc:additionalObject ?additionalCpv . }" + 
//					"    FILTER (" + 
//					"      STRSTARTS(STR(?mainCpv), ?cpvStart) ||" + 
//					"      STRSTARTS(STR(?additionalCpv), ?cpvStart)" + 
//					"    )" +
//					"    VALUES ?cpvStart { " + cpvStartStr + " }" +
//					"} LIMIT " + limit);
			
			Query query = QueryFactory.create( 
					config.getPreference("prefixes") +
					"SELECT DISTINCT ?url " + 
					"WHERE {" + 
					//"  GRAPH <http://ld.opendata.cz/tenderstats/dataset/isvzus.cz> {" +
					(publicationDateRangeDays > 0 ? "{" : "") +
					"    ?url pc:mainObject ?mainCpv ;" +
					"		  a pc:Contract ." + 
					"    OPTIONAL { ?url pc:additionalObject ?additionalCpv . }" + 
					"    FILTER (" + 
					"      STRSTARTS(STR(?mainCpv), ?cpvStart) ||" + 
					"      STRSTARTS(STR(?additionalCpv), ?cpvStart)" + 
					"    )" +
					"    VALUES ?cpvStart { " + cpvStartStr + " }" + 
					(publicationDateRangeDays > 0 ?
					"}   OPTIONAL { ?url pc:notice ?notice . " +
					"				?notice pc:publicationDate ?publicationDate . }" + 
					"    FILTER ( " +
					"	   ?publicationDate >= \"" + lowDate + "\"^^xsd:date &&" +
					"	   ?publicationDate <= \"" + highDate + "\"^^xsd:date" +
					"	 )" +
					"" 
					: "" ) +
					//"  }" + 
					"} LIMIT " + limit);
			/* @formatter:on */

			System.out.println("###################################################");
			System.out.println(query);

			ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
			while (rs.hasNext()) {
				QuerySolution row = rs.next();
				urls.add(row.get("url").asResource().toString());
			}
		} catch (Exception e) {
			throw new ServletException("Can't get similar contracts urls from public SPARQL endpoint "
					+ config.getSparqlPublicQuery(), e);
		}

		return urls;
	}

	protected BusinessEntity getAwardedSupplier(Contract contract) { // TODO checks using contains ?
		try {
			Resource awardedTenderResource =
					contract.getRdf()
							.getProperty(ResourceFactory.createResource(contract.getUrl()),
									ResourceFactory.createProperty(PC, "awardedTender")).getResource();

			Resource businessEntityResource =
					contract.getRdf().getProperty(awardedTenderResource, ResourceFactory.createProperty(PC, "supplier"))
							.getResource();

			String name =
					contract.getRdf().getProperty(businessEntityResource, ResourceFactory.createProperty(GR, "legalName"))
							.getString();

			String url = businessEntityResource.getURI();

			String place = "";
			try {
				Resource addressResource =
						contract.getRdf().getProperty(businessEntityResource, ResourceFactory.createProperty(S, "address"))
								.getResource();
				place =
						contract.getRdf().getProperty(addressResource, ResourceFactory.createProperty(S, "addressLocality"))
								.getString();
				place += ", ";
				place +=
						contract.getRdf().getProperty(addressResource, ResourceFactory.createProperty(S, "addressCountry"))
								.getString();
			} catch (Exception e) {
			}

			return new BusinessEntity(name, url, place);

		} catch (Exception e) {
			return null;
		}
	}

	// public void benchmarkContractStore(String csName, Collection<Contract> data, boolean warmup) {
	//
	// String packageName = getClass().getPackage().getName();
	// ContractStore cs = null;
	// try {
	// cs = (ContractStore) Class.forName(packageName + "." + csName).newInstance();
	// } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
	// }
	//
	// long startTime = 0;
	// long endTime = 0;
	//
	// // addAll
	// if (!warmup) {
	// startTime = System.currentTimeMillis();
	// }
	// // for (int i = 0; i < 10; i++) {
	// cs.addAll(data);
	// // }
	// if (!warmup) {
	// endTime = System.currentTimeMillis();
	// System.out.println("[" + csName + "] addAll in " + (endTime - startTime) + " ms");
	// }
	//
	// // sort
	// if (!warmup) {
	// startTime = System.currentTimeMillis();
	// }
	// cs.sort();
	// if (!warmup) {
	// endTime = System.currentTimeMillis();
	// System.out.println("[" + csName + "] sort in " + (endTime - startTime) + " ms");
	// }
	//
	// if (csName.equals("ArrayContractStore")) {
	// return;
	// }
	//
	// // iterator.remove all
	// if (!warmup) {
	// startTime = System.currentTimeMillis();
	// }
	// Iterator<Contract> it = cs.iterator();
	// while (it.hasNext()) {
	// it.next();
	// it.remove();
	// }
	// if (!warmup) {
	// endTime = System.currentTimeMillis();
	// System.out.println("[" + csName + "] iterator.remove in " + (endTime - startTime) + " ms");
	// }
	// }

	/**
	 * Retrieves contracts similar to the one specified.
	 * 
	 * @throws ServletException
	 */
	protected List<SimilarContractTableRow> getSimilarContracts(HttpServletRequest request) throws ServletException {

		ServletRequestConfiguration requestConfig = new ServletRequestConfiguration(request);

		long startTotalTime = System.currentTimeMillis();

		long startTime;
		long endTime;

		// parse request
		UserContext uc = getUserContext(request);
		String baseUrl = request.getParameter("contractURL");

		System.out.println("###################################################");

		// get base contract
		startTime = System.currentTimeMillis();
		Model baseRdf = getPrivateContract(baseUrl, uc.getNamedGraph());
		Contract base = new Contract(baseUrl, baseRdf);
		endTime = System.currentTimeMillis();
		System.out.println("base contract retrieved in [" + (endTime - startTime) + " ms]");

		// get list of promising urls to compare with
		startTime = System.currentTimeMillis();
		List<String> urls = getPossiblySimilarContractUrls(base, 300 /* Long.parseLong(request.getParameter("limit")) */, // TODO
				0 /* Integer.parseInt(request.getParameter("publicationDateRangeDays")) */);
		endTime = System.currentTimeMillis();
		System.out.println(urls.size() + " similar contracts found in [" + (endTime - startTime) + " ms]");

		// fetch all level 0 data about promising contracts
		startTime = System.currentTimeMillis();

		// basic contract store
		ContractStore cs = new LinkedContractStore();

		for (String url : urls) {
			Model rdf = getPublicContract(url);
			if (rdf != null) {
				cs.add(new Contract(url, rdf));
			}
			// System.out.println(url);
		}
		endTime = System.currentTimeMillis();
		System.out.println("level 0 linked data retrieved in [" + (endTime - startTime) + " ms]");
		// System.out.println("# of promising contracts: " + cs.size());

		startTime = System.currentTimeMillis();
		Comparer tdc = new TenderDeadlineComparer(requestConfig);
		tdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TenderDeadlineComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("TenderDeadlineComparer.min"), request.getParameter("TenderDeadlineComparer.max"))) {
			cs.retain(tdc,
					Double.parseDouble(request.getParameter("TenderDeadlineComparer.min")),
					Double.parseDouble(request.getParameter("TenderDeadlineComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer pdc = new PublicationDateComparer(requestConfig);
		pdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("PublicationDateComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("PublicationDateComparer.min"), request.getParameter("PublicationDateComparer.max"))) {
			cs.retain(pdc,
					Double.parseDouble(request.getParameter("PublicationDateComparer.min")),
					Double.parseDouble(request.getParameter("PublicationDateComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer cpvc = new CPVComparer(requestConfig);
		cpvc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("CPVComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("CPVComparer.min"), request.getParameter("CPVComparer.max"))) {
			cs.retain(cpvc,
					Double.parseDouble(request.getParameter("CPVComparer.min")),
					Double.parseDouble(request.getParameter("CPVComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer gdc = new GeoDistanceComparer(requestConfig);
		gdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("GeoDistanceComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("GeoDistanceComparer.min"), request.getParameter("GeoDistanceComparer.max"))) {
			cs.retain(gdc,
					Double.parseDouble(request.getParameter("GeoDistanceComparer.min")),
					Double.parseDouble(request.getParameter("GeoDistanceComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer txtc = new TextComparer(requestConfig);
		txtc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TextComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("TextComparer.min"), request.getParameter("TextComparer.max"))) {
			cs.retain(txtc,
					Double.parseDouble(request.getParameter("TextComparer.min")),
					Double.parseDouble(request.getParameter("TextComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		// sort according to total score
		startTime = System.currentTimeMillis();
		cs.sortDescending();
		endTime = System.currentTimeMillis();
		System.out.println("sort in [" + (endTime - startTime) + " ms]");

		double maxScore = 0;
		for (Comparer c : cs.getComparers()) {
			maxScore += c.getWeight();
		}

		// prepare results
		List<SimilarContractTableRow> table = new ArrayList<>();

		for (Contract c : cs) {

			double percent = (int) (1000 * c.getTotalScore() / maxScore) / 10f;

			String triplesURL =
					"http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
							+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+.+}"
							+ "+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
							+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C" + c.getUrl()
							+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain";

			Map<Comparer, List<String>> comparerMessages = new HashMap<>();
			for (Comparer comparer : c.getScores().keySet()) {
				List<String> attributes = new ArrayList<>();
				attributes.add(new Double(comparer.getWeight()).toString()); // weight
				attributes.add(String.valueOf((int) (c.getScore(comparer) * 100))); // score
				attributes.add(c.getMessage(comparer)); // message
				comparerMessages.put(comparer, attributes);
			}

			String title = "";
			try {
				title =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(DCTERMS, "title")).getString();
			} catch (Exception e) {
			}

			String description = "";
			try {
				description =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(DCTERMS, "description")).getString();
			} catch (Exception e) {
			}

			String price = "";
			String currency = "";
			try {
				Resource priceURL =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "estimatedPrice")).getResource();
				price = c.getRdf().getProperty(priceURL, ResourceFactory.createProperty(GR, "hasCurrencyValue")).getString();
				currency = c.getRdf().getProperty(priceURL, ResourceFactory.createProperty(GR, "hasCurrency")).getString();
			} catch (Exception e) {
			}

			String place = "";
			try {
				Resource placeUrl =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "location")).getResource();
				place = c.getRdf().getProperty(placeUrl, ResourceFactory.createProperty(RDFS, "label")).getString();
			} catch (Exception e) {
			}

			String country = "";
			try {
				Resource authorityResource =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "contractingAuthority")).getResource();
				Resource addressResource =
						c.getRdf().getProperty(authorityResource, ResourceFactory.createProperty(S, "address")).getResource();
				country =
						c.getRdf().getProperty(addressResource, ResourceFactory.createProperty(S, "addressCountry")).getString();
			} catch (Exception e) {
			}

			// TODO find a better way ?
			double distance = Double.MAX_VALUE;
			try {
				Pattern distancePattern = Pattern.compile("distance (.*) km.*");
				Matcher distanceMatcher = distancePattern.matcher(c.getMessage(new GeoDistanceComparer()));
				if (distanceMatcher.matches()) {
					distance = Double.parseDouble(distanceMatcher.group(1));
				}
			} catch (Exception e) {
			}

			String publicationDate = "";
			if (c.getRdf().contains(ResourceFactory.createResource(c.getUrl()), ResourceFactory.createProperty(PC, "notice"))) {
				Resource otherDateResource =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "notice")).getResource();
				if (c.getRdf().contains(otherDateResource, ResourceFactory.createProperty(PC, "publicationDate"))) {
					publicationDate =
							c.getRdf().getProperty(otherDateResource, ResourceFactory.createProperty(PC, "publicationDate"))
									.getString();
				}
			}

			table.add(new SimilarContractTableRow(c.getUrl(),
					title,
					description,
					price,
					currency,
					place,
					percent,
					triplesURL,
					comparerMessages,
					country,
					distance,
					publicationDate));
		}

		long endTotalTime = System.currentTimeMillis();
		System.out.println("total runtime [" + (endTotalTime - startTotalTime) + " ms]");

		return table;

	}

	/**
	 * Retrieves contracts similar to the one specified.<br>
	 * 
	 * @throws ServletException
	 */
	// TODO refactor
	protected List<SimilarContractTableRow> getSimilarContracts(Model baseRdf) throws ServletException {

		long startTotalTime = System.currentTimeMillis();

		long startTime;
		long endTime;

		System.out.println("###################################################");

		startTime = System.currentTimeMillis();
		Contract base = new Contract(null, baseRdf);
		endTime = System.currentTimeMillis();

		// get list of promising urls to compare with
		startTime = System.currentTimeMillis();
		List<String> urls = getPossiblySimilarContractUrls(base, 300 /* Long.parseLong(request.getParameter("limit")) */, // TODO
				0 /* Integer.parseInt(request.getParameter("publicationDateRangeDays")) */);
		endTime = System.currentTimeMillis();
		System.out.println(urls.size() + " similar contracts found in [" + (endTime - startTime) + " ms]");

		// fetch all level 0 data about promising contracts
		startTime = System.currentTimeMillis();

		// basic contract store
		ContractStore cs = new LinkedContractStore();

		for (String url : urls) {
			Model rdf = getPublicContract(url);
			if (rdf != null) {
				cs.add(new Contract(url, rdf));
			}
			// System.out.println(url);
		}
		endTime = System.currentTimeMillis();
		System.out.println("level 0 linked data retrieved in [" + (endTime - startTime) + " ms]");
		// System.out.println("# of promising contracts: " + cs.size());

		startTime = System.currentTimeMillis();
		Comparer tdc = new TenderDeadlineComparer();
		tdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TenderDeadlineComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer pdc = new PublicationDateComparer();
		pdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("PublicationDateComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer cpvc = new CPVComparer();
		cpvc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("CPVComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer gdc = new GeoDistanceComparer();
		gdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("GeoDistanceComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer txtc = new TextComparer();
		txtc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TextComparer in [" + (endTime - startTime) + " ms]");

		// sort according to total score
		startTime = System.currentTimeMillis();
		cs.sortDescending();
		endTime = System.currentTimeMillis();
		System.out.println("sort in [" + (endTime - startTime) + " ms]");

		double maxScore = 0;
		for (Comparer c : cs.getComparers()) {
			maxScore += c.getWeight();
		}

		// prepare results
		List<SimilarContractTableRow> table = new ArrayList<>();

		for (Contract c : cs) {

			double percent = (int) (1000 * c.getTotalScore() / maxScore) / 10f;

			String triplesURL =
					"http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
							+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+.+}"
							+ "+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
							+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C" + c.getUrl()
							+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain";

			Map<Comparer, List<String>> comparerMessages = new HashMap<>();
			for (Comparer comparer : c.getScores().keySet()) {
				List<String> attributes = new ArrayList<>();
				attributes.add(new Double(comparer.getWeight()).toString()); // weight
				attributes.add(String.valueOf((int) (c.getScore(comparer) * 100))); // score
				attributes.add(c.getMessage(comparer)); // message
				comparerMessages.put(comparer, attributes);
			}

			String title = "";
			try {
				title =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(DCTERMS, "title")).getString();
			} catch (Exception e) {
			}

			String description = "";
			try {
				description =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(DCTERMS, "description")).getString();
			} catch (Exception e) {
			}

			String price = "";
			String currency = "";
			try {
				Resource priceURL =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "estimatedPrice")).getResource();
				price = c.getRdf().getProperty(priceURL, ResourceFactory.createProperty(GR, "hasCurrencyValue")).getString();
				currency = c.getRdf().getProperty(priceURL, ResourceFactory.createProperty(GR, "hasCurrency")).getString();
			} catch (Exception e) {
			}

			String place = "";
			try {
				Resource placeUrl =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "location")).getResource();
				place = c.getRdf().getProperty(placeUrl, ResourceFactory.createProperty(RDFS, "label")).getString();
			} catch (Exception e) {
			}

			String country = "";
			try {
				Resource authorityResource =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "contractingAuthority")).getResource();
				Resource addressResource =
						c.getRdf().getProperty(authorityResource, ResourceFactory.createProperty(S, "address")).getResource();
				country =
						c.getRdf().getProperty(addressResource, ResourceFactory.createProperty(S, "addressCountry")).getString();
			} catch (Exception e) {
			}

			// TODO find a better way ?
			double distance = Double.MAX_VALUE;
			try {
				Pattern distancePattern = Pattern.compile("distance (.*) km.*");
				Matcher distanceMatcher = distancePattern.matcher(c.getMessage(new GeoDistanceComparer()));
				if (distanceMatcher.matches()) {
					distance = Double.parseDouble(distanceMatcher.group(1));
				}
			} catch (Exception e) {
			}

			String publicationDate = "";
			if (c.getRdf().contains(ResourceFactory.createResource(c.getUrl()), ResourceFactory.createProperty(PC, "notice"))) {
				Resource otherDateResource =
						c.getRdf()
								.getProperty(ResourceFactory.createResource(c.getUrl()),
										ResourceFactory.createProperty(PC, "notice")).getResource();
				if (c.getRdf().contains(otherDateResource, ResourceFactory.createProperty(PC, "publicationDate"))) {
					publicationDate =
							c.getRdf().getProperty(otherDateResource, ResourceFactory.createProperty(PC, "publicationDate"))
									.getString();
				}
			}

			table.add(new SimilarContractTableRow(c.getUrl(),
					title,
					description,
					price,
					currency,
					place,
					percent,
					triplesURL,
					comparerMessages,
					country,
					distance,
					publicationDate));
		}

		long endTotalTime = System.currentTimeMillis();
		System.out.println("total runtime [" + (endTotalTime - startTotalTime) + " ms]");

		return table;

	}

	/**
	 * Gets suitable suppliers for the given cpv codes
	 * 
	 * @param cpvString
	 * @throws ServletException
	 */
	protected List<SuitableSuppliersTableRow> getSuitableSuppliers(HttpServletRequest request) throws ServletException {

		// TODO isolate duplicate code with similar contracts
		ServletRequestConfiguration requestConfig = new ServletRequestConfiguration(request);

		long startTotalTime = System.currentTimeMillis();

		long startTime;
		long endTime;

		// parse request
		UserContext uc = getUserContext(request);
		String baseUrl = request.getParameter("contractURL");

		System.out.println("###################################################");

		// get base contract
		startTime = System.currentTimeMillis();
		Model baseRdf = getPrivateContract(baseUrl, uc.getNamedGraph());
		Contract base = new Contract(baseUrl, baseRdf);
		endTime = System.currentTimeMillis();
		System.out.println("base contract retrieved in [" + (endTime - startTime) + " ms]");

		// get list of promising urls to compare with
		startTime = System.currentTimeMillis();
		List<String> urls = getPossiblySimilarContractUrls(base, 300 /* Long.parseLong(request.getParameter("limit")) */, // TODO
				3650 /* Integer.parseInt(request.getParameter("publicationDateRangeDays")) */);
		endTime = System.currentTimeMillis();
		System.out.println(urls.size() + " similar contracts found in [" + (endTime - startTime) + " ms]");

		// fetch all level 0 data about promising contracts
		startTime = System.currentTimeMillis();

		// basic contract store
		ContractStore cs = new LinkedContractStore();

		for (String url : urls) {
			Model rdf = getPublicContract(url);
			if (rdf != null) {
				cs.add(new Contract(url, rdf));
			}
			// System.out.println(url);
		}
		endTime = System.currentTimeMillis();
		System.out.println("level 0 linked data retrieved in [" + (endTime - startTime) + " ms]");
		// System.out.println("# of promising contracts: " + cs.size());

		startTime = System.currentTimeMillis();
		Comparer tdc = new TenderDeadlineComparer(requestConfig);
		tdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TenderDeadlineComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("TenderDeadlineComparer.min"), request.getParameter("TenderDeadlineComparer.max"))) {
			cs.retain(tdc,
					Double.parseDouble(request.getParameter("TenderDeadlineComparer.min")),
					Double.parseDouble(request.getParameter("TenderDeadlineComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer pdc = new PublicationDateComparer(requestConfig);
		pdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("PublicationDateComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("PublicationDateComparer.min"), request.getParameter("PublicationDateComparer.max"))) {
			cs.retain(pdc,
					Double.parseDouble(request.getParameter("PublicationDateComparer.min")),
					Double.parseDouble(request.getParameter("PublicationDateComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer cpvc = new CPVComparer(requestConfig);
		cpvc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("CPVComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("CPVComparer.min"), request.getParameter("CPVComparer.max"))) {
			cs.retain(cpvc,
					Double.parseDouble(request.getParameter("CPVComparer.min")),
					Double.parseDouble(request.getParameter("CPVComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer gdc = new GeoDistanceComparer(requestConfig);
		gdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("GeoDistanceComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("GeoDistanceComparer.min"), request.getParameter("GeoDistanceComparer.max"))) {
			cs.retain(gdc,
					Double.parseDouble(request.getParameter("GeoDistanceComparer.min")),
					Double.parseDouble(request.getParameter("GeoDistanceComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		startTime = System.currentTimeMillis();
		Comparer txtc = new TextComparer(requestConfig);
		txtc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TextComparer in [" + (endTime - startTime) + " ms]");
		if (allDefined(request.getParameter("TextComparer.min"), request.getParameter("TextComparer.max"))) {
			cs.retain(txtc,
					Double.parseDouble(request.getParameter("TextComparer.min")),
					Double.parseDouble(request.getParameter("TextComparer.max")));
			System.out.println("# of filtered contracts: " + cs.size());
		}

		Map<BusinessEntity, Double> scores = new HashMap<>();

		startTime = System.currentTimeMillis();
		for (Contract c : cs) {
			BusinessEntity be = getAwardedSupplier(c);
			if (be != null) {
				Double oldScore = scores.get(be);
				Double newScore = 0d;
				if (oldScore != null) {
					newScore = Math.max(1, oldScore + c.getTotalScore());
				} else {
					newScore = c.getTotalScore();
				}
				scores.put(be, newScore);
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("Awarded suppliers in [" + (endTime - startTime) + " ms]");
		System.out.println("# of unique suppliers: " + scores.size());
		System.out.println();

		startTime = System.currentTimeMillis();
		List<Map.Entry<BusinessEntity, Double>> entries = new ArrayList<>(scores.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<BusinessEntity, Double>>() {

			public int compare(Map.Entry<BusinessEntity, Double> a, Map.Entry<BusinessEntity, Double> b) {
				return -a.getValue().compareTo(b.getValue());
			}
		});
		Map<BusinessEntity, Double> sortedScores = new LinkedHashMap<BusinessEntity, Double>();
		for (Map.Entry<BusinessEntity, Double> entry : entries) {
			sortedScores.put(entry.getKey(), entry.getValue());
		}
		endTime = System.currentTimeMillis();
		System.out.println("sort in [" + (endTime - startTime) + " ms]");

		double maxScore = 0;
		for (Comparer c : cs.getComparers()) {
			maxScore += c.getWeight();
		}

		// remove the actual supplier (for measuring effectiveness)
		// TODO the following 2 lines above max score computation?
		BusinessEntity awardedSupplier = getAwardedSupplier(base);
		scores.remove(awardedSupplier);

		Set<String> cpvSet = new HashSet<>();
		cpvSet.add(base.rdf
				.getProperty(ResourceFactory.createResource(base.getUrl()), ResourceFactory.createProperty(PC, "mainObject"))
				.getResource().toString());
		NodeIterator it =
				base.rdf.listObjectsOfProperty(ResourceFactory.createResource(base.getUrl()),
						ResourceFactory.createProperty(PC, "additionalObject"));
		while (it.hasNext()) {
			for (RDFNode additionalCpvNode : it.toSet()) {
				cpvSet.add(additionalCpvNode.asResource().toString());
			}
		}
		List<String> cpvs = new ArrayList<String>(cpvSet);

		List<SuitableSuppliersTableRow> table = new ArrayList<>();

		List<BusinessEntity> suppliers = new ArrayList<>(sortedScores.keySet());

		// TODO
		if (request.getParameter("additionalMetrics").equals("true")) {
			// suppliers = suppliers.subList(0, 10);
		} else {
			suppliers.clear();
		}

		startTime = System.currentTimeMillis();

		Map<String, Long> contracts = getSuppliersContracts(suppliers);
		Map<String, Long> contractsSameCPV = getSuppliersContractsSameCPV(suppliers, cpvs);

		Map<String, Long> volumeOfContracts = getSuppliersVolumeOfContracts(suppliers);
		Map<String, Long> volumeOfContractsSameCPV = getSuppliersVolumeOfContractsSameCPV(suppliers, cpvs);

		Map<String, Long> contractingAuthorities = getContractingAuthorities(suppliers);
		Map<String, Long> contractingAuthoritiesSameCPV = getContractingAuthoritiesSameCPV(suppliers, cpvs);

		endTime = System.currentTimeMillis();
		System.out.println("additional metrics in [" + (endTime - startTime) + " ms]");

		// process additional metrics for suppliers
		for (BusinessEntity supplier : sortedScores.keySet() /* suppliers */) {
			String triples =
					"http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
							+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+.+}"
							+ "+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
							+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C" + supplier.url
							+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain";

			double percent = (int) (1000 * sortedScores.get(supplier) / maxScore) / 10f;

			table.add(new SuitableSuppliersTableRow(supplier.url,
					supplier.name,
					supplier.place,
					percent,
					triples,
					contracts.containsKey(supplier.url) ? contracts.get(supplier.url) : 0,
					contractsSameCPV.containsKey(supplier.url) ? contractsSameCPV.get(supplier.url) : 0,
					volumeOfContracts.containsKey(supplier.url) ? volumeOfContracts.get(supplier.url) : 0,
					volumeOfContractsSameCPV.containsKey(supplier.url) ? volumeOfContractsSameCPV.get(supplier.url) : 0,
					contractingAuthorities.containsKey(supplier.url) ? contractingAuthorities.get(supplier.url) : 0,
					contractingAuthoritiesSameCPV.containsKey(supplier.url) ? contractingAuthoritiesSameCPV.get(supplier.url) : 0));
		}

		long endTotalTime = System.currentTimeMillis();
		System.out.println("total runtime [" + (endTotalTime - startTotalTime) + " ms]");

		return table;

	}

	/**
	 * Gets suitable suppliers for the given cpv codes
	 * 
	 * @param cpvString
	 * @throws ServletException
	 */
	// TODO refactor & update
	protected List<SuitableSuppliersTableRow> getSuitableSuppliers(Model baseRdf) throws ServletException {

		long startTotalTime = System.currentTimeMillis();

		long startTime;
		long endTime;

		System.out.println("###################################################");

		// get base contract
		startTime = System.currentTimeMillis();
		Contract base = new Contract(null, baseRdf);
		endTime = System.currentTimeMillis();

		// get list of promising urls to compare with
		startTime = System.currentTimeMillis();
		List<String> urls = getPossiblySimilarContractUrls(base, 300 /* Long.parseLong(request.getParameter("limit")) */, // TODO
				3650 /* Integer.parseInt(request.getParameter("publicationDateRangeDays")) */);
		endTime = System.currentTimeMillis();
		System.out.println(urls.size() + " similar contracts found in [" + (endTime - startTime) + " ms]");

		// fetch all level 0 data about promising contracts
		startTime = System.currentTimeMillis();

		// basic contract store
		ContractStore cs = new LinkedContractStore();

		for (String url : urls) {
			Model rdf = getPublicContract(url);
			if (rdf != null) {
				cs.add(new Contract(url, rdf));
			}
			// System.out.println(url);
		}
		endTime = System.currentTimeMillis();
		System.out.println("level 0 linked data retrieved in [" + (endTime - startTime) + " ms]");
		// System.out.println("# of promising contracts: " + cs.size());

		startTime = System.currentTimeMillis();
		Comparer tdc = new TenderDeadlineComparer();
		tdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TenderDeadlineComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer pdc = new PublicationDateComparer();
		pdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("PublicationDateComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer cpvc = new CPVComparer();
		cpvc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("CPVComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer gdc = new GeoDistanceComparer();
		gdc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("GeoDistanceComparer in [" + (endTime - startTime) + " ms]");

		startTime = System.currentTimeMillis();
		Comparer txtc = new TextComparer();
		txtc.runVerbose(base, cs);
		endTime = System.currentTimeMillis();
		System.out.println("TextComparer in [" + (endTime - startTime) + " ms]");

		Map<BusinessEntity, Double> scores = new HashMap<>();

		startTime = System.currentTimeMillis();
		for (Contract c : cs) {
			BusinessEntity be = getAwardedSupplier(c);
			if (be != null) {
				Double oldScore = scores.get(be);
				Double newScore = 0d;
				if (oldScore != null) {
					newScore = Math.max(1, oldScore + c.getTotalScore());
				} else {
					newScore = c.getTotalScore();
				}
				scores.put(be, newScore);
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("Awarded suppliers in [" + (endTime - startTime) + " ms]");
		System.out.println("# of unique suppliers: " + scores.size());
		System.out.println();

		startTime = System.currentTimeMillis();
		List<Map.Entry<BusinessEntity, Double>> entries = new ArrayList<>(scores.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<BusinessEntity, Double>>() {

			public int compare(Map.Entry<BusinessEntity, Double> a, Map.Entry<BusinessEntity, Double> b) {
				return -a.getValue().compareTo(b.getValue());
			}
		});
		Map<BusinessEntity, Double> sortedScores = new LinkedHashMap<BusinessEntity, Double>();
		for (Map.Entry<BusinessEntity, Double> entry : entries) {
			sortedScores.put(entry.getKey(), entry.getValue());
		}
		endTime = System.currentTimeMillis();
		System.out.println("sort in [" + (endTime - startTime) + " ms]");

		double maxScore = 0;
		for (Comparer c : cs.getComparers()) {
			maxScore += c.getWeight();
		}

		// remove the actual supplier (for measuring effectiveness)
		// TODO the following 2 lines above max score computation?
		BusinessEntity awardedSupplier = getAwardedSupplier(base);
		scores.remove(awardedSupplier);

		List<SuitableSuppliersTableRow> table = new ArrayList<>();

		// process additional metrics for suppliers
		for (BusinessEntity supplier : sortedScores.keySet()) {
			String triples =
					"http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
							+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+.+}"
							+ "+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
							+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C" + supplier.url
							+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain";

			double percent = (int) (1000 * sortedScores.get(supplier) / maxScore) / 10f;

			Set<String> cpvSet = new HashSet<>();
			cpvSet.add(base.rdf
					.getProperty(ResourceFactory.createResource(base.getUrl()), ResourceFactory.createProperty(PC, "mainObject"))
					.getResource().toString());
			NodeIterator it =
					base.rdf.listObjectsOfProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(PC, "additionalObject"));
			while (it.hasNext()) {
				for (RDFNode additionalCpvNode : it.toSet()) {
					cpvSet.add(additionalCpvNode.asResource().toString());
				}
			}
			List<String> cpvs = new ArrayList<String>(cpvSet);

			// TODO more effective sparql for all BEs at once
			long contracts = -1; // getSuppliersContracts(supplier);
			long contractsSameCPV = -1; // getSuppliersContractsSameCPV(supplier, cpvs);

			long volumeOfContracts = -1; // getSuppliersVolumeOfContracts(supplier);
			long volumeOfContractsSameCPV = -1; // getSuppliersVolumeOfContractsSameCPV(supplier, cpvs);

			long contractingAuthorities = -1; // getContractingAuthorities(supplier);
			long contractingAuthoritiesSameCPV = -1; // getContractingAuthoritiesSameCPV(supplier, cpvs);

			table.add(new SuitableSuppliersTableRow(supplier.url,
					supplier.name,
					supplier.place,
					percent,
					triples,
					contracts,
					contractsSameCPV,
					volumeOfContracts,
					volumeOfContractsSameCPV,
					contractingAuthorities,
					contractingAuthoritiesSameCPV));
		}

		long endTotalTime = System.currentTimeMillis();
		System.out.println("total runtime [" + (endTotalTime - startTotalTime) + " ms]");

		return table;

	}

	protected Map<String, Long> getSuppliersContracts(List<BusinessEntity> suppliers) {

		String supplierString = new String();
		for (BusinessEntity supplier : suppliers) {
			supplierString += "<" + supplier.url + "> ";
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT ?supplier (COUNT(DISTINCT ?contract) AS ?total) " + 
				"WHERE { " +
				"	?awardedTender pc:supplier ?supplier . " +
				"	?contract pc:awardedTender  ?awardedTender ;" +
				"			  a pc:Contract . " + 
				"	VALUES ?supplier { " + supplierString + " } " +
				"}" +
				"GROUP BY ?supplier");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Long> results = new HashMap<>();

		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			try {
				results.put(row.get("supplier").asResource().getURI(), row.get("total").asLiteral().getLong());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return results;
	}

	protected Map<String, Long> getSuppliersContractsSameCPV(List<BusinessEntity> suppliers, List<String> cpvs) {

		String supplierString = new String();
		for (BusinessEntity supplier : suppliers) {
			supplierString += "<" + supplier.url + "> ";
		}

		String cpvString = new String();
		for (String cpv : cpvs) {
			cpvString += "<" + cpv + "> ";
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT ?supplier (COUNT(DISTINCT ?contract) AS ?total) " + 
				"WHERE { " + 
				"	?awardedTender pc:supplier ?supplier . " +
				"	?contract pc:awardedTender  ?awardedTender ;" +
				"			  a pc:Contract . " + 
				"	OPTIONAL { " + 
				"		?contract pc:mainObject ?cpv . " + 
				"	} " + 
				"	OPTIONAL { " + 
				"		?contract pc:additionalObject ?cpv . " + 
				"	} " + 
				"	VALUES ?supplier { " + supplierString + " } " +
				"	VALUES ?cpv { " + cpvString + " } " + 
				"}" +
				"GROUP BY ?supplier");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Long> results = new HashMap<>();

		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			try {
				results.put(row.get("supplier").asResource().getURI(), row.get("total").asLiteral().getLong());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return results;
	}

	protected Map<String, Long> getSuppliersVolumeOfContracts(List<BusinessEntity> suppliers) {

		String supplierString = new String();
		for (BusinessEntity supplier : suppliers) {
			supplierString += "<" + supplier.url + "> ";
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT ?supplier (SUM(?priceValue) AS ?total) " + 
				"WHERE { " + 
				"	?awardedTender pc:supplier ?supplier . " +
				"	?contract pc:estimatedPrice ?price ; " + 
				"			  pc:awardedTender ?awardedTender ;" +
				"			  a pc:Contract . " +  
				"	?price gr:hasCurrencyValue ?priceValue . " + 
				"	VALUES ?supplier { " + supplierString + " } " +
				"} " + 
				"GROUP BY ?supplier");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Long> results = new HashMap<>();

		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			try {
				results.put(row.get("supplier").asResource().getURI(), row.get("total").asLiteral().getLong());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return results;
	}

	protected Map<String, Long> getSuppliersVolumeOfContractsSameCPV(List<BusinessEntity> suppliers, List<String> cpvs) {

		String supplierString = new String();
		for (BusinessEntity supplier : suppliers) {
			supplierString += "<" + supplier.url + "> ";
		}

		String cpvString = new String();
		for (String cpv : cpvs) {
			cpvString += "<" + cpv + "> ";
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT ?supplier (SUM(?priceValue) AS ?total) " + 
				"WHERE { " + 
				"	?awardedTender pc:supplier ?supplier . " +
				"	?contract pc:estimatedPrice ?price ; " + 
				"	          pc:awardedTender ?awardedTender;" +
				"			  a pc:Contract . " +  
				"	?price gr:hasCurrencyValue ?priceValue . " + 
				"	OPTIONAL { " + 
				"		?contract pc:mainObject ?cpv . " + 
				"	} " + 
				"	OPTIONAL { " + 
				"		?contract pc:additionalObject ?cpv . " + 
				"	} " + 
				"	VALUES ?supplier { " + supplierString + " } " +
				"	VALUES ?cpv { " + cpvString + " } " +
				"} " + 
				"GROUP BY ?supplier");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Long> results = new HashMap<>();

		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			try {
				results.put(row.get("supplier").asResource().getURI(), row.get("total").asLiteral().getLong());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return results;
	}

	protected Map<String, Long> getContractingAuthorities(List<BusinessEntity> suppliers) {

		String supplierString = new String();
		for (BusinessEntity supplier : suppliers) {
			supplierString += "<" + supplier.url + "> ";
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT ?supplier (COUNT(DISTINCT ?contractingAuthority) AS ?total) " + 
				"WHERE { " + 
				"	?awardedTender pc:supplier ?supplier . " +
				"	?contract pc:awardedTender  ?awardedTender ; " +
				"			  pc:contractingAuthority ?contractingAuthority ;" +
				"			  a pc:Contract. " +
				"	VALUES ?supplier { " + supplierString + " } " +
				"}" +
				"GROUP BY ?supplier");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Long> results = new HashMap<>();

		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			try {
				results.put(row.get("supplier").asResource().getURI(), row.get("total").asLiteral().getLong());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return results;
	}

	protected Map<String, Long> getContractingAuthoritiesSameCPV(List<BusinessEntity> suppliers, List<String> cpvs) {

		String supplierString = new String();
		for (BusinessEntity supplier : suppliers) {
			supplierString += "<" + supplier.url + "> ";
		}

		String cpvString = new String();
		for (String cpv : cpvs) {
			cpvString += "<" + cpv + "> ";
		}

		/* @formatter:off */
		Query query = QueryFactory.create( 
				config.getPreference("prefixes") +
				"SELECT ?supplier (COUNT(DISTINCT ?contractingAuthority) AS ?total) " + 
				"WHERE { " + 
				"	?awardedTender pc:supplier ?supplier . " + 
				"	?contract pc:awardedTender  ?awardedTender ; " +
				"			  pc:contractingAuthority ?contractingAuthority ;" +
				"			  a pc:Contract . " +
				"	OPTIONAL { " + 
				"		?contract pc:mainObject ?cpv . " + 
				"	} " + 
				"	OPTIONAL { " + 
				"		?contract pc:additionalObject ?cpv . " + 
				"	} " +
				"	VALUES ?supplier { " + supplierString + " } " +
				"	VALUES ?cpv { " + cpvString + " } " +
				"}" +
				"GROUP BY ?supplier");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Long> results = new HashMap<>();

		ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
		while (rs.hasNext()) {
			QuerySolution row = rs.next();
			try {
				results.put(row.get("supplier").asResource().getURI(), row.get("total").asLiteral().getLong());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return results;
	}

	// TODO find a suitable design pattern, perhaps some kind of Factory ?
	protected List<SimilarContractTableRow> refineSimilarContracts(List<SimilarContractTableRow> similarContracts,
			HttpServletRequest request) {

		List<SimilarContractTableRow> filtered = new LinkedList<>(similarContracts); // TODO benchmark - ArrayList could be faster
		Iterator<SimilarContractTableRow> it = null;

		// age
		if (allDefined(request.getParameter("dateFrom"))) {
			it = filtered.iterator();
			while (it.hasNext()) {
				SimilarContractTableRow contract = it.next();
				try {
					if (DatatypeConverter.parseDate(contract.publicationDate).before(DatatypeConverter.parseDate(request
							.getParameter("dateFrom")))) {
						it.remove();
					}
				} catch (Exception e) {
					System.err.println("[WARN] refine : " + e.getMessage());
					it.remove();
				}
			}
		}
		if (allDefined(request.getParameter("dateTo"))) {
			it = filtered.iterator();
			while (it.hasNext()) {
				SimilarContractTableRow contract = it.next();
				try {
					if (DatatypeConverter.parseDate(contract.publicationDate).after(DatatypeConverter.parseDate(request
							.getParameter("dateTo")))) {
						it.remove();
					}
				} catch (Exception e) {
					System.err.println("[WARN] refine : " + e.getMessage());
					it.remove();
				}
			}
		}

		// score
		if (allDefined(request.getParameter("minScore"))) {
			it = filtered.iterator();
			while (it.hasNext()) {
				SimilarContractTableRow contract = it.next();
				try {
					if (contract.percent < Double.parseDouble(request.getParameter("minScore"))) {
						it.remove();
					}
				} catch (Exception e) {
					System.err.println("[WARN] refine : " + e.getMessage());
					it.remove();
				}
			}
		}
		if (allDefined(request.getParameter("maxScore"))) {
			it = filtered.iterator();
			while (it.hasNext()) {
				SimilarContractTableRow contract = it.next();
				try {
					if (contract.percent > Double.parseDouble(request.getParameter("maxScore"))) {
						it.remove();
					}
				} catch (Exception e) {
					System.err.println("[WARN] refine : " + e.getMessage());
					it.remove();
				}
			}
		}

		// distance
		if (allDefined(request.getParameter("maxDistance"))) {
			it = filtered.iterator();
			while (it.hasNext()) {
				SimilarContractTableRow contract = it.next();
				try {
					if (contract.distance > Double.parseDouble(request.getParameter("maxDistance"))) {
						it.remove();
					}
				} catch (Exception e) {
					System.err.println("[WARN] refine : " + e.getMessage());
					it.remove();
				}
			}
		}

		// countries
		if (allDefined(request.getParameter("countries"))) {

			Set<String> countries = new HashSet<>(Arrays.asList(request.getParameter("countries").split(",")));
			if (!(countries.isEmpty() || countries.contains("-- any --"))) {
				it = filtered.iterator();
				while (it.hasNext()) {
					SimilarContractTableRow contract = it.next();
					try {
						if (contract.country == null || !countries.contains(contract.country)) {
							it.remove();
						}
					} catch (Exception e) {
						System.err.println("[WARN] refine : " + e.getMessage());
						it.remove();
					}
				}
			}
		}

		return filtered;
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

		// int items = 2_000_000;
		// int warmup = 1;
		// int measurement = 3;
		//
		// List<Contract> contracts = new ArrayList<>(items);
		// for (int i = 0; i < items; i++) {
		// contracts.add(new Contract(UUID.randomUUID().toString(), null));
		// }
		//
		// System.out.println("random contract generation complete, " + items + " items");
		//
		// for (int i = 0; i < warmup; i++) {
		// benchmarkContractStore("ArrayContractStore", new ArrayList<>(contracts), true);
		// }
		// for (int i = 0; i < measurement; i++) {
		// benchmarkContractStore("ArrayContractStore", new ArrayList<>(contracts), false);
		// }
		//
		// for (int i = 0; i < warmup; i++) {
		// benchmarkContractStore("LinkedContractStore", new ArrayList<>(contracts), true);
		// }
		// for (int i = 0; i < measurement; i++) {
		// benchmarkContractStore("LinkedContractStore", new ArrayList<>(contracts), false);
		// }
		//
		// for (int i = 0; i < warmup; i++) {
		// benchmarkContractStore("LinkedHashContractStore", new ArrayList<>(contracts), true);
		// }
		// for (int i = 0; i < measurement; i++) {
		// benchmarkContractStore("LinkedHashContractStore", new ArrayList<>(contracts), false);
		// }

		if (isUserLoggedIn(request)) {
			String action = request.getParameter("action");
			if (action == null) {
				response.sendError(400);
				return;
			}

			// TODO move where used
			HttpSession session = request.getSession(false);
			Gson gson = new Gson();

			List<SimilarContractTableRow> similarContracts = null;
			List<SimilarContractTableRow> cpage = null;

			List<SuitableSuppliersTableRow> suitableSuppliers = null;
			List<SuitableSuppliersTableRow> spage = null;

			switch (action) {

				case "getSimilarContracts":

					if (!allDefined(request.getParameter("from"), request.getParameter("to"), request.getParameter("contractURL") // ,
					// request.getParameter("limit"),
					// request.getParameter("publicationDateRangeDays")
					)) {
						response.sendError(400);
						return;
					}

					// if (!allDefined(request.getParameter("from"),
					// request.getParameter("to"),
					// request.getParameter("contractURL"),
					// request.getParameter("cpvString"))) {
					// response.sendError(400);
					// return;
					// }
					synchronized (session) {
						similarContracts =
								(List<SimilarContractTableRow>) session.getAttribute("similarContracts"
										+ DigestUtils.sha256Hex(request.getParameter("contractURL")));
						if (similarContracts == null) {
							similarContracts = getSimilarContracts(request);
							session.setAttribute("similarContracts" + DigestUtils.sha256Hex(request.getParameter("contractURL")),
									similarContracts);
						}

						// TODO split into function?
						// refine results
						if (allDefined(request.getParameter("refine"))) {
							List<SimilarContractTableRow> similarContractsRefined = null;
							similarContractsRefined =
									(List<SimilarContractTableRow>) session.getAttribute("similarContractsRefined"
											+ DigestUtils.sha256Hex(request.getQueryString()));
							if (similarContractsRefined == null) {
								similarContractsRefined = refineSimilarContracts(similarContracts, request);
								session.setAttribute("similarContractsRefined" + DigestUtils.sha256Hex(request.getQueryString()),
										similarContractsRefined);
							}
							similarContracts = similarContractsRefined;
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
					if (!allDefined(request.getParameter("contractURL"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						similarContracts =
								(List<SimilarContractTableRow>) session.getAttribute("similarContracts"
										+ DigestUtils.sha256Hex(request.getParameter("contractURL")));
						if (similarContracts == null) {
							similarContracts = getSimilarContracts(request);
							session.setAttribute("similarContracts" + DigestUtils.sha256Hex(request.getParameter("contractURL")),
									similarContracts);
						}

						// refine results
						if (allDefined(request.getParameter("refine"))) {
							List<SimilarContractTableRow> similarContractsRefined = null;
							similarContractsRefined =
									(List<SimilarContractTableRow>) session.getAttribute("similarContractsRefined"
											+ DigestUtils.sha256Hex(request.getQueryString()));
							if (similarContractsRefined == null) {
								similarContractsRefined = refineSimilarContracts(similarContracts, request);
								session.setAttribute("similarContractsRefined" + DigestUtils.sha256Hex(request.getQueryString()),
										similarContractsRefined);
							}
							similarContracts = similarContractsRefined;
						}
					}

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println((int) Math.ceil(similarContracts.size() / 10d)); // TODO length from uc
					break;

				case "getSuitableSuppliers":
					if (!allDefined(request.getParameter("from"), request.getParameter("to"), request.getParameter("contractURL"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						suitableSuppliers =
								(List<SuitableSuppliersTableRow>) session.getAttribute("suitableSuppliers"
										+ DigestUtils.sha256Hex(request.getParameter("contractURL"))
										+ request.getParameter("additionalMetrics")); // TODO
						if (suitableSuppliers == null) {
							suitableSuppliers = getSuitableSuppliers(request);
							session.setAttribute("suitableSuppliers" + DigestUtils.sha256Hex(request.getParameter("contractURL"))
									+ request.getParameter("additionalMetrics"), suitableSuppliers);
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
					if (!allDefined(request.getParameter("contractURL"))) {
						response.sendError(400);
						return;
					}
					synchronized (session) {
						suitableSuppliers =
								(List<SuitableSuppliersTableRow>) session.getAttribute("suitableSuppliers"
										+ DigestUtils.sha256Hex(request.getParameter("contractURL"))
										+ request.getParameter("additionalMetrics"));
						if (suitableSuppliers == null) {
							suitableSuppliers = getSuitableSuppliers(request);
							session.setAttribute("suitableSuppliers" + DigestUtils.sha256Hex(request.getParameter("contractURL"))
									+ request.getParameter("additionalMetrics"), suitableSuppliers);
						}
					}

					response.setContentType("application/json; charset=UTF-8");
					response.getWriter().println((int) Math.ceil(suitableSuppliers.size() / 10d)); // TODO length from uc
					break;

				case "debug":
					// ////////////////////////////////////////////////////////////////////////////////////////////////

					if (!allDefined(request.getParameter("contractURL"),
							request.getParameter("limit"),
							request.getParameter("publicationDateRangeDays"))) {
						response.sendError(400);
						return;
					}

					// synchronized (session) {

					ServletRequestConfiguration requestConfig = new ServletRequestConfiguration(request);

					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().println("<html><body><pre>");

					long startTotalTime = System.currentTimeMillis();

					long startTime;
					long endTime;

					// parse request
					// UserContext uc = getUserContext(request);
					String baseUrl = request.getParameter("contractURL");

					// System.out.println("###################################################");

					// get base contract
					startTime = System.currentTimeMillis();

					// Model baseRdf = getPrivateContract(baseUrl, uc.getNamedGraph());
					Model baseRdf = getPublicContract(baseUrl); // TODO private

					Contract base = new Contract(baseUrl, baseRdf);
					endTime = System.currentTimeMillis();
					response.getWriter().println("base contract retrieved in [" + (endTime - startTime) + " ms]");
					response.getWriter().println();

					// get list of promising urls to compare with
					startTime = System.currentTimeMillis();
					List<String> urls =
							getPossiblySimilarContractUrls(base,
									Long.parseLong(request.getParameter("limit")),
									Integer.parseInt(request.getParameter("publicationDateRangeDays")));
					endTime = System.currentTimeMillis();
					response.getWriter().println(urls.size() + " similar contracts found in [" + (endTime - startTime) + " ms]");
					response.getWriter().println();

					// fetch all level 0 data about promising contracts
					startTime = System.currentTimeMillis();

					// basic contract store
					ContractStore cs = new LinkedContractStore();

					for (String url : urls) {
						Model rdf = getPublicContract(url);
						if (rdf != null) {
							cs.add(new Contract(url, rdf));
						}
						// response.getWriter().println(url);
					}
					endTime = System.currentTimeMillis();
					response.getWriter().println("level 0 linked data retrieved in [" + (endTime - startTime) + " ms]");
					// response.getWriter().println("# of promising contracts: " + cs.size());
					response.getWriter().println();

					startTime = System.currentTimeMillis();
					Comparer tdc = new TenderDeadlineComparer(requestConfig);
					tdc.runVerbose(base, cs);
					endTime = System.currentTimeMillis();
					response.getWriter().println("TenderDeadlineComparer in [" + (endTime - startTime) + " ms]");
					if (allDefined(request.getParameter("TenderDeadlineComparer.min"),
							request.getParameter("TenderDeadlineComparer.max"))) {
						cs.retain(tdc,
								Double.parseDouble(request.getParameter("TenderDeadlineComparer.min")),
								Double.parseDouble(request.getParameter("TenderDeadlineComparer.max")));
						response.getWriter().println("# of filtered contracts: " + cs.size());
					}
					response.getWriter().println();

					startTime = System.currentTimeMillis();
					Comparer pdc = new PublicationDateComparer(requestConfig);
					pdc.runVerbose(base, cs);
					endTime = System.currentTimeMillis();
					response.getWriter().println("PublicationDateComparer in [" + (endTime - startTime) + " ms]");
					if (allDefined(request.getParameter("PublicationDateComparer.min"),
							request.getParameter("PublicationDateComparer.max"))) {
						cs.retain(pdc,
								Double.parseDouble(request.getParameter("PublicationDateComparer.min")),
								Double.parseDouble(request.getParameter("PublicationDateComparer.max")));
						response.getWriter().println("# of filtered contracts: " + cs.size());
					}
					response.getWriter().println();

					startTime = System.currentTimeMillis();
					Comparer cpvc = new CPVComparer(requestConfig);
					cpvc.runVerbose(base, cs);
					endTime = System.currentTimeMillis();
					response.getWriter().println("CPVComparer in [" + (endTime - startTime) + " ms]");
					if (allDefined(request.getParameter("CPVComparer.min"), request.getParameter("CPVComparer.max"))) {
						cs.retain(cpvc,
								Double.parseDouble(request.getParameter("CPVComparer.min")),
								Double.parseDouble(request.getParameter("CPVComparer.max")));
						response.getWriter().println("# of filtered contracts: " + cs.size());
					}
					response.getWriter().println();

					startTime = System.currentTimeMillis();
					Comparer gdc = new GeoDistanceComparer(requestConfig);
					gdc.runVerbose(base, cs);
					endTime = System.currentTimeMillis();
					response.getWriter().println("GeoDistanceComparer in [" + (endTime - startTime) + " ms]");
					if (allDefined(request.getParameter("GeoDistanceComparer.min"),
							request.getParameter("GeoDistanceComparer.max"))) {
						cs.retain(gdc,
								Double.parseDouble(request.getParameter("GeoDistanceComparer.min")),
								Double.parseDouble(request.getParameter("GeoDistanceComparer.max")));
						response.getWriter().println("# of filtered contracts: " + cs.size());
					}
					response.getWriter().println();

					startTime = System.currentTimeMillis();
					Comparer txtc = new TextComparer(requestConfig);
					txtc.runVerbose(base, cs);
					endTime = System.currentTimeMillis();
					response.getWriter().println("TextComparer in [" + (endTime - startTime) + " ms]");
					if (allDefined(request.getParameter("TextComparer.min"), request.getParameter("TextComparer.max"))) {
						cs.retain(txtc,
								Double.parseDouble(request.getParameter("TextComparer.min")),
								Double.parseDouble(request.getParameter("TextComparer.max")));
						response.getWriter().println("# of filtered contracts: " + cs.size());
					}
					response.getWriter().println();

					if (allDefined(request.getParameter("submit-contracts"))) {

						// sort according to total score
						startTime = System.currentTimeMillis();
						cs.sortDescending();
						endTime = System.currentTimeMillis();
						response.getWriter().println("sort in [" + (endTime - startTime) + " ms]");

						double maxScore = 0;
						for (Comparer c : cs.getComparers()) {
							maxScore += c.getWeight();
						}

						// return results
						response.getWriter().println();
						for (Contract c : cs) {
							if (c.equals(base)) {
								response.getWriter().println("</pre><div style='background:lightgreen; "
										+ "padding-top:4px; padding-bottom:4px;'><pre>");
							}
							response.getWriter().print("</pre>[" + (int) (1000 * c.getTotalScore() / maxScore) / 10f + " %] ");
							response.getWriter()
									.println("<a href='http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
											+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+.+}"
											+ "+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
											+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C" + c.getUrl()
											+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain'>"
											+ c.getUrl() + "</a><pre>");

							BusinessEntity be = getAwardedSupplier(c);
							if (be != null) {
								response.getWriter().println("</pre>Awarded supplier: " + be.name);
								response.getWriter()
										.println("<a href='http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
												+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+"
												+ ".+}+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
												+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C"
												+ be.url
												+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain'>"
												+ be.url + "</a><pre>");
							}
							for (Comparer comparer : c.getScores().keySet()) {
								response.getWriter().println(comparer + " (weight " + comparer.getWeight() + ") : "
										+ ((int) (c.getScore(comparer) * 1000)) / 1000f);
								response.getWriter().println("\tExplanation: " + c.getMessage(comparer));
							}
							if (c.equals(base)) {
								response.getWriter().println("</pre></div><pre>");
							}
							response.getWriter().println();
						}

					} else if (allDefined(request.getParameter("submit-suppliers"))) {
						Map<BusinessEntity, Double> scores = new HashMap<>();

						startTime = System.currentTimeMillis();
						for (Contract c : cs) {
							BusinessEntity be = getAwardedSupplier(c);
							if (be != null) {
								Double oldScore = scores.get(be);
								Double newScore = 0d;
								if (oldScore != null) {
									newScore = Math.max(1, oldScore + c.getTotalScore());
								} else {
									newScore = c.getTotalScore();
								}
								scores.put(be, newScore);
							}
						}
						endTime = System.currentTimeMillis();
						response.getWriter().println("Awarded suppliers in [" + (endTime - startTime) + " ms]");
						response.getWriter().println("# of unique suppliers: " + scores.size());
						response.getWriter().println();

						startTime = System.currentTimeMillis();
						List<Map.Entry<BusinessEntity, Double>> entries = new ArrayList<>(scores.entrySet());
						Collections.sort(entries, new Comparator<Map.Entry<BusinessEntity, Double>>() {

							public int compare(Map.Entry<BusinessEntity, Double> a, Map.Entry<BusinessEntity, Double> b) {
								return -a.getValue().compareTo(b.getValue());
							}
						});
						Map<BusinessEntity, Double> sortedScores = new LinkedHashMap<BusinessEntity, Double>();
						for (Map.Entry<BusinessEntity, Double> entry : entries) {
							sortedScores.put(entry.getKey(), entry.getValue());
						}
						endTime = System.currentTimeMillis();
						response.getWriter().println("sort in [" + (endTime - startTime) + " ms]");
						response.getWriter().println();

						double maxScore = 0;
						for (Comparer c : cs.getComparers()) {
							maxScore += c.getWeight();
						}

						BusinessEntity awardedSupplier = getAwardedSupplier(base);

						for (BusinessEntity be : sortedScores.keySet()) {
							if (awardedSupplier != null && be.name.equals(awardedSupplier.name)) {
								response.getWriter().println("</pre><div style='background:lightgreen; "
										+ "padding-top:4px; padding-bottom:4px;'><pre>");
							}
							response.getWriter().print("</pre>[" + (int) (1000 * sortedScores.get(be) / maxScore) / 10f + " %] ");
							response.getWriter()
									.println("<a href='http://xrg15.projekty.ms.mff.cuni.cz:3030/public/sparql?query="
											+ "CONSTRUCT+{+%3FcontractURI+%3Fp1+%3Fo1+.+%3Fo1+%3Fp2+%3Fo2+.++%3Fo2+%3Fp3+%3Fo3+.+}"
											+ "+WHERE+{+%3FcontractURI+%3Fp1+%3Fo1+.+OPTIONAL++{+%3Fo1+%3Fp2+%3Fo2+OPTIONAL+"
											+ "{+%3Fo2+%3Fp3+%3Fo3+}+}+}+VALUES+%3FcontractURI+{+%3C" + be.url
											+ "%3E+}%0D%0A&output=text&stylesheet=%2Fxml-to-html.xsl&force-accept=text%2Fplain'>"
											+ be.url + "</a><pre>");

							response.getWriter().println(be.name);
							if (awardedSupplier != null && be.name.equals(awardedSupplier.name)) {
								response.getWriter().println("</pre></div><pre>");
							}
							response.getWriter().println();
						}
					}

					long endTotalTime = System.currentTimeMillis();
					System.out.println("total runtime [" + (endTotalTime - startTotalTime) + " ms]");
					response.getWriter().println("total runtime [" + (endTotalTime - startTotalTime) + " ms]");

					// }

					// ////////////////////////////////////////////////////////////////////////////////////////////////
					// response.sendError(400);
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

		// TODO remove
		Geocoder.saveCache("cache/geocoder.cache");

	}
}
