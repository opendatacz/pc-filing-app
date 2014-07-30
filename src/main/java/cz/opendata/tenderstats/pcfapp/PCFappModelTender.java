package cz.opendata.tenderstats.pcfapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import cz.opendata.tenderstats.ComponentConfiguration;
import cz.opendata.tenderstats.Mailer;
import cz.opendata.tenderstats.UserContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

public class PCFappModelTender implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7453245992378782686L;

    private boolean debug = true;

    private ComponentConfiguration config;
    private PCFappModel model;
    private PCFappModelContract modelContract;

    public PCFappModelTender(ComponentConfiguration config) {
        model = new PCFappModel(config);
        modelContract = new PCFappModelContract(config);
        this.config = config;
    }

    public JsonObject getTenderAsJson(String tenderURL, String namedGraph) {

        JsonObject json = new JsonObject();

        Model tenderModel = getPublicTender(tenderURL);
        Resource tenderRes = tenderModel.getResource(tenderURL);

        JsonObject supplier = new JsonObject();
        supplier.addProperty("entity", tenderRes.getProperty(PCFappModel.pc_supplier).getObject().asResource().toString());
        supplier.addProperty("name",
                tenderRes.getProperty(PCFappModel.pc_supplier).getObject().asResource().getProperty(PCFappModel.gr_legalName)
                .getObject().asLiteral().getString());
        json.add("supplier", supplier);

        if (tenderRes.getProperty(PCFappModel.pc_contract).getObject().asResource()
                .getProperty(PCFappModel.pc_contractingAuthority) != null) {
            JsonObject buyer = new JsonObject();
            Resource authority
                    = tenderRes.getProperty(PCFappModel.pc_contract).getObject().asResource()
                    .getProperty(PCFappModel.pc_contractingAuthority).getObject().asResource();

            buyer.addProperty("entity", authority.getURI());
            buyer.addProperty("name", authority.getProperty(PCFappModel.dc_title).getObject().asLiteral().getString());
            json.add("buyer", buyer);
        }

        // desc
        if (tenderRes.hasProperty(PCFappModel.dc_description)) {
            json.addProperty("description", tenderRes.getProperty(PCFappModel.dc_description).getObject().asLiteral().getString());
        }

        // start
        if (tenderRes.hasProperty(PCFappModel.pc_startDate)) {
            json.addProperty("startDate", tenderRes.getProperty(PCFappModel.pc_startDate).getObject().asLiteral().getString());
        }

        // end
        if (tenderRes.hasProperty(PCFappModel.pc_endDate)) {
            json.addProperty("endDate", tenderRes.getProperty(PCFappModel.pc_endDate).getObject().asLiteral().getString());
        }

        // price
        if (tenderRes.hasProperty(PCFappModel.pc_offeredPrice)) {
            Resource price = tenderModel.getResource(tenderRes.getPropertyResourceValue(PCFappModel.pc_offeredPrice).toString());
            if (price.hasProperty(PCFappModel.gr_hasCurrency)) {
                json.addProperty("currency", price.getProperty(PCFappModel.gr_hasCurrency).getString());
            }
            if (price.hasProperty(PCFappModel.gr_hasCurrencyValue)) {
                json.addProperty("price", price.getProperty(PCFappModel.gr_hasCurrencyValue).getObject().asLiteral().getString());
            }
        }

        String token;
        String fileName;
        String docType = null;

        JsonArray docs = new JsonArray();
        StmtIterator i = tenderRes.listProperties(PCFappModel.pcf_document);
        while (i.hasNext()) {
            Statement st = i.next();
            StmtIterator listProperties = st.getObject().asResource().listProperties(PCFappModel.pcf_documentType);
            while (listProperties.hasNext()) {
                String typeUri = listProperties.next().getObject().asResource().getLocalName();
                if (!typeUri.equals("MediaObject")) {
                    docType = typeUri;
                }
            }
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

        // StmtIterator iter = tenderRes.listProperties();
        // while ( iter.hasNext() ) {
        // Statement s = iter.next();
        // System.out.println(s.getPredicate().toString());
        // }
        return json;
    }

    /**
     * Returns private tender as JENA model
     *
     * @param contractURI
     * @param namedGraph
     */
    public Model getPrivateTender(String tenderURI, String namedGraph) {
        /* @formatter:off */
        Query query = QueryFactory.create(
                config.getPreference("prefixes")
                + "CONSTRUCT  "
                + "  { ?tenderURI ?p1 ?o1 . "
                + "    ?o1 ?p2 ?o2 . "
                + "    ?o2 ?p3 ?o3 . "
                + "    ?o3 ?p4 ?o4 . "
                + "    ?o4 ?p5 ?o5 .} "
                + "FROM <" + namedGraph + "> "
                + "WHERE"
                + "  { ?tenderURI ?p1 ?o1 . "
                + "    ?tenderURI a pc:tender "
                + "    OPTIONAL "
                + "      { ?o1 ?p2 ?o2 "
                + "        OPTIONAL "
                + "          { ?o2 ?p3 ?o3 "
                + "            OPTIONAL "
                + "              { ?o3 ?p4 ?o4 "
                + "                OPTIONAL "
                + "                  { ?o4 ?p5 ?o5 } "
                + "              } "
                + "          } "
                + "      } "
                + "  }"
                + "VALUES ?tenderURI { <" + tenderURI + "> }");
        /* @formatter:on */
        Model tender = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

        System.out.println(query.toString());
        // System.out.println("###################################################");
        tender.write(System.out, "Turtle");

        return tender;
    }

    ;

	/**
	 * Returns private tender as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	public Model getPublicTender(String tenderURI) {
        /* @formatter:off */
        Query query = QueryFactory.create(
                config.getPreference("prefixes")
                + "CONSTRUCT  "
                + "  { ?tenderURI ?p1 ?o1 . "
                + "    ?o1 ?p2 ?o2 . "
                + "    ?o2 ?p3 ?o3 . "
                + "    ?o3 ?p4 ?o4 . "
                + "    ?o4 ?p5 ?o5 .} "
                + "WHERE"
                + "{	graph ?g "
                + "  { ?tenderURI ?p1 ?o1 . "
                + "    ?tenderURI a pc:tender "
                + "    OPTIONAL "
                + "      { ?o1 ?p2 ?o2 "
                + "        OPTIONAL "
                + "          { ?o2 ?p3 ?o3 "
                + "            OPTIONAL "
                + "              { ?o3 ?p4 ?o4 "
                + "                OPTIONAL "
                + "                  { ?o4 ?p5 ?o5 } "
                + "              } "
                + "          } "
                + "      } "
                + "  }"
                + "}"
                + "VALUES ?tenderURI { <" + tenderURI + "> }");
        /* @formatter:on */
        Model tender = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

        System.out.println(query.toString());
        // System.out.println("###################################################");
        tender.write(System.out, "Turtle");

        return tender;
    }

    ;

	/**
	 * Returns private contract as JENA model
	 * 
	 * @param contractURI
	 * @param namedGraph
	 */
	public Model getSupplier(String supplierURI) {
        /* @formatter:off */
        Query query = QueryFactory.create(
                config.getPreference("prefixes")
                + "CONSTRUCT  "
                + "  { ?contractURI ?p1 ?o1 . "
                + "    ?o1 ?p2 ?o2 . "
                + "    ?o2 ?p3 ?o3 . "
                + "    ?o3 ?p4 ?o4 . "
                + "    ?o4 ?p5 ?o5 .} "
                + "WHERE "
                + "{ GRAPH ?g "
                + "  { ?contractURI ?p1 ?o1 . "
                + "    ?contractURI a gr:BusinessEntity "
                + "    OPTIONAL "
                + "      { ?o1 ?p2 ?o2 "
                + "        OPTIONAL "
                + "          { ?o2 ?p3 ?o3 "
                + "            OPTIONAL "
                + "              { ?o3 ?p4 ?o4 "
                + "                OPTIONAL "
                + "                  { ?o4 ?p5 ?o5 } "
                + "              } "
                + "          } "
                + "      } "
                + "  } "
                + "}"
                + "VALUES ?contractURI { <" + supplierURI + "> }");
        /* @formatter:on */
        Model entity = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execConstruct();

        // System.out.println("###################################################");
        System.out.println(query.toString());
        entity.write(System.out, "Turtle");

        return entity;
    }

    /**
     * Deletes private tender with specified URL.
     *
     * @param uc
     * @param tenderURL
     * @throws ServletException
     */
    public void deletePrivateTender(UserContext uc, String tenderURL) throws ServletException {
        List<ExtendedDocument> documents = ExtendedDocument.fetchAllByGraphAndTender(uc.getNamedGraph(), tenderURL);
        for (ExtendedDocument extendedDocument : documents) {
            model.deleteDocument(extendedDocument);
        }
        /* @formatter:off */
        UpdateRequest request = UpdateFactory.create( // TODO This works for our URIs, but might not for others
                config.getPreference("prefixes")
                + "WITH <" + uc.getNamedGraph() + "> "
                + "DELETE "
                + "{"
                + "?s ?p ?o ."
                + "?priceO ?priceP ?priceS ."
                + "}"
                + "WHERE"
                + "{"
                + "   ?s pc:offeredPrice ?priceO ."
                + "   ?priceO ?priceP ?priceS ."
                + "   ?s ?p ?o ."
                + "   FILTER ( CONTAINS(str(?s), \"" + tenderURL + "\") )"
                + "}");
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(request);
        }

        UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
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

        Model contract = modelContract.getPrivateContract(contractURL, buyerURL, "none");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        contract.write(baos, "N-TRIPLE");

        // documents upload
        String documents = "";
        String fileToken;
        Document fileName;
        String documentObjectURI = config.getPrefix("contract") + "document/";

        if (certs != null && !certs.isEmpty()) {
            documents += " ; pcfapp:document <" + documentObjectURI + certs + "> ";
        }
        if (profile != null && !profile.isEmpty()) {
            documents += " ; pcfapp:document <" + documentObjectURI + profile + "> ";
        }
        if (fin != null && !fin.isEmpty()) {
            documents += " ; pcfapp:document <" + documentObjectURI + fin + "> ";
        }

        String[] docTypes = {"Offer", "TechSpecs", "PriceDelivery", "Requested"};
        List<String> docTypesList = Arrays.asList(docTypes);

        Collection<Part> parts;
        try {
            parts = httpRequest.getParts();

            Iterator<Part> i = parts.iterator();
            if (!i.hasNext()) {
                System.out.println(":(((");
            }
            while (i.hasNext()) {
                Part part = i.next();
                System.out.println(part.getName());

                if (docTypesList.contains(part.getName())) {
                    fileToken = UUID.randomUUID().toString();
                    fileName = model.utils.processFileUpload(part, fileToken, uc);
                    if (fileName != null) {
                        documents += " ; pcfapp:document	<" + fileName.getUri() + "> ";
                        model.addDocument(new ExtendedDocument(true, part.getName(), fileName));
                    }
                }
            }

        } catch (IllegalStateException | IOException | ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* @formatter:off */
        UpdateRequest request
                = UpdateFactory.create(
                        config.getPreference("prefixes")
                        + "INSERT DATA "
                        + "{ "
                        + "	GRAPH <" + uc.getNamedGraph() + ">"
                        + "	{ "
                        + "		<" + newTenderURL + ">	    a							pc:tender"
                        + ";									pc:offeredPrice				<" + offeredPriceURL + ">"
                        + ";									pc:contract					<" + contractURL + ">"
                        + ";									pcfapp:buyer					<" + buyerURL + "> "
                        + documents
                        + ";									pc:supplier					<" + uc.getPreference("businessEntity") + "> "
                        + ";									dc:description				\"" + description.replaceAll("(\r\n|[\r\n])+", " ") + "\"@en"
                        + ";									pc:startDate				\"" + startDate + "\"^^xsd:date"
                        + ";									pc:endDate			\"" + endDate + "\"^^xsd:date" + "		. "
                        + "		<" + offeredPriceURL + ">	a							gr:UnitPriceSpecification"
                        + ";									gr:hasCurrency				\"" + currency + "\""
                        + ";									gr:hasCurrencyValue			\"" + currencyValue + "\"^^xsd:float"
                        + "	} "
                        + "}");
        /* @formatter:on */
        System.out.println(request);
        UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
        upr.execute();

    }

    public void editPrivateTender(UserContext uc, String tenderURL, String description, String price, String currency,
            String startDate, String endDate, String certs, String profile, String fin, HttpServletRequest httpRequest) {

        // documents upload
        String documents = "";

        String fileToken;
        Document fileName;
        String documentObjectURI = config.getPrefix("contract") + "document/";

        if (certs != null && !certs.isEmpty()) {
            documents += " ; pcfapp:document <" + documentObjectURI + certs + "> ";
        }
        if (profile != null && !profile.isEmpty()) {
            documents += " ; pcfapp:document <" + documentObjectURI + profile + "> ";
        }
        if (fin != null && !fin.isEmpty()) {
            documents += " ; pcfapp:document <" + documentObjectURI + fin + "> ";
        }

        //String[] fileArrays = { "inputFileOffer", "inputFileTechSpecs", "inputFilePriceDelivery", "inputFileRequested" };
        String[] docTypes = {"Offer", "TechSpecs", "PriceDelivery", "Requested"};
        List<String> docTypesList = Arrays.asList(docTypes);

        Collection<Part> parts;
        try {
            parts = httpRequest.getParts();

            Iterator<Part> i = parts.iterator();
            if (!i.hasNext()) {
                System.out.println(":(((");
            }
            while (i.hasNext()) {
                Part part = i.next();
                System.out.println(part.getName());

                if (docTypesList.contains(part.getName())) {
                    fileToken = UUID.randomUUID().toString();
                    fileName = model.utils.processFileUpload(part, fileToken, uc);
                    if (fileName != null) {
                        documents += " ; pcfapp:document	<" + fileName.getUri() + "> ";
                        model.addDocument(new ExtendedDocument(true, part.getName(), fileName));
                    }
                }
            }

        } catch (IllegalStateException | IOException | ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//		for (int i = 0; i < fileArrays.length; i++) {
//			fileToken = UUID.randomUUID().toString();
//			fileName = model.utils.processFileUpload(httpRequest, fileArrays[i], uc.getUserName(), fileToken);
//			if (fileName != null && !fileName.isEmpty()) {
//				String documentURI = documentObjectURI + fileToken;
//				documents += " ; pcfapp:document	<" + documentURI + "> ";
//				model.addDocument(uc.getNamedGraph(), documentObjectURI + fileToken, fileToken, fileName, false, docTypes[i]);
//			}
//		}
        UpdateRequest request;
        /* @formatter:off */
        request = UpdateFactory.create(
                config.getPreference("prefixes")
                + "WITH <" + uc.getNamedGraph() + "> "
                + "DELETE "
                + "{"
                + "	<" + tenderURL + ">		dc:description			?desc "
                + ";							pc:startDate			?start "
                + ";							pc:endDate				?end "
                + ".	?priceURL				gr:hasCurrency			?currency "
                + ";							gr:hasCurrencyValue		?value "
                + "}"
                + "INSERT "
                + "{ "
                + "	<" + tenderURL + ">		dc:description				\"" + description.replaceAll("(\r\n|[\r\n])+", " ") + "\"@en"
                + ";							pc:startDate				\"" + startDate + "\"^^xsd:date"
                + ";							pc:endDate					\"" + endDate + "\"^^xsd:date"
                + documents
                + "	. "
                + "	?priceURL				gr:hasCurrency				\"" + currency + "\""
                + ";							gr:hasCurrencyValue			\"" + price + "\"^^xsd:float"
                + "}"
                + "WHERE"
                + "{"
                + "	OPTIONAL { <" + tenderURL + ">		dc:description			?desc } "
                + "	OPTIONAL { <" + tenderURL + ">		pc:startDate			?start } "
                + "	OPTIONAL { <" + tenderURL + ">		pc:endDate				?end } "
                + "	OPTIONAL { <" + tenderURL + ">		pc:offeredPrice			?priceURL }"
                + "	OPTIONAL { ?priceURL				gr:hasCurrency			?currency } "
                + "	OPTIONAL { ?priceURL				gr:hasCurrencyValue		?value }"
                + "}");
        /* @formatter:on */

        // System.out.println("###################################################");
        System.out.println(request);

        UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
        upr.execute();

    }

    private String TenderQuery(String namedGraph, String select, String supplierSelect, String buyerSelect) {

        if (select == null) {
            select = "";
        }
        if (supplierSelect == null) {
            supplierSelect = "";
        }
        if (buyerSelect == null) {
            buyerSelect = "";
        }

        return config.getPreference("prefixes")
                + "SELECT DISTINCT ?tenderURI ?buyerURI ?contractURI ?title ?description ?conf ?buyerEntity ?buyerName ?price ?currency ?cpv1URL ?cpvAdd ?modified ?endDate"
                + " ?deadline ?publicationDate ?place " + select + " WHERE " + "  { GRAPH <" + namedGraph + "> " + "       {  "
                + "			?tenderURI		a					pc:tender" + ".			?tenderURI		dc:description 		?description "
                + ".			?tenderURI		pc:contract			?contractURI " + ".			?tenderURI		pcfapp:buyer		?buyerURI "
                + ".			?tenderURI		pc:offeredPrice		?offeredPrice" + ".			?offeredPrice	gr:hasCurrency		?currency"
                + ".			?offeredPrice	gr:hasCurrencyValue ?price " + supplierSelect
                + "       } " + "		GRAPH ?buyerURI " + "		{" + "			?contractURI	a					pc:Contract"
                + ".			?contractURI	dc:title			?title " + ".			?contractURI	pc:contractingAuthority			?buyerEntity "
                + /*".			?buyerEntity	dc:title			?buyerName " +*/ ".			?contractURI 	pc:mainObject       ?cpv1URL "
                + ".			OPTIONAL { ?contractURI	pcfapp:confidentialPrice ?conf }" + "           OPTIONAL" + "			{"
                + "				?contractURI pcfapp:modified ?modified " + "			} " + "			OPTIONAL " + "			{ "
                + "         		?contractURI pc:notice ?notice . " + "	    		?notice pc:publicationDate ?publicationDate "
                + "			} " + "           OPTIONAL " + "	        { " + "				?contractURI pc:tenderDeadline ?deadline" + "			} "
                + buyerSelect + "			OPTIONAL " + "			{ " + "				{ "
                + "					SELECT ?contractURI (group_concat( distinct ?cpv) as ?cpvAdd) "
                + "					WHERE { GRAPH ?buyerURI { ?contractURI pc:additionalObject ?cpv } } " + "					GROUP BY ?contractURI "
                + "				}" + "			}" + "		}" + "  }";
    }

    public ResultSet getSupplierPreparedTenders(String namedGraph) {
        /* @formatter:off */
        String buyer
                = "			?contractURI	pcfapp:status		pcfapp:Published "
                + "			OPTIONAL {	?contractURI pc:tender ?tenderURI	} "
                + "			MINUS { ?contractURI pcfapp:withdrawn \"true\"^^xsd:boolean }"
                + "			MINUS { ?contractURI pc:tender ?tenderURI }"
                + "			MINUS { ?contractURI pc:awardedTender ?atender } "
                + "			MINUS { ?contractURI pc:rejectedTender ?tenderURI } "
                + "			MINUS { ?contractURI pc:withdrawnTender ?tenderURI } ";
        /* @formatter:on */
        String queryS = TenderQuery(namedGraph, null, null, buyer);
        Query query = QueryFactory.create(queryS);

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierSubmittedTenders(String namedGraph) {
        /* @formatter:off */
        String select = "?submitted";
        String buyer
                = ".		  	?contractURI	pc:tender			?tenderURI ."
                + "			?contractURI	pcfapp:status		pcfapp:Published . "
                + "			?tenderURI	pcfapp:submitted ?submitted "
                + "			FILTER NOT EXISTS { ?contractURI pc:awardedTender ?awarded } "
                + "			MINUS { ?contractURI pcfapp:withdrawn ?m } ";

        Query query = QueryFactory.create(TenderQuery(namedGraph, select, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierWithdrawnTenders(String namedGraph) {
        /* @formatter:off */
        String select = "?withdrawn";
        String buyer
                = ".		  	?contractURI	pc:withdrawnTender			?tenderURI "
                + ".			?tenderURI	 	pcfapp:withdrawn			?withdrawn ";
        Query query = QueryFactory.create(TenderQuery(namedGraph, select, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierTendersForWithdrawnCalls(String namedGraph) {
        /* @formatter:off */
        String buyer
                = ".		  	?contractURI	pc:tender			?tenderURI "
                + ".			?contractURI 	pcfapp:status		pcfapp:Withdrawn ";
        Query query = QueryFactory.create(TenderQuery(namedGraph, null, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierAwardedTenders(String namedGraph) {
        /* @formatter:off */
        String select = "?awarded";
        String buyer
                = ".			?contractURI	pc:awardedTender	?tenderURI ."
                + "			?contractURI	pcfapp:awarded		?awarded ."
                + "			?contractURI	pcfapp:status		pcfapp:Awarded ";

        Query query = QueryFactory.create(TenderQuery(namedGraph, select, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierRejectedTenders(String namedGraph) {
        /* @formatter:off */
        String select = "?rejected";
        String buyer
                = ".			?contractURI	pc:rejectedTender	?tenderURI ."
                + "			?tenderURI		pcfapp:rejected		?rejected ";

        Query query = QueryFactory.create(TenderQuery(namedGraph, select, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierNonAwardedTenders(String namedGraph) {
        /* @formatter:off */
        String buyer
                = ".		  	?contractURI	pc:tender			?tenderURI "
                + ".			?contractURI	pc:awardedTender	?tender "
                + ".			{ ?contractURI	pcfapp:status		pcfapp:Completed } "
                + "			UNION"
                + "			{ ?contractURI	pcfapp:status		pcfapp:Awarded }";
        Query query = QueryFactory.create(TenderQuery(namedGraph, null, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    /**
     * Creates copy of tender in private space of buyer.
     *
     * @param uc
     * @param contractURL
     */
    public boolean submitTender(UserContext uc, String tenderURL, String buyerURL, String contractURL) {

        Model tender = getPrivateTender(tenderURL, uc.getNamedGraph());

        String[] toDelete = {"buyer"};
        for (String property : toDelete) {
            tender.removeAll(null, ResourceFactory.createProperty(PCFappModel.pcf, property), null);
        }

        // System.out.println("###################################################");
        // contract.write(System.out, "Turtle");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tender.write(baos, "N-TRIPLE");

        String submittedDate = "";

        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(new GregorianCalendar());
            submittedDate = calendar.toXMLFormat();
        } catch (DatatypeConfigurationException unused) {
        }

        UpdateRequest request;
        try {
            /* @formatter:off */
            request = UpdateFactory.create(
                    config.getPreference("prefixes")
                    + "INSERT DATA "
                    + "{ "
                    + "	GRAPH <" + buyerURL + "> { "
                    + "	<" + contractURL + ">	pc:tender				<" + tenderURL + "> ."
                    + "	<" + tenderURL + ">	pcfapp:submitted 	\"" + submittedDate + "\"^^xsd:dateTime . "
                    + new String(baos.toString("UTF-8"))
                    + "	} "
                    + "}");
            /* @formatter:on */

            // System.out.println("###################################################");
            // System.out.println(request);
            UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
            upr.execute();
        } catch (UnsupportedEncodingException unused) {
        }

        Model contract = modelContract.getPrivateContract(contractURL, buyerURL, "none");

        String mail = model.getMailFromNS(buyerURL);
        String info
                = "You have recieved an offer/tender from " + uc.getUserName() + " for event '"
                + contract.getProperty(null, PCFappModel.dc_title).getObject().asLiteral().getString() + "'";
        new Mailer(config.getPreference("infoMail"), mail, "Tender information", info).send();

        return true;
    }

    /**
     * Sets tender as awarded
     *
     * @param uc
     * @param contractURL
     */
    public boolean awardTender(UserContext uc, String tenderURL, String contractURL) {

        UpdateRequest request;
        /* @formatter:off */
        request = UpdateFactory.create(
                config.getPreference("prefixes")
                + "DELETE DATA"
                + "{"
                + "	GRAPH <" + uc.getNamedGraph() + "> { "
                + "	<" + contractURL + ">	pc:tender	<" + tenderURL + "> ."
                + "	<" + contractURL + ">	pcfapp:status		pcfapp:Published "
                + "	} "
                + "} ; "
                + "INSERT DATA "
                + "{ "
                + "	GRAPH <" + uc.getNamedGraph() + "> { "
                + "	<" + contractURL + ">	pc:awardedTender	<" + tenderURL + "> . "
                + "	<" + contractURL + ">	pcfapp:status		pcfapp:Awarded . "
                + "	<" + contractURL + ">	pcfapp:awarded		\"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime . "
                + "	} "
                + "}");
        /* @formatter:on */

        // System.out.println("###################################################");
        // System.out.println(request);
        UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
        upr.execute();

        Model awarded = modelContract.getPrivateTendersForContract(uc.getNamedGraph(), contractURL, true);
        Model contract = modelContract.getPrivateContract(contractURL, uc.getNamedGraph(), "none");
        String title = contract.getProperty(null, PCFappModel.dc_title).getObject().asLiteral().getString();

        String mail
                = model.getMailFromEntity(awarded.getProperty(null, PCFappModel.pc_supplier).getObject().asResource().getURI());
        String info = "Your tender/offer for event '" + title + "' has been marked as awarded.";
        new Mailer(config.getPreference("infoMail"), mail, "Tender information", info).send();

        Model others = modelContract.getPrivateTendersForContract(uc.getNamedGraph(), contractURL, false);
        NodeIterator i = others.listObjectsOfProperty(PCFappModel.pc_supplier);
        while (i.hasNext()) {
            RDFNode node = i.next();
            node.asResource().getURI();
            info = "Your tender/offer for event '" + title + "' has been marked as non-awarded.";
            new Mailer(config.getPreference("infoMail"), mail, "Tender information", info).send();
        }

        return true;
    }

    /**
     * Sets tender as rejected
     *
     * @param uc
     * @param contractURL
     */
    public boolean rejectTender(UserContext uc, String tenderURL, String contractURL, String noteRejected) {

        UpdateRequest request;
        /* @formatter:off */
        request = UpdateFactory.create(
                config.getPreference("prefixes")
                + "DELETE DATA"
                + "{"
                + "	GRAPH <" + uc.getNamedGraph() + "> { "
                + "	<" + contractURL + ">	pc:tender		<" + tenderURL + "> "
                + "	} "
                + "} ; "
                + "INSERT DATA "
                + "{ "
                + "	GRAPH <" + uc.getNamedGraph() + "> { "
                + "	<" + contractURL + ">	pc:rejectedTender		<" + tenderURL + "> ."
                + "	<" + tenderURL + ">		pcfapp:rejected			\"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime ."
                + "   <" + tenderURL + ">		pcfapp:noteRejected   	\"" + noteRejected + "\"^^xsd:string "
                + "	} "
                + "}");
        /* @formatter:on */

        // System.out.println("###################################################");
        // System.out.println(request);
        UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
        upr.execute();

        Model rejected = getPrivateTender(tenderURL, uc.getNamedGraph());
        Model contract = modelContract.getPrivateContract(contractURL, uc.getNamedGraph(), "none");
        String title = contract.getProperty(null, PCFappModel.dc_title).getObject().asLiteral().getString();

        String mail
                = model.getMailFromEntity(rejected.getProperty(null, PCFappModel.pc_supplier).getObject().asResource().getURI());
        String info = "Your tender/offer for event '" + title + "' has been rejected.";
        new Mailer(config.getPreference("infoMail"), mail, "Tender information", info).send();

        return true;
    }

    /**
     * Deletes tender from buyers private space
     *
     * @param uc
     * @param contractURL
     */
    public void withdrawTender(UserContext uc, String tenderURL, String buyerURL, String contractURL) {

        /* @formatter:off */
        UpdateRequest request = UpdateFactory.create(
                config.getPreference("prefixes")
                + "DELETE DATA {"
                + "	GRAPH <" + buyerURL + "> {"
                + "		<" + contractURL + ">		pc:tender	<" + tenderURL + "> "
                + "	} "
                + "} ; "
                + "INSERT DATA {"
                + "	GRAPH <" + buyerURL + "> {"
                + "	<" + contractURL + ">		pc:withdrawnTender	<" + tenderURL + "> ."
                + "	<" + tenderURL + ">			pcfapp:withdrawn    \"" + PCFappUtils.currentXMLTime() + "\"^^xsd:dateTime . "
                + "	}"
                + "}");
        /* @formatter:on */

        // System.out.println("###################################################");
        // System.out.println(request);
        UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
        upr.execute();

        Model contract = modelContract.getPrivateContract(contractURL, buyerURL, "none");
        String title = contract.getProperty(null, PCFappModel.dc_title).getObject().asLiteral().getString();

        String mail = model.getMailFromNS(buyerURL);
        String info = "Offer for your call '" + title + "' by " + uc.getUserName() + " has been withdrawn.";
        new Mailer(config.getPreference("infoMail"), mail, "Tender information", info).send();

    }

    public ResultSet getSupplierCompletedTenders(String namedGraph) {

        /* @formatter:off */
        Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
                config.getPreference("prefixes")
                + "SELECT DISTINCT ?tenderURI ?buyerURI ?contractURI ?title ?description ?price ?currency ?cpv1URL ?cpvAdd ?modified ?endDate ?completed "
                + " ?deadline ?publicationDate ?place ?buyerName ?buyerEntity "
                + "WHERE "
                + "  { GRAPH <" + namedGraph + "> "
                + "      {  "
                + "			?tenderURI		a					pc:tender"
                + ".			?tenderURI		dc:description 		?description "
                + ".			?tenderURI		pc:contract			?contractURI "
                + ".			?tenderURI		pcfapp:buyer		?buyerURI "
                + "      } "
                + "	  GRAPH ?buyerURI "
                + "		{"
                + "			?contractURI	a					pc:Contract"
                + ".			?contractURI	dc:title			?title "
                + ".			?contractURI	pc:awardedTender	?tenderURI "
                + ".			?contractURI	pc:actualEndDate	?endDate "
                + ".			?contractURI	pc:contractingAuthority	?buyerEntity "
                + ".			?buyerEntity	dc:title			?buyerName "
                + ".			?contractURI	pcfapp:completed	?completed"
                + ".			?contractURI	pc:actualPrice		?priceURI "
                + ".			?priceURI		gr:hasCurrency		?currency"
                + ".			?priceURI		gr:hasCurrencyValue ?price"
                + ".			?contractURI 		pc:mainObject       ?cpv1URL "
                + "			OPTIONAL {"
                + "			}"
                + "			OPTIONAL {"
                + "				{ "
                + "					SELECT ?contractURI (group_concat( distinct ?cpv) as ?cpvAdd) "
                + "					WHERE { GRAPH ?buyerURI { ?contractURI pc:additionalObject ?cpv } } "
                + "					GROUP BY ?contractURI "
                + "				}"
                + "			}"
                + "		}"
                + "  }");
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();
    }

    public ResultSet getSubmittedTenders(String namedGraph, String contractURI) {

        /* @formatter:off */
        Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
                config.getPreference("prefixes")
                + "SELECT DISTINCT ?contractURI ?tenderURL ?grSupplier ?currency ?price ?supplierName ?submitted "
                + "WHERE "
                + "  { GRAPH <" + namedGraph + "> "
                + "      {  "
                + "           "
                + "			?contractURI		rdf:type 				pc:Contract ."
                + "			?contractURI		pc:tender 				?tenderURL ."
                + "			?tenderURL			pc:supplier 			?grSupplier ."
                + "			?tenderURL			pc:offeredPrice 		?offeredPriceURL ."
                + "			?offeredPriceURL	gr:hasCurrency			?currency ."
                + "			?offeredPriceURL	gr:hasCurrencyValue		?price ."
                + "			OPTIONAL { GRAPH ?g { ?grSupplier a	gr:BusinessEntity ."
                + "						?grSupplier dc:title ?supplierName } }"
                + "			OPTIONAL { ?tenderURL  pcfapp:submitted ?submitted }"
                + "			MINUS { ?contractURI pc:rejectedTender ?tenderURL } "
                + "			"
                + "         "
                + "      } "
                + "  } "
                + "VALUES ?contractURI { <" + contractURI + "> }");
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();
    }

    public ResultSet getSupplierTendersForCancelled(String namedGraph) {

        /* @formatter:off */
        String select = "?cancelled";
        String buyer
                = ".			?contractURI	pc:tender			?tenderURI ."
                + "			?contractURI	pcfapp:cancelled	?cancelled ."
                + "			?contractURI	pcfapp:status		pcfapp:Cancelled ";

        Query query = QueryFactory.create(TenderQuery(namedGraph, select, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

    }

    public ResultSet getSupplierTendersForWithdrawn(String namedGraph) {
        /* @formatter:off */
        String select = "?withdrawn";
        String buyer
                = ".			?contractURI	pc:awardedTender	?tenderURI ."
                + "			?contractURI	pcfapp:withdrawn	?withdrawn ."
                + "			?contractURI	pcfapp:status		pcfapp:Withdrawn ";

        Query query = QueryFactory.create(TenderQuery(namedGraph, select, null, buyer));
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();
    }

}
