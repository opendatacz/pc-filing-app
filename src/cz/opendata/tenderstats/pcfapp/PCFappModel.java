package cz.opendata.tenderstats.pcfapp;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.jena.riot.RiotException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.OWL;

import cz.opendata.tenderstats.ComponentConfiguration;
import cz.opendata.tenderstats.UserContext;
import cz.opendata.tenderstats.admin.ClassHierarchy;

public class PCFappModel implements Serializable {

	private static final long serialVersionUID = -3963894760247662458L;

	private boolean debug = true;

	public static final String pc = "http://purl.org/procurement/public-contracts#";
	public static final String pccrit = "http://purl.org/procurement/public-contracts-criteria#";
	public static final String pcf = "http://purl.org/procurement/pcfilingapp#";
	public static final String pce = "http://purl.org/procurement/public-contracts-eu#";
	public static final String gr = "http://purl.org/goodrelations/v1#";
	public static final String dc = "http://purl.org/dc/terms/";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String vc = "http://www.w3.org/2006/vcard/ns#";
	public static final String rdfsns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	// NEW
	public static final String owl = "http://www.w3.org/2002/07/owl#";
	public static final String fs = "http://linked.opendata.cz/ontology/form-specification#";

	public static final Property dc_title = ResourceFactory.createProperty(dc,
			"title");
	public static final Property dc_description = ResourceFactory
			.createProperty(dc, "description");
	public static final Property pc_mainCPV = ResourceFactory.createProperty(
			pc, "mainObject");
	public static final Property pc_additionalCPV = ResourceFactory
			.createProperty(pc, "additionalObject");
	public static final Property pc_procedureType = ResourceFactory
			.createProperty(pc, "procedureType");

	public static final Property pc_deadline = ResourceFactory.createProperty(
			pc, "tenderDeadline");
	public static final Property pc_estimatedPrice = ResourceFactory
			.createProperty(pc, "estimatedPrice");
	public static final Property pc_actualPrice = ResourceFactory
			.createProperty(pc, "actualPrice");
	public static final Property pc_actualEndDate = ResourceFactory
			.createProperty(pc, "actualEndDate");
	public static final Property pc_offeredPrice = ResourceFactory
			.createProperty(pc, "offeredPrice");
	public static final Property pc_estimatedEndDate = ResourceFactory
			.createProperty(pc, "estimatedEndDate");
	public static final Property pc_startDate = ResourceFactory.createProperty(
			pc, "startDate");
	public static final Property pc_endDate = ResourceFactory.createProperty(
			pc, "endDate");
	public static final Property pc_location = ResourceFactory.createProperty(
			pc, "location");
	public static final Property pc_sealed = ResourceFactory.createProperty(pc,
			"tendersSealed");
	public static final Property pc_tenderOpening = ResourceFactory
			.createProperty(pc, "tenderOpeningDateTime");
	public static final Property pc_contact = ResourceFactory.createProperty(
			pc, "contact");
	public static final Property pc_contract = ResourceFactory.createProperty(
			pc, "contract");
	public static final Property pc_contractingAuthority = ResourceFactory
			.createProperty(pc, "contractingAuthority");
	public static final Property pc_awardCriteriaCombination = ResourceFactory
			.createProperty(pc, "awardCriteriaCombination");
	public static final Property pc_awardCriterion = ResourceFactory
			.createProperty(pc, "awardCriterion");
	public static final Property pc_weightedCriterion = ResourceFactory
			.createProperty(pc, "weightedCriterion");
	public static final Property pc_criterionWeight = ResourceFactory
			.createProperty(pc, "criterionWeight");
	public static final Property pc_supplier = ResourceFactory.createProperty(
			pc, "supplier");
	public static final Property pc_tender = ResourceFactory.createProperty(pc,
			"tender");
	public static final Property pc_awardedTender = ResourceFactory
			.createProperty(pc, "awardedTender");

	public static final Property gr_hasCurrencyValue = ResourceFactory
			.createProperty(gr, "hasCurrencyValue");
	public static final Property gr_hasCurrency = ResourceFactory
			.createProperty(gr, "hasCurrency");

	public static final Property pcf_confidentialPrice = ResourceFactory
			.createProperty(pcf, "confidentialPrice");
	public static final Property pcf_created = ResourceFactory.createProperty(
			pcf, "created");
	public static final Property pcf_modified = ResourceFactory.createProperty(
			pcf, "modified");
	public static final Property pcf_projectID = ResourceFactory
			.createProperty(pcf, "projectID");
	public static final Property pcf_eventReference = ResourceFactory
			.createProperty(pcf, "eventReferenceField");
	public static final Property pcf_eventType = ResourceFactory
			.createProperty(pcf, "eventType");
	public static final Property pcf_fileGenTerms = ResourceFactory
			.createProperty(pcf, "documentGenTerms");
	public static final Property pcf_fileCallDoc = ResourceFactory
			.createProperty(pcf, "documentCallDoc");
	public static final Property pcf_document = ResourceFactory.createProperty(
			pcf, "document");
	public static final Property pcf_documentToken = ResourceFactory
			.createProperty(pcf, "token");
	public static final Property pcf_documentType = ResourceFactory
			.createProperty(pcf, "docType");
	public static final Property pcf_documentFileName = ResourceFactory
			.createProperty(pcf, "fileName");
	public static final Property pcf_documentGlobal = ResourceFactory
			.createProperty(pcf, "docGlobal");
	public static final Property pcf_submitted = ResourceFactory
			.createProperty(pcf, "submitted");

	public static final Property pce_hasParentRegion = ResourceFactory
			.createProperty(pce, "hasParentRegion");

	public static final Property vc_fn = ResourceFactory.createProperty(vc,
			"fn");
	public static final Property vc_note = ResourceFactory.createProperty(vc,
			"note");
	public static final Property vc_email = ResourceFactory.createProperty(vc,
			"email");
	public static final Property vc_tel = ResourceFactory.createProperty(vc,
			"tel");

	public static final Property rdfs_label = ResourceFactory.createProperty(
			rdfs, "label");

	public static final Property rdfsns_type = ResourceFactory.createProperty(
			rdfsns, "type");
	public static final Property rdfsns_value = ResourceFactory.createProperty(
			rdfsns, "value");

	protected ComponentConfiguration config;
	protected PCFappUtils utils;

	public PCFappModel(ComponentConfiguration config) {
		this.config = config;
		this.utils = new PCFappUtils(this.config);
	}

	/**
	 * Returns private tender as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	public Model getDocument(String documentToken, String namedGraph) {
		String documentURI = config.getPreference("newContractURL")
				+ "document/" + documentToken;
		/* @formatter:off */
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?documentURI ?p1 ?o1 . "
				+ "    ?o1 ?p2 ?o2 . " + "    ?o2 ?p3 ?o3 . "
				+ "    ?o3 ?p4 ?o4 . " + "    ?o4 ?p5 ?o5 .} " + "FROM <"
				+ namedGraph + "> " + "WHERE " + "  { ?documentURI ?p1 ?o1 . "
				+ "    ?documentURI a pcfapp:Document " + "    OPTIONAL "
				+ "      { ?o1 ?p2 ?o2 " + "        OPTIONAL "
				+ "          { ?o2 ?p3 ?o3 " + "            OPTIONAL "
				+ "              { ?o3 ?p4 ?o4 " + "                OPTIONAL "
				+ "                  { ?o4 ?p5 ?o5 } " + "              } "
				+ "          } " + "      } " + "  } "
				+ "VALUES ?documentURI { <" + documentURI + "> }");
		/* @formatter:on */
		Model document = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();

		System.out.println(query.toString());
		// System.out.println("###################################################");
		document.write(System.out, "Turtle");

		return document;
	};

	/**
	 * Returns private contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	public Model getSupplier(String supplierURI) {
		/* @formatter:off */
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?contractURI ?p1 ?o1 . "
				+ "    ?o1 ?p2 ?o2 . " + "    ?o2 ?p3 ?o3 . "
				+ "    ?o3 ?p4 ?o4 . " + "    ?o4 ?p5 ?o5 .} " + "WHERE "
				+ "{ GRAPH ?g " + "  { ?contractURI ?p1 ?o1 . "
				+ "    ?contractURI a gr:BusinessEntity " + "    OPTIONAL "
				+ "      { ?o1 ?p2 ?o2 " + "        OPTIONAL "
				+ "          { ?o2 ?p3 ?o3 " + "            OPTIONAL "
				+ "              { ?o3 ?p4 ?o4 " + "                OPTIONAL "
				+ "                  { ?o4 ?p5 ?o5 } " + "              } "
				+ "          } " + "      } " + "  } " + "}"
				+ "VALUES ?contractURI { <" + supplierURI + "> }");
		/* @formatter:on */
		Model entity = QueryExecutionFactory.sparqlService(
				config.getSparqlPublicQuery(), query).execConstruct();

		// System.out.println("###################################################");
		System.out.println(query.toString());
		entity.write(System.out, "Turtle");

		return entity;
	};

	/**
	 * Returns private tender as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	protected Model getTendersFromContract(String tenderURI, String namedGraph,
			boolean awarded) {
		/* @formatter:off */
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?tenderURI ?p1 ?o1 . "
				+ "    ?o1 ?p2 ?o2 . " + "    ?o2 ?p3 ?o3 . "
				+ "    ?o3 ?p4 ?o4 . " + "    ?o4 ?p5 ?o5 .} " + "FROM <"
				+ namedGraph + "> " + "WHERE " + "  { ?tenderURI ?p1 ?o1 . "
				+ "    ?tenderURI a pc:tender " + "    OPTIONAL "
				+ "      { ?o1 ?p2 ?o2 " + "        OPTIONAL "
				+ "          { ?o2 ?p3 ?o3 " + "            OPTIONAL "
				+ "              { ?o3 ?p4 ?o4 " + "                OPTIONAL "
				+ "                  { ?o4 ?p5 ?o5 } " + "              } "
				+ "          } " + "      } " + "  } "
				+ "VALUES ?tenderURI { <" + tenderURI + "> }");
		/* @formatter:on */
		Model tender = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();

		// contract.write(System.out, "Turtle");

		return tender;
	};

	/**
	 * Returns private tenders for contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 * @param
	 */
	public Model getPrivateTendersForContract(String namedGraph,
			String contractURI, boolean awarded) {
		/* @formatter:off */
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT " + "  { ?contractURI "
				+ ((awarded) ? "pc:awardedTender" : "pc:tender")
				+ " ?tender . " + "	 ?tender ?p1 ?o1 . " + "	 ?price ?p2 ?o2 "
				+ "} " + "FROM <" + namedGraph + "> " + "WHERE "
				+ "{  ?contractURI "
				+ ((awarded) ? "pc:awardedTender" : "pc:tender")
				+ " ?tender . " + "	?tender ?p1 ?o1 . "
				+ "	?tender pc:offeredPrice ?price . " + "	?price ?p2 ?o2 . "
				+ "	?contractURI rdf:type pc:Contract ." + "} "
				+ "VALUES ?contractURI { <" + contractURI + "> } ");
		/* @formatter:on */
		Model tenders = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();

		// contract.write(System.out, "Turtle");

		return tenders;
	};

	/**
	 * Returns private contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	public Model getPublicContract(String contractURI) {
		/* @formatter:off */
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?contractURI ?p1 ?o1 . "
				+ "    ?o1 ?p2 ?o2 . " + "    ?o2 ?p3 ?o3 . "
				+ "    ?o3 ?p4 ?o4 . " + "    ?o4 ?p5 ?o5 .} " + "FROM <"
				+ config.getPreference("publicGraphName") + "> " + "WHERE "
				+ "  { ?contractURI ?p1 ?o1 . "
				+ "    ?contractURI a pc:Contract " + "    OPTIONAL "
				+ "      { ?o1 ?p2 ?o2 " + "        OPTIONAL "
				+ "          { ?o2 ?p3 ?o3 " + "            OPTIONAL "
				+ "              { ?o3 ?p4 ?o4 " + "                OPTIONAL "
				+ "                  { ?o4 ?p5 ?o5 } " + "              } "
				+ "          } " + "      } " + "  } "
				+ "VALUES ?contractURI { <" + contractURI + "> }");
		/* @formatter:on */
		Model contract = QueryExecutionFactory.sparqlService(
				config.getSparqlPublicQuery(), query).execConstruct();

		contract.write(System.out, "Turtle");

		if (debug) {
			System.out
					.println("###################################################");
			contract.write(System.out, "Turtle");
		}

		return contract;
	};

	private Connection connection = null;

	private Connection connectDB() throws SQLException {

		if (connection != null && connection.isValid(0))
			return connection;

		return DriverManager.getConnection(
				config.getRdbAddress() + config.getRdbDatabase(),
				config.getRdbUsername(), config.getRdbPassword());
	}

	public ResultSet getPublicSupplierData(String entity) {

		/* @formatter:off */
		Query query = QueryFactory
				.create(config.getPreference("prefixes")
						+ "SELECT DISTINCT ?contractURI ?title ?description ?cpv1URL ?cpvAdd ?currency ?price "
						+ "WHERE "
						+ "  { GRAPH <"
						+ config.getPreference("publicGraphName")
						+ "> "
						+ "      {      "
						+ "            ?contractURI rdf:type pc:Contract . "
						+ "            ?contractURI dcterms:title ?title . "
						+ "            ?contractURI pc:notice ?notice . "
						+ "            ?notice pc:publicationDate ?publicationDate ."
						+ "			 ?contractURI pc:awardedTender ?tender ."
						+ "			 ?tender pc:supplier <"
						+ entity
						+ "> ."
						+ "			 ?contractURI pc:actualPrice ?apriceURI . "
						+ "			 ?apriceURI gr:hasCurrency		?currency . "
						+ "			 ?apriceURI gr:hasCurrencyValue	?price . "
						+ "            OPTIONAL "
						+ "              { ?contractURI pcfapp:modified ?modified } "
						+ "            OPTIONAL "
						+ "              { ?contractURI pc:location ?locationURI ."
						+ "				 ?locationURI rdfs:label ?place } "
						+ "            OPTIONAL "
						+ "              { ?contractURI pc:mainObject ?cpv1URL } "
						+ "			 OPTIONAL "
						+ "			 { "
						+ "				SELECT ?contractURI (group_concat( distinct ?cpv) as ?cpvAdd) "
						+ "				WHERE { GRAPH ?buyerURI { ?contractURI pc:additionalObject ?cpv } } "
						+ "				GROUP BY ?contractURI " + "			 } " + "      } "
						+ "  }");
		/* @formatter:n */

		if (debug) {
			System.out
					.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(
				config.getSparqlPublicQuery(), query).execSelect();

	}

	public ResultSet getUserDocuments(String namedGraph, boolean global) {

		/* @formatter:off */
		Query query = QueryFactory
				.create( // TODO rewrite using CONSTRUCT
				config.getPreference("prefixes")
						+ "SELECT DISTINCT ?documentURI ?token ?fileName ?docType "
						+ "WHERE "
						+ "  { GRAPH <"
						+ namedGraph
						+ "> "
						+ "      {  "
						+ "			?documentURI		rdf:type 				pcfapp:Document ;"
						+ "								pcfapp:token			?token ;"
						+ "								pcfapp:fileName			?fileName ;"
						+ (global ? "						pcfapp:docGlobal		\"true\"^^xsd:boolean ; "
								: "")
						+ "      							pcfapp:docType			?docType ." + "	  } "
						+ "	} ");
		/* @formatter:on */

		if (debug) {
			System.out
					.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();
	}

	public void unlinkDocument(UserContext uc, String contractURL, String token) {

		Model document = getDocument(token, uc.getNamedGraph());

		if (!document.contains(null, pcf_documentGlobal)) {
			deleteDocument(uc, token);
		}

		UpdateRequest request;
		/* @formatter:off */
		request = UpdateFactory.create(config.getPreference("prefixes")
				+ "DELETE DATA" + "{" + "	GRAPH <" + uc.getNamedGraph()
				+ "> { " + "	<" + contractURL + ">	pcfapp:document		<"
				+ config.getPreference("newContractURL") + "document/" + token
				+ "> " + "	} " + "}");
		/* @formatter:on */

		if (debug) {
			System.out
					.println("###################################################");
			System.out.println(request);
		}

		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

	}

	public int deleteDocument(UserContext uc, String token) {

		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create( // TODO This works for our
														// URIs, but might not
														// for others
				config.getPreference("prefixes") + "WITH <"
						+ uc.getNamedGraph() + "> " + "DELETE "
						+ "{ ?s ?p ?o }" + "WHERE" + "{" + "   ?s ?p ?o ."
						+ "   FILTER ( CONTAINS(str(?s), \"" + "document/"
						+ token + "\") )" + "}");
		/* @formatter:on */

		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

		try {
			Connection con = connectDB();
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM documents WHERE token = ? ");
			pst.setString(1, token);
			return pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public void addDocument(String namedGraph, String documentURI,
			String token, String fileName, Boolean global, String docType) {

		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "INSERT DATA "
				+ "{ "
				+ "	GRAPH <"
				+ namedGraph
				+ "> { "
				+ "			<"
				+ documentURI
				+ "> 	a 					pcfapp:Document ;"
				+ " 									pcfapp:token 		\""
				+ token
				+ "\"^^xsd:string ; "
				+ " 									pcfapp:fileName 	\""
				+ fileName
				+ "\"^^xsd:string ; "
				+ (global ? "						pcfapp:docGlobal	\"true\"^^xsd:boolean ; "
						: "")
				+ "									pcfapp:docType		pcfapp:"
				+ docType
				+ " ." + "	} " + "}");
		/* @formatter:on */

		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

	}

	public void addSupplierDocs(UserContext uc, HttpServletRequest httpRequest) {

		String fileToken;
		String fileName;
		String documentObjectURI = config.getPreference("newContractURL")
				+ "document/";

		String[] docTypes = { "QualityCertificate", "CompanyProfile",
				"FinancialStatements" };
		List<String> docTypesList = Arrays.asList(docTypes);

		Collection<Part> parts;
		try {
			parts = httpRequest.getParts();

			Iterator<Part> i = parts.iterator();
			if (!i.hasNext())
				System.out.println(":(((");
			while (i.hasNext()) {
				Part part = i.next();
				System.out.println(part.getName());

				if (docTypesList.contains(part.getName())) {
					fileToken = UUID.randomUUID().toString();
					fileName = utils.processFileUpload(httpRequest, part,
							uc.getUserName(), fileToken);
					if (fileName != null && !fileName.isEmpty()) {
						addDocument(uc.getNamedGraph(), documentObjectURI
								+ fileToken, fileToken, fileName, true,
								part.getName());
					}
				}
			}

		} catch (IllegalStateException | IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for (int i = 0; i < fileArrays.length; i++) {
		// fileToken = UUID.randomUUID().toString();
		// fileName = utils.processFileUpload(httpRequest, fileArrays[i],
		// uc.getUserName(), fileToken);
		// if (fileName != null && !fileName.isEmpty()) {
		// addDocument(uc.getNamedGraph(), documentObjectURI + fileToken,
		// fileToken, fileName, true, docTypes[i]);
		// }
		// }
	}

	public String getMailFromEntity(String entity) {

		PreparedStatement pst;
		try {
			Connection con = connectDB();
			pst = con
					.prepareStatement("SELECT * FROM user_preferences WHERE preference = 'businessEntity' AND value = ? ");
			pst.setString(1, entity);
			java.sql.ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString("username");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getMailFromNS(String entity) {

		PreparedStatement pst;
		try {
			Connection con = connectDB();
			pst = con
					.prepareStatement("SELECT * FROM user_preferences WHERE preference = 'namedGraph' AND value = ? ");
			pst.setString(1, entity);
			java.sql.ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString("username");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public ResultSet getBuyerActivityData(String namedGraph) {

		/* @formatter:off */
		Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
				config.getPreference("prefixes")
						+ "SELECT ?subject ?subjectTitle ?object ?objectTitle ?entity ?entityName ?price ?currency ?date ?type "
						+ "WHERE "
						+ "{ "
						+ "	GRAPH <"
						+ namedGraph
						+ "> "
						+ "	{ "
						+ "		{ "
						+ "			SELECT ?subject ?subjectTitle ?date (\"created\" as ?type) "
						+ "			WHERE "
						+ "			{ "
						+ "				?subject a pc:Contract . "
						+ "				?subject pcfapp:created ?date . "
						+ "				?subject dc:title ?subjectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			SELECT ?subject ?subjectTitle ?date (\"published\" as ?type) "
						+ "			WHERE "
						+ "			{ "
						+ "				?subject a pc:Contract . "
						+ "				?subject pcfapp:published ?date . "
						+ "				?subject dc:title ?subjectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			SELECT ?subject ?subjectTitle ?date (\"cancelled\" as ?type) "
						+ "			WHERE "
						+ "			{ "
						+ "				?subject a pc:Contract . "
						+ "				?subject pcfapp:cancelled ?date . "
						+ "				?subject dc:title ?subjectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			SELECT ?subject ?subjectTitle ?date (\"withdrawn\" as ?type) "
						+ "			WHERE "
						+ "			{ "
						+ "				?subject a pc:Contract . "
						+ "				?subject pcfapp:withdrawn ?date . "
						+ "				?subject dc:title ?subjectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			SELECT ?subject ?subjectTitle ?date (\"completed\" as ?type) "
						+ "			WHERE "
						+ "			{ "
						+ "				?subject a pc:Contract . "
						+ "				?subject pcfapp:completed ?date . "
						+ "				?subject dc:title ?subjectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			SELECT ?subject ?subjectTitle ?entity ?entityName ?object ?price ?currency ?date (\"awarded\" as ?type) "
						+ "			WHERE "
						+ "			{ "
						+ "				?subject a pc:Contract . "
						+ "				?subject pcfapp:awarded ?date . "
						+ "				?subject dc:title ?subjectTitle . "
						+ "				?subject pc:awardedTender ?object ."
						+ "				?object pc:offeredPrice ?offer ."
						+ "				?offer gr:hasCurrency ?currency ."
						+ "				?offer gr:hasCurrencyValue ?price ."
						+ "				?object pc:supplier ?entity ."
						+ "				?entity dc:title ?entityName "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			select ?subject ?subjectTitle ?object ?objectTitle ?entity ?entityName ?price ?currency ?date (\"tenderSubmitted\" as ?type) "
						+ "			where "
						+ "			{ "
						+ "				?subject a pc:tender . "
						+ "				?subject pc:supplier ?entity ."
						+ "				?entity dc:title ?entityName . "
						+ "				?subject pc:offeredPrice ?offer ."
						+ "				?offer gr:hasCurrency ?currency ."
						+ "				?offer gr:hasCurrencyValue ?price ."
						+ "				?subject pcfapp:submitted ?date . "
						+ "				?subject pc:contract ?object . "
						+ "				?object dc:title ?objectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			select ?subject ?subjectTitle ?object ?objectTitle ?entity ?entityName ?price ?currency ?date (\"tenderWithdrawn\" as ?type) "
						+ "			where "
						+ "			{ "
						+ "				?subject a pc:tender . "
						+ "				?subject pc:supplier ?entity ."
						+ "				?entity dc:title ?entityName . "
						+ "				?subject pc:offeredPrice ?offer ."
						+ "				?offer gr:hasCurrency ?currency ."
						+ "				?offer gr:hasCurrencyValue ?price ."
						+ "				?subject pcfapp:withdrawn ?date . "
						+ "				?subject pc:contract ?object . "
						+ "				?object dc:title ?objectTitle "
						+ "			} "
						+ "		} "
						+ "		UNION "
						+ "		{ "
						+ "			select ?subject ?subjectTitle ?object ?objectTitle ?entity ?entityName ?price ?currency ?date (\"tenderRejected\" as ?type) "
						+ "			where " + "			{ " + "				?subject a pc:tender . "
						+ "				?subject pc:supplier ?entity ."
						+ "				?entity dc:title ?entityName . "
						+ "				?subject pc:offeredPrice ?offer ."
						+ "				?offer gr:hasCurrency ?currency ."
						+ "				?offer gr:hasCurrencyValue ?price ."
						+ "				?subject pcfapp:rejected ?date . "
						+ "				?subject pc:contract ?object . "
						+ "				?object dc:title ?objectTitle " + "			} "
						+ "		} " + "	} " + "} " + "ORDER BY DESC(?date) ");
		/* @formatter:on */

		if (debug) {
			System.out
					.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();
	}

	public JsonObject getBuyerStats(String namedGraph, boolean total) {

		/* @formatter:off */
		Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
				config.getPreference("prefixes")
						+ "SELECT ?status (count(?a) as ?count) " + "WHERE "
						+ "  { GRAPH "
						+ ((total) ? " ?g " : " <" + namedGraph + "> ")
						+ "      {  " + "			?a a 				pc:Contract ;"
						+ "			   pcfapp:status 	?status " + "	  } " + "	}"
						+ "GROUP BY ?status ");
		/* @formatter:on */

		if (debug) {
			System.out
					.println("###################################################");
			System.out.println(query);
		}

		ResultSet rs = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();

		JsonObject data = new JsonObject();

		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			if (qs.contains("status") && qs.contains("count")) {
				qs.get("status").asResource().getLocalName();
				qs.get("count").asLiteral().getInt();
				data.addProperty(qs.get("status").asResource().getLocalName(),
						qs.get("count").asLiteral().getInt());
			}
		}

		JsonObject ret = new JsonObject();
		ret.add("data", data);

		return ret;
	}

}
