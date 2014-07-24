package cz.opendata.tenderstats.pcfapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import cz.opendata.tenderstats.ComponentConfiguration;
import cz.opendata.tenderstats.Config;
import cz.opendata.tenderstats.Mailer;
import cz.opendata.tenderstats.UserContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCFappModelContract implements Serializable {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCFappModelContract.class);	
	
	private static final long serialVersionUID = 4317705214419986680L;

	private boolean debug = true;

	private ComponentConfiguration config;
	private PCFappModel model;

	public PCFappModelContract(ComponentConfiguration config) {
		model = new PCFappModel(config);
		this.config = config;
	}

	public JsonObject getPublicContractAsJson(Model contract, String contractURI) throws PCFappException {

		if ( !contract.containsResource(ResourceFactory.createResource(contractURI)) ) {
			throw new PCFappException("Contract "+contractURI+" not found.");
		}
		
		JsonObject json = new JsonObject();
		Resource contractRes = contract.getResource(contractURI);

		// contracting authority name
		if (contractRes.hasProperty(PCFappModel.pc_contractingAuthority)) {
			JsonObject authority = new JsonObject();
			authority.addProperty("entity", contractRes.getProperty(PCFappModel.pc_contractingAuthority).getObject().toString());
			authority.addProperty("name", contractRes.getProperty(PCFappModel.pc_contractingAuthority).getObject().asResource()
					.getProperty(PCFappModel.gr_legalName).getObject().asLiteral().getString());
			json.add("contractingAuthority", authority);
		}

		// title
		if (contractRes.hasProperty(PCFappModel.dc_title))
			json.addProperty("title", contractRes.getProperty(PCFappModel.dc_title).getObject().asLiteral().getString());
		
		// desc
		if (contractRes.hasProperty(PCFappModel.dc_description))
			json.addProperty("description", contractRes.getProperty(PCFappModel.dc_description).getObject().asLiteral()
					.getString());

		// deadline
		if (contractRes.hasProperty(PCFappModel.pc_deadline)) {
			String deadline = contractRes.getProperty(PCFappModel.pc_deadline).getObject().asLiteral().getString();
			json.addProperty("deadline", deadline.substring(0, deadline.indexOf('T')));
		}
		
		// award criterion
		if (contractRes.hasProperty(PCFappModel.pc_awardCriteriaCombination)) {
			Resource criteria =
					contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_awardCriteriaCombination).toString());
			StmtIterator awardCrits = criteria.listProperties(PCFappModel.pc_awardCriterion);
			JsonObject critsArray = new JsonObject();
			while (awardCrits.hasNext()) {
				Statement s = awardCrits.nextStatement();
				Resource criterion = contract.getResource(s.getObject().toString());
				if (criterion.hasProperty(PCFappModel.pc_weightedCriterion)
						&& criterion.hasProperty(PCFappModel.pc_weightedCriterion)) {
					String prop =
							criterion.getProperty(PCFappModel.pc_weightedCriterion).getObject().toString()
									.replace(PCFappModel.pccrit, "");
					String val = criterion.getProperty(PCFappModel.pc_criterionWeight).getObject().asLiteral().getString();
					critsArray.addProperty(prop, val);
				}
			}
			json.add("criteria", critsArray);
		}

		// cpv main
		json.addProperty("mainCPV",
				contractRes.getProperty(PCFappModel.pc_mainCPV).getObject().toString()
						.replaceAll(Config.cc().getPrefix("cpv"), ""));
		// cpv additional
		StmtIterator iter = contractRes.listProperties(PCFappModel.pc_additionalCPV);
		JsonArray array = new JsonArray();
		while (iter.hasNext()) {
			Statement s = iter.nextStatement();
			array.add(new JsonPrimitive(s.getObject().toString().replaceAll(Config.cc().getPrefix("cpv"), "")));
		}
		json.add("additionalCPV", array);

		// / event type
				if (contractRes.hasProperty(PCFappModel.pcf_eventType))
					json.addProperty("eventType", contractRes.getProperty(PCFappModel.pcf_eventType).getString());

		
		// procurement method
		if (contractRes.hasProperty(PCFappModel.pc_procedureType))
			json.addProperty("procedureType", contractRes.getProperty(PCFappModel.pc_procedureType).getObject().asResource()
					.getLocalName().toString());
		
		// start - end date
		if (contractRes.hasProperty(PCFappModel.pc_startDate))
			json.addProperty("startDate", contractRes.getProperty(PCFappModel.pc_startDate).getString());
		if (contractRes.hasProperty(PCFappModel.pc_estimatedEndDate))
			json.addProperty("estimatedEndDate", contractRes.getProperty(PCFappModel.pc_estimatedEndDate).getString());
		// actual end
		if (contractRes.hasProperty(PCFappModel.pc_actualEndDate))
			json.addProperty("aend", contractRes.getProperty(PCFappModel.pc_actualEndDate).getString());

		// location
		if (contractRes.hasProperty(PCFappModel.pc_location)) {
			Resource location = contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_location).toString());
			if (location.hasProperty(PCFappModel.rdfs_label))
				json.addProperty("locationLabel", location.getProperty(PCFappModel.rdfs_label).getObject().asLiteral()
						.getString());
			if (location.hasProperty(PCFappModel.pce_hasParentRegion)) {
				String loc = location.getProperty(PCFappModel.pce_hasParentRegion).getObject().toString();
				json.addProperty("locationNUTS", loc.substring(loc.lastIndexOf('/') + 1, loc.length()));
			}
		}
		
		boolean addPrice = true;
		if ( contractRes.hasProperty(PCFappModel.pcf_confidentialPrice) ) {
			if ( contractRes.getProperty(PCFappModel.pcf_confidentialPrice).getObject().asLiteral().getBoolean() ) {
				addPrice = false;
			}
		}
		
		if ( addPrice ){
			// price
			if (contractRes.hasProperty(PCFappModel.pc_estimatedPrice)) {
				Resource price = contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_estimatedPrice).toString());
				if (price.hasProperty(PCFappModel.gr_hasCurrency))
					json.addProperty("currency", price.getProperty(PCFappModel.gr_hasCurrency).getString());
				if (price.hasProperty(PCFappModel.gr_hasCurrencyValue))
					json.addProperty("price", price.getProperty(PCFappModel.gr_hasCurrencyValue).getObject().asLiteral().getString());
			}

			// actual price
			if (contractRes.hasProperty(PCFappModel.pc_actualPrice)) {
				Resource price = contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_actualPrice).toString());
				if (price.hasProperty(PCFappModel.gr_hasCurrency))
					json.addProperty("acurrency", price.getProperty(PCFappModel.gr_hasCurrency).getString());
				if (price.hasProperty(PCFappModel.gr_hasCurrencyValue))
					json.addProperty("aprice", price.getProperty(PCFappModel.gr_hasCurrencyValue).getObject().asLiteral().getString());
			}
		}
		
		
		
		// sealed tenders
		if (contractRes.hasProperty(PCFappModel.pc_sealed))
			json.addProperty("tendersSealed", contractRes.getProperty(PCFappModel.pc_sealed).getObject().asLiteral().getBoolean());
				
		// tenderOpening
		if (contractRes.hasProperty(PCFappModel.pc_tenderOpening))
			json.addProperty("tendersOpening", contractRes.getProperty(PCFappModel.pc_tenderOpening).getString());

		
		return json;

	}

	public JsonObject getContractAsJson(String contractURI, String namedGraph) throws PCFappException {

		Model contract = getPrivateContract(contractURI, namedGraph, "none");
		
		if ( !contract.containsResource(ResourceFactory.createResource(contractURI)) ) {
			throw new PCFappException("Contract "+contractURI+" not found.");
		}
		
		Resource contractRes = contract.getResource(contractURI);
		
		JsonObject json = new JsonObject();
		
		json.addProperty("contractURI",contractURI);
		
		// contracting authority name
		try {
			JsonObject authority = new JsonObject();
			authority.addProperty("entity", contractRes.getRequiredProperty(PCFappModel.pc_contractingAuthority).getObject().toString());
			Resource supplier = contractRes.getRequiredProperty(PCFappModel.pc_contractingAuthority).getObject().asResource();			
			authority.addProperty("name", supplier.getRequiredProperty(PCFappModel.gr_legalName).getObject().asLiteral().getString());
			json.add("contractingAuthority", authority);
		} catch ( PropertyNotFoundException e ) {
			logger.warn(contractURI+" "+PCFappModel.pc_contractingAuthority.getLocalName()+" not found or has missing required property",e);
		}
		
		// status
		if ( contractRes.hasProperty(PCFappModel.pcf_status) ) {
			json.addProperty("status", contractRes.getProperty(PCFappModel.pcf_status).getObject().asResource().getLocalName());
		}
		
		// sealed tenders
		if (contractRes.hasProperty(PCFappModel.pc_sealed))
			json.addProperty("tendersSealed", contractRes.getProperty(PCFappModel.pc_sealed).getObject().asLiteral().getBoolean());
		
		// tenderOpening
		if (contractRes.hasProperty(PCFappModel.pc_tenderOpening))
			json.addProperty("tendersOpening", contractRes.getProperty(PCFappModel.pc_tenderOpening).getString());
				
		// title
		if (contractRes.hasProperty(PCFappModel.dc_title))
			json.addProperty("title", contractRes.getProperty(PCFappModel.dc_title).getObject().asLiteral().getString());
		// desc
		if (contractRes.hasProperty(PCFappModel.dc_description))
			json.addProperty("description", contractRes.getProperty(PCFappModel.dc_description).getObject().asLiteral()
					.getString());
		// cpv main
		json.addProperty("mainCPV",
				contractRes.getProperty(PCFappModel.pc_mainCPV).getObject().toString()
						.replaceAll(Config.cc().getPrefix("cpv"), ""));
		// cpv additional
		StmtIterator iter = contractRes.listProperties(PCFappModel.pc_additionalCPV);
		JsonArray array = new JsonArray();
		while (iter.hasNext()) {
			Statement s = iter.nextStatement();
			array.add(new JsonPrimitive(s.getObject().toString().replaceAll(Config.cc().getPrefix("cpv"), "")));
		}
		json.add("additionalCPV", array);
		// procurement method
		if (contractRes.hasProperty(PCFappModel.pc_procedureType))
			json.addProperty("procedureType", contractRes.getProperty(PCFappModel.pc_procedureType).getObject().asResource()
					.getLocalName().toString());
		// deadline
		if (contractRes.hasProperty(PCFappModel.pc_deadline)) {
			String deadline = contractRes.getProperty(PCFappModel.pc_deadline).getObject().asLiteral().getString();
			json.addProperty("deadline", deadline.substring(0, deadline.indexOf('T')));
		}
		// price
		if (contractRes.hasProperty(PCFappModel.pc_estimatedPrice)) {
			Resource price = contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_estimatedPrice).toString());
			if (price.hasProperty(PCFappModel.gr_hasCurrency))
				json.addProperty("currency", price.getProperty(PCFappModel.gr_hasCurrency).getString());
			if (price.hasProperty(PCFappModel.gr_hasCurrencyValue))
				json.addProperty("price", price.getProperty(PCFappModel.gr_hasCurrencyValue).getObject().asLiteral().getString());
		}
		// confidential
		if (contractRes.hasProperty(PCFappModel.pcf_confidentialPrice)) {
			json.addProperty("confidential", contractRes.getProperty(PCFappModel.pcf_confidentialPrice).getBoolean());
		} else {
			json.addProperty("confidential", false);
		}

		// PROJECT ID
		if (contractRes.hasProperty(PCFappModel.pcf_projectID))
			json.addProperty("projectID", contractRes.getProperty(PCFappModel.pcf_projectID).getObject().asLiteral().getString());
		// EVENT REFERENCE
		if (contractRes.hasProperty(PCFappModel.pcf_eventReference))
			json.addProperty("eventReference", contractRes.getProperty(PCFappModel.pcf_eventReference).getObject().asLiteral()
					.getString());

		// / event type
		if (contractRes.hasProperty(PCFappModel.pcf_eventType))
			json.addProperty("eventType", contractRes.getProperty(PCFappModel.pcf_eventType).getString());

		// start - end date
		if (contractRes.hasProperty(PCFappModel.pc_startDate))
			json.addProperty("startDate", contractRes.getProperty(PCFappModel.pc_startDate).getString());
		if (contractRes.hasProperty(PCFappModel.pc_estimatedEndDate))
			json.addProperty("estimatedEndDate", contractRes.getProperty(PCFappModel.pc_estimatedEndDate).getString());
		// location
		if (contractRes.hasProperty(PCFappModel.pc_location)) {
			Resource location = contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_location).toString());
			if (location.hasProperty(PCFappModel.rdfs_label))
				json.addProperty("locationLabel", location.getProperty(PCFappModel.rdfs_label).getObject().asLiteral()
						.getString());
			if (location.hasProperty(PCFappModel.pce_hasParentRegion)) {
				String loc = location.getProperty(PCFappModel.pce_hasParentRegion).getObject().toString();
				json.addProperty("locationNUTS", loc.substring(loc.lastIndexOf('/') + 1, loc.length()));
			}
		}

		// award criterion
		if (contractRes.hasProperty(PCFappModel.pc_awardCriteriaCombination)) {
			Resource criteria =
					contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_awardCriteriaCombination).toString());
			StmtIterator awardCrits = criteria.listProperties(PCFappModel.pc_awardCriterion);
			JsonObject critsArray = new JsonObject();
			while (awardCrits.hasNext()) {
				Statement s = awardCrits.nextStatement();
				Resource criterion = contract.getResource(s.getObject().toString());
				if (criterion.hasProperty(PCFappModel.pc_weightedCriterion)
						&& criterion.hasProperty(PCFappModel.pc_weightedCriterion)) {
					String prop =
							criterion.getProperty(PCFappModel.pc_weightedCriterion).getObject().toString()
									.replace(PCFappModel.pccrit, "");
					String val = criterion.getProperty(PCFappModel.pc_criterionWeight).getObject().asLiteral().getString();
					critsArray.addProperty(prop, val);
				}
			}
			json.add("criteria", critsArray);
		}

		// vcard
		if (contractRes.hasProperty(PCFappModel.pc_contact)) {
			Resource contact = contract.getResource(contractRes.getPropertyResourceValue(PCFappModel.pc_contact).toString());
			if (contact.hasProperty(PCFappModel.vc_fn))
				json.addProperty("vcFN", contact.getProperty(PCFappModel.vc_fn).getObject().asLiteral().getString());
			if (contact.hasProperty(PCFappModel.vc_note))
				json.addProperty("vcNote", contact.getProperty(PCFappModel.vc_note).getObject().asLiteral().getString());
			if (contact.hasProperty(PCFappModel.vc_email)) {
				String mail = contact.getProperty(PCFappModel.vc_email).getObject().toString();
				json.addProperty("vcEmail", mail.substring(mail.indexOf(':') + 1, mail.length()));
			}
			if (contact.hasProperty(PCFappModel.vc_tel)) {
				Resource phone = contract.getResource(contact.getPropertyResourceValue(PCFappModel.vc_tel).toString());
				if (phone.hasProperty(PCFappModel.rdfsns_value))
					json.addProperty("vcPhone", phone.getProperty(PCFappModel.rdfsns_value).getObject().asLiteral().getString());
			}
		}

		// files

		String token;
		String fileName;
		String docType;

		JsonArray docs = new JsonArray();
		StmtIterator i = contractRes.listProperties(PCFappModel.pcf_document);
		while (i.hasNext()) {
			Statement st = i.next();
			docType = st.getProperty(PCFappModel.pcf_documentType).getObject().asResource().getLocalName();
			token = st.getProperty(PCFappModel.pcf_documentToken).getObject().asLiteral().getString();
			fileName = st.getProperty(PCFappModel.pcf_documentFileName).getObject().asLiteral().getString();
			JsonObject fileGenTerms = new JsonObject();
			fileGenTerms.addProperty("token", token);
			fileGenTerms.addProperty("docType", docType);
			fileGenTerms.addProperty("fileName", fileName);
			docs.add(fileGenTerms);
		}
		// token = contractRes.getProperty(PCFappModel.pcf_document).getObject().asLiteral().getString();
		// fileName = getDocumentFileName(token);
		json.add("documents", docs);

		// invited suppliers
		StmtIterator suppliersIterator = contractRes.listProperties(PCFappModel.pcf_invitedSupplier);
		JsonArray invitedSuppliers = new JsonArray();
		while( suppliersIterator.hasNext() ) {
			Statement supplier = suppliersIterator.next();
			JsonObject newSupp = new JsonObject();
			
			Resource suppRes = supplier.getObject().asResource();
			newSupp.addProperty("beURI", suppRes.getURI());
			newSupp.addProperty("name", suppRes.getProperty(PCFappModel.gr_legalName).getObject().asLiteral().getString());
			
			invitedSuppliers.add(newSupp);
		}
		json.add("invitedSuppliers", invitedSuppliers);
		
		return json;
	}

	/**
	 * Returns private contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	public Model getPrivateContract(String contractURI, String namedGraph, String tenders) {

		String tendersFilter = "";

		if (tenders != null) {
			switch (tenders) {

				case "none":
					tendersFilter =
							"MINUS { ?o1 a pc:tender } MINUS { ?o1 a pc:awardedTender } MINUS { ?o1 a pc:withdrawnTender }";
					break;

				case "all":
					tendersFilter = "";
					break;

				case "awarded":
					tendersFilter = "MINUS { ?o1 a pc:tender } MINUS { ?o1 a pc:withdrawnTender }";
					break;
			}
		} else {
			tendersFilter = "";

		}

		/* @formatter:off */
		Query query = QueryFactory.create(
				config.getPreference("prefixes") + 
				"CONSTRUCT  " + 
				"  { ?contractURI ?p1 ?o1 . " + 
				"    ?o1 ?p2 ?o2 ." +
				"    ?o2 ?p3 ?o3 . " + 
				"    ?o3 ?p4 ?o4 . " + 
				"    ?o4 ?p5 ?o5 ." +
				"	 ?o1 ?beP ?beO ." +
				"	 ?o1 s:email ?beEmail ." +
				"  } " + 				
				"WHERE " + 
				"  {" +
				((namedGraph != null) ? "	GRAPH <" + namedGraph + "> " : " GRAPH ?g " )+
				"	 {" +
				"	 ?contractURI ?p1 ?o1 . " + 
				"    ?contractURI a pc:Contract . " +				
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
				"	   }" +				
				"	}" +
				"	" + tendersFilter + 
				"    OPTIONAL " +
				"		{ " +
				"			SERVICE <"+ config.getSparqlPublicQuery() +"> {" +
				"				GRAPH ?g {" +
				"					?o1 a gr:BusinessEntity . ?o1 ?beP ?beO " +				
				"				}" +
				"			}" +
				"		}" +
				"	 OPTIONAL {" +				
				"		GRAPH <http://ld.opendata.cz/tenderstats/dataset/pcfapp/BEemails> {" +
				"			?o1 s:email ?beEmail " +
				"		}" +
				"	 }" +				
				"  } " +
				"VALUES ?contractURI { <" + contractURI + "> }");
		/* @formatter:on */
		Model contract = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

		System.out.println(query);
		// System.out.println("###################################################");
		contract.write(System.out, "Turtle");

		return contract;
	};

	/**
	 * Returns private tenders for contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 * @param
	 */
	public Model getPrivateTendersForContract(String namedGraph, String contractURI, boolean awarded) {
		/* @formatter:off */
		Query query = QueryFactory.create(			
				config.getPreference("prefixes") + 
				"CONSTRUCT " +  
				"  { ?contractURI " + ( (awarded) ? "pc:awardedTender" : "pc:tender" ) + " ?tender . "+ 
				"	 ?tender ?p1 ?o1 . " +
				"	 ?price ?p2 ?o2 "+
				"} " +
				"FROM <"+ namedGraph +"> "+ 
				"WHERE " +
				"{  ?contractURI " + ( (awarded) ? "pc:awardedTender" : "pc:tender" ) + " ?tender . " +
				"	?tender ?p1 ?o1 . " +
				"	?tender pc:offeredPrice ?price . " +
				"	?price ?p2 ?o2 . " +
				"	?contractURI rdf:type pc:Contract ." +				
				"} " +
				"VALUES ?contractURI { <"+ contractURI +"> } ");
		/* @formatter:on */
		Model tenders = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

		// System.out.println(query.toString());
		// System.out.println("###################################################");
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
		Query query = QueryFactory.create(
				config.getPreference("prefixes") + 
				"CONSTRUCT  " + 
				"  { ?contractURI ?p1 ?o1 . " + 
				"    ?o1 ?p2 ?o2 . " + 
				"    ?o2 ?p3 ?o3 . " + 
				"    ?o3 ?p4 ?o4 . " + 
				"    ?o4 ?p5 ?o5 .} " + 	
				// TODO removed because our default graph contains union of all datasets -> needed for ContractsComponent
				//"FROM <" + config.getPreference("publicGraphName") + "> " +
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
		Model contract = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execConstruct();

		if (debug) {
			System.out.println("###################################################");
			contract.write(System.out, "Turtle");
		}

		return contract;
	};

	/**
	 * Adds new contract to user's private dataspace
	 * 
	 * @param uc
	 *            UserContext of user to whom the contract belongs
	 */
	public void addPrivateContract(UserContext uc, String title, String description, String cpv1, String cpv2, String cpv3,
			String projectID, String deadline, String estimatedPrice, String estimatedPriceCurrency, String priceIsConfidential,
			String estimatedStartDate, String estimatedEndDate, String location, String nuts, String evalPrice, String evalTech,
			String evalDate, String contactPerson, String contactEmail, String contactPhone, String contactDescription,
			String eventReference, String procType, String eventType, String tendersSealed, String createdDate, String contractURL,
			HttpServletRequest httpRequest, Model contract) {

		if (eventType == null)
			eventType = "";
		if (contractURL == null) {
			String contractID = UUID.randomUUID().toString();
			contractURL = config.getPrefix("contract") + contractID;
		}

		String priceURL = contractURL + "/price-specification/1";

		if (!deadline.contains("T")) {
			deadline += "T00:00:00";
		}

		String procedureURL = config.getPrefix("proctypes") + procType;

		String cpv1URL = config.getPrefix("cpv") + (cpv1 + "-").substring(0, (cpv1 + "-").indexOf('-'));
		String cpv2URL = config.getPrefix("cpv") + (cpv2 + "-").substring(0, (cpv2 + "-").indexOf('-'));
		String cpv3URL = config.getPrefix("cpv") + (cpv3 + "-").substring(0, (cpv3 + "-").indexOf('-'));

		String locationURL = contractURL + "/place/1";
		String nutsURL = config.getPrefix("nuts") + nuts.substring(0, (nuts + "#").indexOf('#'));

		String awardCombiURL = contractURL + "/combination-of-contract-award-criteria/1";
		String awardCriterion1URL = awardCombiURL + "/contract-award-criterion/1";
		String awardCriterion2URL = awardCombiURL + "/contract-award-criterion/2";
		String awardCriterion3URL = awardCombiURL + "/contract-award-criterion/3";

		String vcardURL = contractURL + "/vcard-class/1";
		String telURL = contractURL + "/vcard-class/1/tel/1";

		String invitations = "";
		if ( contract != null ) {			
			Resource contractRes = contract.getResource(contractURL);
			StmtIterator i = contractRes.listProperties(PCFappModel.pcf_invitedSupplier);
			while ( i.hasNext() ) {
				invitations += "; pcfapp:invitedSupplier <"+i.next().getObject().asResource().getURI()+"> ";
			}			
		}
		
		String modifiedDate = "";
		try {
			DatatypeFactory df = DatatypeFactory.newInstance();
			XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(new GregorianCalendar());
			modifiedDate = calendar.toXMLFormat();
			// modifiedDate = modifiedDate.substring(0, modifiedDate.indexOf('T'));
			if (createdDate == null)
				createdDate = modifiedDate; // TODO select from old contract on edit / update
		} catch (DatatypeConfigurationException unused) {
		}

		// documents upload

		String documents = "";
		String fileToken;
		String fileName;
		String documentObjectURI = config.getPrefix("contract") + "document/";
		
		String[] docTypes =
				{ "GeneralTerms", "CallDocument", "Amendment", "Responses", "TechnicalSpecifications", "PriceDelivery",
						"BidSecurity", "PerformanceSecurity", "BidSubmissionForm" };

		List<String> docTypesList = Arrays.asList(docTypes);
		Collection<Part> parts;
		try {
			parts = httpRequest.getParts();			
			Iterator<Part> i = parts.iterator();		
			while ( i.hasNext() ) {				
				Part part = i.next();
				System.out.println(part.getName());
				
				if ( docTypesList.contains( part.getName() ) ) {
				
					fileToken = UUID.randomUUID().toString();
					fileName = model.utils.processFileUpload(httpRequest, part, uc.getUserName(), uc.getRole().getId(), fileToken);
					if (fileName != null && !fileName.isEmpty()) {
						String documentURI = documentObjectURI + fileToken;
						documents += " ; pcfapp:document	<" + documentURI + "> ";
						model.addDocument(uc.getNamedGraph(), documentURI, fileToken, fileName, false, part.getName());
					}				
				}			
			}	
			
		} catch (IllegalStateException | IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		String beURL = uc.getPreference("businessEntity");
		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create( // TODO rewrite using JENA Model or SPARQL VALUES
				config.getPreference("prefixes") +
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"		<" + contractURL + ">	a	 					pc:Contract " + 
				";								dc:title				\"" + title + "\"@en" +
				";								pcfapp:status			pcfapp:Prepared " +
				
				( !description.isEmpty() ?
				";								dc:description			\"" + description
																		.replaceAll("(\r\n|[\r\n])+", " ") + "\"@en" : "" ) +
				( !projectID.isEmpty() ?
				";								pcfapp:projectID		\"" + projectID + "\"@en" : "" ) +
				( !createdDate.isEmpty() ?
				";								pcfapp:created			\"" + createdDate + "\"^^xsd:dateTime" : "" ) +
				( !eventType.isEmpty() ?
				";								pcfapp:eventType		\"" + eventType + "\"^^xsd:string" : "" ) +
				( !modifiedDate.isEmpty() ?
				";								pcfapp:modified			\"" + modifiedDate + "\"^^xsd:dateTime" : "" ) +
				( !deadline.isEmpty() ?
				";								pc:tenderDeadline		\"" + deadline + "\"^^xsd:dateTime" : "" ) +
				( !estimatedStartDate.isEmpty() ?
				";								pc:startDate			\"" + estimatedStartDate + "\"^^xsd:date" : "" ) +
				( !estimatedEndDate.isEmpty() ?
				";								pc:estimatedEndDate		\"" + estimatedEndDate + "\"^^xsd:date" : "" ) +
				( !eventReference.isEmpty() ?
				";								pcfapp:eventReferenceField	\"" + eventReference + "\"^^xsd:string" : "" ) +
				( !procType.isEmpty() ?
				";								pc:procedureType		<" + procedureURL + ">" : "" ) +
				( !documents.isEmpty() ? documents : "" ) +				
				";								pc:tendersSealed		\""+ ( tendersSealed!= null && !tendersSealed.isEmpty() ? "true" : "false" ) + "\"^^xsd:boolean " +
				( !cpv1.isEmpty() ?
				";								pc:mainObject			<" + cpv1URL + ">" : "" ) +
				( !cpv2.isEmpty() ?
				";								pc:additionalObject		<" + cpv2URL + ">" : "" ) +
				( !cpv3.isEmpty() ?
				";								pc:additionalObject		<" + cpv3URL + ">" : "" ) +						
				";								pc:contractingAuthority	<" + beURL + ">" +
				";								pc:awardCriteriaCombination <" + awardCombiURL + ">" +
				( !contactPerson.isEmpty() ?
				";								pc:contact				<" + vcardURL + "> " : "" ) +
				( priceIsConfidential != null && !priceIsConfidential.isEmpty() ?
				";								pcfapp:confidentialPrice true " : "" ) +
				( !invitations.isEmpty() ? invitations : "" ) +
				( !estimatedPrice.isEmpty() ?
				";								pc:estimatedPrice		<" + priceURL + "> " : "" ) +
				"		. " + 
				( !estimatedPrice.isEmpty() ?
				"		<" + priceURL + ">		gr:hasCurrencyValue		\"" + estimatedPrice + "\"^^xsd:float" +
				";								gr:hasCurrency			\"" + estimatedPriceCurrency + "\"" +
				";								a 						gr:UnitPriceSpecification" +
				"		. " : "" ) +
				( !location.isEmpty() ?
				"		<" + contractURL + ">	pc:location 			<" + locationURL + "> . " +
				"		<" + locationURL + ">	a 						s:Place " +
				";								rdfs:label				\"" + location + "\"@en" +
				( !nuts.isEmpty() ?
				";								pceu:hasParentRegion	<" + nutsURL + ">" : "" ) +
				"		. " : "" ) +
				"		<" + awardCombiURL + ">	a 						pc:AwardCriteriaCombination " +
				( !evalPrice.isEmpty() ?
				";								pc:awardCriterion		<" + awardCriterion1URL + "> " : "" ) +
				( !evalTech.isEmpty() ?
				";								pc:awardCriterion		<" + awardCriterion2URL + "> " : "" ) +
				( !evalDate.isEmpty() ?
				";								pc:awardCriterion		<" + awardCriterion3URL + "> " : "" ) +
				( !evalPrice.isEmpty() ?
				"  . " +
				"  <" + awardCriterion1URL + "> a 						pc:CriterionWeighting " + 
				";								pc:weightedCriterion 	criteria:LowestPrice " +
				";								pc:criterionWeight 		\"" + evalPrice + "\"^^pcdt:percentage" : "" ) +
				( !evalTech.isEmpty() ?
				"  . " +
				"  <" + awardCriterion2URL + "> a 						pc:CriterionWeighting " + 
				";								pc:weightedCriterion 	criteria:TechnicalQuality " +
				";								pc:criterionWeight  	\"" + evalTech + "\"^^pcdt:percentage" : "" ) +
				( !evalDate.isEmpty() ?
				"  . " +
				"  <" + awardCriterion3URL + "> a 						pc:CriterionWeighting " + 
				";								pc:weightedCriterion 	criteria:BestDate " +
				";								pc:criterionWeight  	\"" + evalDate + "\"^^pcdt:percentage" : "" ) +
				( !contactPerson.isEmpty() ?
				"  . " +
				"  <" + vcardURL + "> 			a 						vcard:VCard " + 
				";								vcard:fn				\"" + contactPerson + "\"@en" + 
				( !contactDescription.isEmpty() ?
				";								vcard:note				\"" + contactDescription
																		.replaceAll("(\r\n|[\r\n])+", " ") + "\"@en" : "" ) +
				( !contactEmail.isEmpty() ?
				";								vcard:email				<mailto:" + contactEmail + ">" : "" ) +
				( !contactPhone.isEmpty() ?
				";								vcard:tel 				<" + telURL + ">" : "" ) +
				( !contactPerson.isEmpty() && !contactPhone.isEmpty() ?
				"  . " +				
				"  <" + telURL + "> 			a 						vcard:Work " + 
				";								rdf:value				\"" + contactPhone + "\"" : "" )
				: "" ) +
				" . " +
				"	} " +
				"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		System.out.println(request);

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Deletes private contract with specified URL.
	 * 
	 * @param uc
	 * @param contractURL
	 * @throws ServletException
	 */
	public void deletePrivateContract(UserContext uc, String contractURL, boolean documents) throws ServletException {
		
		String contractID = contractURL.substring(contractURL.lastIndexOf('/') + 1);
		System.out.println("delete "+contractID);		

		// deletes document objects
		if ( documents ) {
			Model contractModel = getPrivateContract(contractURL, uc.getNamedGraph(), "none");
			StmtIterator i = contractModel.getResource(contractURL).listProperties(PCFappModel.pcf_document);
			while ( i.hasNext() ) {				
				String token = i.next().getObject().asResource().getURI().substring(contractURL.lastIndexOf('/') + 1);
				model.unlinkDocument(uc, contractURL, token);				
			}
			
		}
		
		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create(	// TODO This works for our URIs, but might not for others
			config.getPreference("prefixes") +
			"WITH <" + uc.getNamedGraph() + "> " + 
			"DELETE " + 
			"{" +
			"	?s ?p ?o . " +
			( (documents) ? " ?doc ?prop ?val " : "" ) +
			"}" + 
			"WHERE" + 
			"{" + 
			"   ?s ?p ?o ." +
			(
				(documents) ?
				" OPTIONAL { ?con pcfapp:document ?doc ." +
				" ?doc ?prop ?val " +
				" VALUES ?con { <"+contractURL+"> } } "				
				:
				" FILTER( ?p NOT IN(pcfapp:document)) ."
			) +			
			"   FILTER ( CONTAINS(str(?s), \"" + contractID + "\") )" + 
			"}");
		/* @formatter:on */

		logger.debug(request.toString());
		
		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Deletes public contract with specified URL and associated notices.
	 * 
	 * @param uc
	 * @param contractURL
	 * @throws ServletException
	 */
	public void deletePublicContract(UserContext uc, String contractURL) throws ServletException {

		String contractID = contractURL.substring(contractURL.lastIndexOf('/') + 1);

		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create(	// TODO This works for our URIs, but might not for others
			config.getPreference("prefixes") +
			"WITH <" + config.getPreference("publicGraphName") + "> " + 
			"DELETE " + 
			"{ ?s ?p ?o }" + 
			"WHERE" + 
			"{" + 
			"   ?s ?p ?o ." + 
			"   FILTER ( CONTAINS(str(?s), \"" + contractID + "\") )" +  
			"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(request);

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
		upr.execute();

		String noticeURL = contractURL + "/notice/1";

		/* @formatter:off */
		request = UpdateFactory.create(
			config.getPreference("prefixes") + 
			"INSERT DATA" +
			"{ " +
			"	GRAPH <" + uc.getNamedGraph() + "> {" +
			"		<" + contractURL + ">			pcfapp:withdrawn				\"true\"^^xsd:boolean " +
			"	}" +
			"} ; " +
			"DELETE DATA" + 
			"{ " +
			"	GRAPH <" + uc.getNamedGraph() + "> {" +
			"		<" + contractURL + ">			pc:notice				<" + noticeURL + "> ." +
			"	}" +
			"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(request);

		upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

		/* @formatter:off */
		request = UpdateFactory.create(
			config.getPreference("prefixes") +
			"WITH <" + uc.getNamedGraph() + "> " + 
			"DELETE " + 
			"{ <" + noticeURL + "> ?p ?o " +			
			"}" + 
			"WHERE" + 
			"{ " +
			"	<" + noticeURL + "> ?p ?o " +
			"}");
		/* @formatter:on */

		// System.out.println("###################################################");
		// System.out.println(request);

		upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

		/* @formatter:off */
		request = UpdateFactory.create(
			config.getPreference("prefixes") +
			"WITH <" + config.getPreference("publicGraphName") + "> " + 
			"DELETE " + 
			"{ <" + noticeURL + "> ?p ?o " +			
			"}" + 
			"WHERE" + 
			"{ " +
			"	<" + noticeURL + "> ?p ?o " +
			"}");
		/* @formatter:on */

		upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
		upr.execute();
	}

	/*
	 * docasne naviazanie na tender
	 */
	public void addPrivateTender(UserContext uc, String contractURL, String buyerURL, String description, String currencyValue,
			String currency, String startDate, String endDate, String certs, String profile, String fin,
			HttpServletRequest httpRequest) throws ServletException {

		try {
			contractURL = URLDecoder.decode(contractURL, "UTF-8").toString();
			buyerURL = URLDecoder.decode(buyerURL, "UTF-8").toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String newTenderURL = "http://ld.opendata.cz/resource/pcfilingapp/tender/" + UUID.randomUUID().toString();
		String offeredPriceURL = newTenderURL + "/price";

		Model contract = getPrivateContract(contractURL, buyerURL, "none");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		contract.write(baos, "N-TRIPLE");

		// documents upload

		String documents = "";
		String fileToken;
		String fileName;
		String documentObjectURI = config.getPrefix("contract") + "document/";

		if (certs != null && !certs.isEmpty())
			documents += " ; pcfapp:document <" + documentObjectURI + certs + "> ";
		if (profile != null && !profile.isEmpty())
			documents += " ; pcfapp:document <" + documentObjectURI + profile + "> ";
		if (fin != null && !fin.isEmpty())
			documents += " ; pcfapp:document <" + documentObjectURI + fin + "> ";

		//final String[] fileArrays = { "inputFileOffer", "inputFileTechSpecs", "inputFilePriceDelivery", "inputFileRequested" };
		final String[] docTypes = { "Offer", "TechSpecs", "PriceDelivery", "Requested" };
		List<String> docTypesList = Arrays.asList(docTypes);
		
		Collection<Part> parts;
		try {
			parts = httpRequest.getParts();
			
			Iterator<Part> i = parts.iterator();
			if ( !i.hasNext() ) System.out.println(":((("); 
			while ( i.hasNext() ) {				
				Part part = i.next();
				System.out.println(part.getName());
				
				if ( docTypesList.contains( part.getName() ) ) {
				
					fileToken = UUID.randomUUID().toString();
					fileName = model.utils.processFileUpload(httpRequest, part, uc.getUserName(), uc.getRole().getId(), fileToken);
					if (fileName != null && !fileName.isEmpty()) {
						String documentURI = documentObjectURI + fileToken;
						documents += " ; pcfapp:document	<" + documentURI + "> ";
						model.addDocument(uc.getNamedGraph(), documentURI, fileToken, fileName, false, part.getName());
					}				
				}			
			}	
			
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		// System.out.println("addingTenderTemp");

		// System.out.println(new String(baos.toString()));

		// if ( !contract.contains(null, ResourceFactory.createProperty(pc, "tender"),(RDFNode) null) )
		// {
		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create( 
				config.getPreference("prefixes") +
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"		<" + newTenderURL + ">	    a							pc:tender" +
				";									pc:offeredPrice				<" + offeredPriceURL + ">" +
				";									pc:contract					<" + contractURL + ">" +
				";									pcfapp:buyer					<" + buyerURL + "> " +
				documents +
				";									pc:supplier					<" + uc.getPreference("businessEntity") + "> " +
				";									dc:description				\"" + description
																		.replaceAll("(\r\n|[\r\n])+", " ") + "\"@en" +
				";									pc:startDate				\"" + startDate + "\"^^xsd:date" +
				";									pc:endDate			\"" + endDate + "\"^^xsd:date" +
				"		. " +
				"		<" + offeredPriceURL + ">	a							gr:UnitPriceSpecification" +
				";									gr:hasCurrency				\"" + currency + "\"" +
				";									gr:hasCurrencyValue			\"" + currencyValue + "\"^^xsd:float" +
				"	} " +
				"}");
		/* @formatter:on */

		// System.out.println("request:");
		// System.out.println(request);
		// System.out.println(config.getSparqlPrivateUpdate());
		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
		// }
		// else
		// {
		// System.out.println("has tender");
		// }

	}

	private String ContractQuery(String namedGraph, String select, String query, boolean published) {

		if (select == null)
			select = "";
		if (query == null)
			query = "";

		/* @formatter:off */
		return config.getPreference("prefixes") +
		"SELECT DISTINCT ?contractURI ?title ?description ?price ?conf ?currency ?cpv1URL ?cpvAdd ?modified ?tendersCount " +
		" ?deadline ?publicationDate ?place " + select + " " +
		"WHERE " + 
		"  { GRAPH <" + namedGraph + "> " + 
		"      {      " + 
		"          { " + 
		"            ?contractURI rdf:type pc:Contract . " + 
		"            ?contractURI dc:title ?title . " + 
		( ( !published ) ? "" : 
		"            ?contractURI pc:notice ?notice . " + 
		"            ?notice pc:publicationDate ?publicationDate "
		) +		
		"			 { SELECT ?contractURI ((COUNT(?tenders)) as ?tendersCount ) " +
		" 			 	WHERE { ?contractURI a pc:Contract OPTIONAL {?contractURI pc:tender ?tenders} } " +
		"				 GROUP BY ?contractURI "+ 
		"			 }" +
		"			 OPTIONAL { ?contractURI	pcfapp:confidentialPrice ?conf }" +
		"			 OPTIONAL " + 
		"	 	       { ?contractURI dc:description ?description } " +
		"            OPTIONAL " + 
		"              { ?contractURI pc:estimatedPrice ?priceURI . " + 
		"                ?priceURI gr:hasCurrencyValue ?price . " + 
		"                ?priceURI gr:hasCurrency ?currency " + 
		"              } " + 
		"            OPTIONAL " + 
		"              { ?contractURI pcfapp:modified ?modified } " + 
		"            OPTIONAL " + 
		"              { ?contractURI pc:tenderDeadline ?deadline } " +
		"            OPTIONAL " + 
		"              { ?contractURI pc:location ?locationURI ." +
		"				 ?locationURI rdfs:label ?place } " + 
		"            OPTIONAL " + 
		"              { ?contractURI pc:mainObject ?cpv1URL } " +
		"			 OPTIONAL " +
		"			 { " +
		"				SELECT ?contractURI (group_concat( distinct ?cpv) as ?cpvAdd) " + 
		"				WHERE { GRAPH ?buyerURI { ?contractURI pc:additionalObject ?cpv } } " +
		"				GROUP BY ?contractURI " +
		"			 } "              
		+ query +		
		"          } " + 
		"      } " + 
		"  }" +	
		"ORDER BY ?modified";
		/* @formatter:on */
	}

	public ResultSet getContracts(String namedGraph) {
		/* @formatter:off */		
		String select = " ?sealed ?openingTime ";
		String queryS =		
		"            ?contractURI pc:notice ?notice . " + 
		"            ?notice pc:publicationDate ?publicationDate ." +
		"            ?contractURI pcfapp:status pcfapp:Published " +
		"            OPTIONAL { ?contractURI pc:tendersSealed ?sealed } " +
		"            OPTIONAL { ?contractURI pc:tenderOpeningDateTime ?openingTime } ";
		//"            FILTER NOT EXISTS { ?contractURI pc:awardedTender ?awarded } " +
		//"            FILTER NOT EXISTS { ?contractURI pcfapp:withdrawn \"true\"^^xsd:boolean } ";
		
		Query query = QueryFactory.create( ContractQuery(namedGraph,select,queryS,true));		
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

	}

	public ResultSet getPrivateContracts(String namedGraph) {
		/* @formatter:off */
		String select = "?created";
		String queryS =				
		"		" +
		"       OPTIONAL " + 
		"       { ?contractURI pcfapp:created ?created } " +
		"		?contractURI pcfapp:status 	pcfapp:Prepared ";
		//"		FILTER NOT EXISTS { ?contractURI pcfapp:withdrawn \"true\"^^xsd:boolean }" +
		//"		FILTER NOT EXISTS { ?contractURI pc:notice ?notice . " + 
		//"		  ?notice pc:publicationDate ?publicationDate " + 
		//"		} " ;
		
		Query query = QueryFactory.create( ContractQuery(namedGraph, select, queryS,false) );	
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

	}

	public ResultSet getWithdrawnContracts(String namedGraph) {
		/* @formatter:off */
		String queryS = 
		"			 ?contractURI pcfapp:status pcfapp:Withdrawn ";
		Query query = QueryFactory.create( ContractQuery(namedGraph, null, queryS,false));		
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

	}

	public ResultSet getCanceledContracts(String namedGraph) {
		/* @formatter:off */		
		String queryS = 

		"            ?contractURI pc:notice ?notice . " + 
		"            ?notice pc:publicationDate ?publicationDate ." +
		"            ?contractURI pcfapp:status pcfapp:Cancelled " ;
		//"            FILTER NOT EXISTS { ?contractURI pc:awardedTender ?awarded } " +
		//"            FILTER NOT EXISTS { ?contractURI pcfapp:withdrawn \"true\"^^xsd:boolean } ";
		
		Query query = QueryFactory.create( ContractQuery(namedGraph,null,queryS,true));		
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

	}

	public ResultSet getAwardedContracts(String namedGraph) {
		/* @formatter:off */		
		String select = " ?supplierName ?supplierURI ?aprice ?acurrency ?awarded ?tenderEndDate ";
		String queryS = 
		"			 ?contractURI pc:awardedTender ?awarded . " +
		"			 ?awarded	  pc:supplier	?supplierURI ." +
		"			 ?awarded 	  pc:offeredPrice  ?oPrice ." +
		"			 ?awarded 	  pc:endDate  ?tenderEndDate ." +
		"			 ?oPrice gr:hasCurrencyValue ?aprice ." +
		"			 ?oPrice gr:hasCurrency ?acurrency ." +
		"            ?notice pc:publicationDate ?publicationDate . " +		
		"			 ?contractURI pcfapp:status pcfapp:Awarded " +
		"			 OPTIONAL " + 
		"	 	       { GRAPH ?g { ?supplierURI gr:legalName ?supplierName } } "; 
		//"            FILTER NOT EXISTS { ?contractURI pc:actualEndDate ?endDate } ";
		
		Query query = QueryFactory.create( ContractQuery(namedGraph, select, queryS, true) );		
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

	}

	public ResultSet getCompletedContracts(String namedGraph) {
		/* @formatter:off */
		String select = "?endDate ?aprice ?acurrency ?supplierURI ?supplierName ?awarded "; 
		String queryS = 
		"			 ?contractURI pc:actualPrice  ?actualPrice ." +
		"			 ?actualPrice gr:hasCurrencyValue ?aprice ." +
		"			 ?actualPrice gr:hasCurrency ?acurrency ." +
		"			 ?contractURI pc:awardedTender ?awarded . " +
		"			 ?awarded	  pc:supplier	?supplierURI ." +
		"			 ?contractURI pc:actualEndDate ?endDate . " +
		"			 ?contractURI pcfapp:status pcfapp:Completed " +
		"			 OPTIONAL " + 
		"	 	       { GRAPH ?g { ?supplierURI gr:legalName ?supplierName } } ";	
		
		Query query = QueryFactory.create(ContractQuery(namedGraph, select, queryS, true));
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(query);
		}

		return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

	}

	private Model cleanTenders(Model tender) {
		NodeIterator i = tender.listObjectsOfProperty(PCFappModel.pcf_document);
		while (i.hasNext()) {
			tender.removeAll(i.next().asResource(), null, null);
		}

		i = tender.listObjectsOfProperty(PCFappModel.pc_contact);
		while (i.hasNext()) {
			tender.removeAll(i.next().asResource(), null, null);
		}

		String[] toDelete = { "created", "modified", "document", "submitted", "awarded" };
		for (String property : toDelete) {
			tender.removeAll(null, ResourceFactory.createProperty(PCFappModel.pcf, property), null);
		}

		return tender;
	}

	/**
	 * Adds properties of finished project.
	 * 
	 * @param uc
	 * @param contractURL
	 */
	public boolean finalizeContract(UserContext uc, String contractURL, String publish, String actualEndDate, String satisfied,
			String currency, String currencyValue) {

		Model contract = getPrivateContract(contractURL, uc.getNamedGraph(), "none");

		Model awarded = getPrivateTendersForContract(uc.getNamedGraph(), contractURL, true);
		awarded = cleanTenders(awarded);

		Model normal;

		ByteArrayOutputStream baosA = new ByteArrayOutputStream();
		ByteArrayOutputStream baosN = new ByteArrayOutputStream();

		String publishing = "";

		try {
			switch (publish) {
				case "awarded":
					System.out.println("awarded");
					awarded.write(baosA, "N-TRIPLE");
					publishing = new String(baosA.toString("UTF-8"));
					break;

				case "all":
					System.out.println("all");
					normal = getPrivateTendersForContract(uc.getNamedGraph(), contractURL, false);
					normal = cleanTenders(normal);
					awarded.write(baosA, "N-TRIPLE");
					normal.write(baosN, "N-TRIPLE");
					publishing = new String(baosA.toString("UTF-8")) + " " + new String(baosN.toString("UTF-8"));
					break;

				default:
					System.out.println("default");
					publishing = "";
			}
		} catch (UnsupportedEncodingException unused) {
		}

		// private space

		String actualPriceURL = contractURL + "/actualPrice";

		UpdateRequest request;
		/* @formatter:off */
		request = UpdateFactory.create(
				config.getPreference("prefixes") +
				"DELETE DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"	<"+ contractURL +">		pcfapp:status			    pcfapp:Awarded " +
				"	} " +
				"} ;" +
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"	<"+ contractURL +">		pc:actualEndDate			\"" + actualEndDate + "\"^^xsd:date ; " +
				"							pcfapp:satisfiedField		\"" + satisfied + "\"^^xsd:boolean ; " +
				"							pcfapp:status				pcfapp:Completed ; " +
				"							pcfapp:completed			\"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime ; " +
				"							pc:actualPrice				<"+ actualPriceURL +"> . " +
				"	<"+ actualPriceURL +">	a							gr:UnitPriceSpecification " +
				";							gr:hasCurrency				\"" + currency + "\"" +
				";							gr:hasCurrencyValue			\"" + currencyValue + "\"^^xsd:float . " +
				"	} " +
				"}");
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(request);
		}

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
		// public space

		/* @formatter:off */
		request = UpdateFactory.create(
				config.getPreference("prefixes") +
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + config.getPreference("publicGraphName") +"> { " +
				"	<"+ contractURL +">		pc:actualEndDate			\"" + actualEndDate + "\"^^xsd:date ; " +				
				"							pc:actualPrice				<"+ actualPriceURL +"> . " +
				"	<"+ actualPriceURL +">	a							gr:UnitPriceSpecification " +
				";							gr:hasCurrency				\"" + currency + "\"" +
				";							gr:hasCurrencyValue			\"" + currencyValue + "\"^^xsd:float . " +
				publishing +			
				"	} " +
				"}");
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(request);
		}

		upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
		upr.execute();

		System.out.println(awarded.toString());

		String mail =
				model.getMailFromEntity(awarded.getProperty(null, PCFappModel.pc_supplier).getObject().asResource().getURI());
		String info =
				"Your tender/offer for event '"
						+ contract.getProperty(null, PCFappModel.dc_title).getObject().asLiteral().getString()
						+ "' has been marked as completed.";
		new Mailer(config.getPreference("infoMail"), mail, "Tender information", info).send();

		return true;
	}

	/**
	 * Publishes event to public data space.
	 * 
	 * @param uc
	 * @param contractURL
	 * @throws PCFappException 
	 */
	public boolean publishContract(UserContext uc, String contractURL) throws PCFappException {

		// TODO: prerobit na transakciu
		
		Model contract = getPrivateContract(contractURL, uc.getNamedGraph(), "none");
		Resource contractRes = contract.getResource(contractURL);		

		if (contractRes.getProperty(PCFappModel.pcf_status).getObject().asResource().getLocalName().compareTo("Prepared") != 0 ) {
			throw new PCFappException("Already published");			
		}
		
		if (!contractRes.hasProperty(PCFappModel.pcf_eventType)) {
			throw new PCFappException("Call for tenders is incomplete, please fill in required fields.");
		}

		sendInvitationsPublished(contractRes);
		
		if (contract.contains(ResourceFactory.createResource(contractURL),
				ResourceFactory.createProperty(PCFappModel.pcf, "confidentialPrice"),
				ResourceFactory.createTypedLiteral(true))) {
			NodeIterator it =
					contract.listObjectsOfProperty(ResourceFactory.createResource(contractURL),
							ResourceFactory.createProperty(PCFappModel.pc, "estimatedPrice"));
			contract.removeAll(ResourceFactory.createResource(contractURL),
					ResourceFactory.createProperty(PCFappModel.pc, "estimatedPrice"),
					null);
			if (it.hasNext()) {
				for (RDFNode priceURL : it.toSet()) {
					contract.removeAll(priceURL.asResource(), null, null);
				}
			}
		}
		
		NodeIterator i;
		
		// removes supplier business entity objects from graph		
		i = contract.listObjectsOfProperty(PCFappModel.pcf_invitedSupplier);
		while (i.hasNext()) {
			contract.removeAll(i.next().asResource(), null, null);
		}
		
		// removes document objects from graph		
		i = contract.listObjectsOfProperty(PCFappModel.pcf_document);
		while (i.hasNext()) {
			contract.removeAll(i.next().asResource(), null, null);
		}
		
		// removes unwanted pcfapp properties
		Property[] toDelete = { PCFappModel.pcf_projectID, PCFappModel.pcf_created, PCFappModel.pcf_modified,
				PCFappModel.pcf_eventType, PCFappModel.pcf_eventReference, PCFappModel.pcf_confidentialPrice,
				PCFappModel.pcf_document, PCFappModel.pcf_withdrawn, PCFappModel.pcf_status, PCFappModel.pcf_invitedSupplier};
		
		for (Property property : toDelete) {
			contract.removeAll(null, property, null);
		}

		// System.out.println("###################################################");
		// contract.write(System.out, "Turtle");

		contract.write(System.out, "N-TRIPLE");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		contract.write(baos, "N-TRIPLE");

		UpdateRequest request;
		try {
			/* @formatter:off */
			request = UpdateFactory.create(
					config.getPreference("prefixes") +
					"INSERT DATA " +
					"{ " +
					"	GRAPH <" + config.getPreference("publicGraphName") +"> { " +
						new String(baos.toString("UTF-8")) +
					"	} " +
					"}");
			/* @formatter:on */

			logger.debug(request.toString());

			UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
			upr.execute();
			
		} catch (UnsupportedEncodingException unused) {
		}

		String noticeURL = contractURL + "/notice/1";
		String publicationDate = "";
		try {
			DatatypeFactory df = DatatypeFactory.newInstance();
			XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(new GregorianCalendar());
			publicationDate = calendar.toXMLFormat();
			publicationDate = publicationDate.substring(0, publicationDate.indexOf('T'));
		} catch (DatatypeConfigurationException unused) {
		}

		/* @formatter:off */
		request = UpdateFactory.create(
				config.getPreference("prefixes") +
				"DELETE DATA " +
				"{" +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"		<" + contractURL + ">			pcfapp:status		pcfapp:Prepared " +				
				"	} " +
				"} ; " +
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"		<" + contractURL + ">			pcfapp:status			pcfapp:Published . " +
				"		<" + contractURL + ">			pcfapp:published		\"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime . " +
				"		<" + contractURL + ">			pc:notice				<" + noticeURL + "> ." +
				"		<" + noticeURL + ">			    a						pc:ContractNotice ; " +
				"										pc:publicationDate		\"" + publicationDate + "\"^^xsd:date" +
				"	} " +
				"}"); 
		/* @formatter:on */
		
		logger.debug(request.toString());

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

		/* @formatter:off */
		request = UpdateFactory.create(
				config.getPreference("prefixes") +				
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + config.getPreference("publicGraphName") +"> { " +
				"		<" + contractURL + ">			pc:notice				<" + noticeURL + "> ." +
				"		<" + noticeURL + ">			    a						pc:ContractNotice ; " +
				"										pc:publicationDate		\"" + publicationDate + "\"^^xsd:date" +
				"	} " +
				"}"); 
		/* @formatter:on */

		upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
		upr.execute();
		
		return true;
	}
	
	private void sendInvitationsPublished(Resource contractRes) {
		
		String contractTitle = contractRes.getProperty(PCFappModel.dc_title).getObject().asLiteral().getString();
		Resource buyer = contractRes.getProperty(PCFappModel.pc_contractingAuthority).getObject().asResource();
		String buyerName = buyer.getProperty(PCFappModel.gr_legalName).getObject().asLiteral().getString();
		
		StmtIterator iter = contractRes.listProperties(PCFappModel.pcf_invitedSupplier);
		
		if ( !iter.hasNext() ) return;
		
		String mail_list = "Contract ( "+ contractTitle +" - "+ contractRes.getURI() +" ) has been published by "+buyerName+". \n\n";		
		
		while( iter.hasNext() ) {
			Resource supplier = iter.next().getObject().asResource();			
			String supplierName = supplier.getProperty(PCFappModel.gr_legalName).getObject().asLiteral().getString();
			if ( supplier.hasProperty(PCFappModel.s_email) ) {
				String supplierEmail = supplier.getProperty(PCFappModel.s_email).getObject().asLiteral().getString();				
				String inv_id = UUID.randomUUID().toString();
				logger.debug(contractRes.getURI());
				Boolean sent = sendInvitationEmail(inv_id, supplierEmail, contractTitle, buyerName, contractRes.getURI());
				if ( sent ) {
					mail_list += " - "+supplierName + " , invitation sent to: " + supplierEmail+" \n";
				}	
				else
				{
					mail_list += " - "+supplierName + " , error while sending invitation to: " + supplierEmail+" \n";
				}
			}
			else
			{
				mail_list += " - "+supplierName+ " ( "+supplier.getURI()+" ) unknown email! \n";						
			}						
		}
		logger.debug(mail_list);
		logger.debug(config.getPreference("emailResolver"));
		logger.debug(config.getPreference("infoMail"));
		new Mailer(config.getPreference("infoMail"), config.getPreference("emailResolver"), "New published contract", mail_list).send();
	}
	
	private boolean sendInvitationEmail(String inv_id, String email, String contractName, String buyerName ,String contractURI) {
		
		String mail_content =
				buyerName + " invited you to participate in the bidding process for a public contract \"" + contractName
						+ "\" using TenderStats.\n\n"
						+ "Try out TenderStats Beta at http://tenderstats.xrg.cz/obtain.html?inv_id=" + inv_id
						+ "&email=" + email;
		System.out.println(mail_content);

		try {
			addInvitationEntry(inv_id, URLDecoder.decode(email, "UTF-8"), URLDecoder.decode(contractURI, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Unable to send email",e);			
			return false;
		}
		return new Mailer(config.getPreference("invitationEmail"), email, "New invitation", mail_content).send();
		
	}

	private void addInvitationEntry(String inv_id, String email, String contractURI) {

		Connection con;
		try {
			con = DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
							config.getRdbUsername(),
							config.getRdbPassword());

			String select = "INSERT INTO invitations (`id`,`email`,`contractURI`) VALUES (?,?,?)";
			PreparedStatement insertStatement = con.prepareStatement(select);

			insertStatement.setString(1, inv_id);
			insertStatement.setString(2, email);
			insertStatement.setString(3, contractURI);

			System.out.println(contractURI);
			
			insertStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void cancelContract(UserContext uc, String contractURI) {

		UpdateRequest request;
		/* @formatter:off */
		request = UpdateFactory.create(
				config.getPreference("prefixes") +
				"DELETE DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"	<"+ contractURI +">		pcfapp:status			pcfapp:Published " +
				"	} " +
				"} ; "+
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"	<"+ contractURI +">		pcfapp:status			pcfapp:Cancelled ." +
				"	<"+ contractURI +">		pcfapp:cancelled		\"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime " +
				"	} " +
				"}");
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(request);
		}

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

	}

	public void withdrawContract(UserContext uc, String contractURI) {
		UpdateRequest request;

		/* @formatter:off */
		request = UpdateFactory.create(
				config.getPreference("prefixes") +
				"DELETE DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"	<"+ contractURI +">		pcfapp:status			pcfapp:Awarded . " +				
				"	} " +
				"} ; "+
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"	<"+ contractURI +">		pcfapp:status			pcfapp:Withdrawn ." +
				"	<"+ contractURI +">		pcfapp:withdrawn		\"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime ." +				
				"	} " +
				"}");
		/* @formatter:on */

		if (debug) {
			System.out.println("###################################################");
			System.out.println(request);
		}

		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

	}

	public boolean openTenders(String contractURI, String namedGraph) {

		Model contract = getPrivateContract(contractURI, namedGraph, null);
		Resource contractRes = contract.getResource(contractURI);
		
		if ( contractRes.hasProperty(PCFappModel.pc_sealed) && contractRes.getProperty(PCFappModel.pc_sealed).getBoolean() )
		{
			if ( contractRes.hasProperty(PCFappModel.pc_tenderOpening) ) {
				return true;
			}
			
			UpdateRequest request;
			String now = PCFappUtils.currentXMLTime();
			/* @formatter:off */
			request = UpdateFactory.create(
					config.getPreference("prefixes") +
					"INSERT DATA " +
					"{ " +
					"	GRAPH <" + namedGraph +"> { " +					
					"		<"+ contractURI +">		pc:tenderOpeningDateTime		\"" + now + "\"^^xsd:dateTime ." +				
					"	} " +
					"}");
			/* @formatter:on */

			if (debug) {
				System.out.println("###################################################");
				System.out.println(request);
			}

			UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
			upr.execute();
			
			request = UpdateFactory.create(
					config.getPreference("prefixes") +
					"INSERT DATA " +
					"{ " +
					"	GRAPH <" + config.getPreference("publicGraphName") +"> { " +					
					"		<"+ contractURI +">		pc:tenderOpeningDateTime		\"" + now + "\"^^xsd:dateTime ." +				
					"	} " +
					"}");
			/* @formatter:on */

			if (debug) {
				System.out.println("###################################################");
				System.out.println(request);
			}

			upr = new UpdateProcessRemote(request, config.getSparqlPublicUpdate(), Context.emptyContext);
			upr.execute();
			
			return true;
			
		}
		return false;
		
	}

	public String createSearchContract(UserContext uc, String cpvString) {
				
		String contractURI = config.getPrefix("contract") + UUID.randomUUID().toString();		
		String cpvURI = config.getPrefix("cpv") + (cpvString + "-").substring(0, (cpvString + "-").indexOf('-'));
		String time = PCFappUtils.currentXMLTime();
		/* @formatter:off */
		
		 // TODO rewrite using JENA Model or SPARQL VALUES
		String requestString = 
				config.getPreference("prefixes") +
				"INSERT DATA " +
				"{ " +
				"	GRAPH <" + uc.getNamedGraph() +"> { " +
				"		<" + contractURI + ">	a	 						pc:Contract " +
				";								pc:contractingAuthority 	<"+ uc.getPreference("businessEntity") +"> " +
				";								pcfapp:status				pcfapp:Prepared " + 
				";								dc:title					\"Unnamed event\"@en " +
				";								pcfapp:created				\"" + time + "\"^^xsd:dateTime " +
				";								pcfapp:modified				\"" + time + "\"^^xsd:dateTime " +
				";								pc:mainObject				<" + cpvURI + "> " +
				"	}" +
				"}";
		logger.debug(requestString.toString()); 
		UpdateRequest request = UpdateFactory.create(requestString);
		/* @formatter:on */
		
		UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
		
		return contractURI;
		
	}

	public Model getPrivateContract(String contractURI, String namedGraph) {
		return getPrivateContract(contractURI, namedGraph,"none");		
	}	
	
}
