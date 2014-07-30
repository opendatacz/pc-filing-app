package cz.opendata.tenderstats.pcfapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import cz.opendata.tenderstats.ComponentConfiguration;
import cz.opendata.tenderstats.Config;
import cz.opendata.tenderstats.Mustache;
import cz.opendata.tenderstats.Sparql;
import cz.opendata.tenderstats.UserContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCFappModel implements Serializable {

    public class PCFappModelException extends Exception {

        private static final long serialVersionUID = 6450291265247840868L;

        public PCFappModelException(String message) {
            super(message);
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(PCFappModel.class);
    private static final long serialVersionUID = -3963894760247662458L;

    private final boolean debug = true;

    public static final String pc = Config.cc().getPrefix("pc");
    public static final String pccrit = Config.cc().getPrefix("criteria");
    public static final String pcf = Config.cc().getPrefix("pcfapp");
    public static final String pce = Config.cc().getPrefix("pceu");
    public static final String gr = Config.cc().getPrefix("gr");
    public static final String dc = Config.cc().getPrefix("dcterms");
    public static final String rdfs = Config.cc().getPrefix("rdfs");
    public static final String vc = Config.cc().getPrefix("vcard");
    public static final String rdfsns = Config.cc().getPrefix("rdf");
    public static final String s = Config.cc().getPrefix("schema");
    public static final String rdf = Config.cc().getPrefix("rdf");

    public static final Property dc_title = ResourceFactory.createProperty(dc, "title");
    public static final Property dc_description = ResourceFactory.createProperty(dc, "description");
    public static final Property pc_mainCPV = ResourceFactory.createProperty(pc, "mainObject");
    public static final Property pc_additionalCPV = ResourceFactory.createProperty(pc, "additionalObject");
    public static final Property pc_procedureType = ResourceFactory.createProperty(pc, "procedureType");

    public static final Property pc_deadline = ResourceFactory.createProperty(pc, "tenderDeadline");
    public static final Property pc_estimatedPrice = ResourceFactory.createProperty(pc, "estimatedPrice");
    public static final Property pc_actualPrice = ResourceFactory.createProperty(pc, "actualPrice");
    public static final Property pc_actualEndDate = ResourceFactory.createProperty(pc, "actualEndDate");
    public static final Property pc_offeredPrice = ResourceFactory.createProperty(pc, "offeredPrice");
    public static final Property pc_estimatedEndDate = ResourceFactory.createProperty(pc, "estimatedEndDate");
    public static final Property pc_startDate = ResourceFactory.createProperty(pc, "startDate");
    public static final Property pc_endDate = ResourceFactory.createProperty(pc, "endDate");
    public static final Property pc_location = ResourceFactory.createProperty(pc, "location");
    public static final Property pc_sealed = ResourceFactory.createProperty(pc, "tendersSealed");
    public static final Property pc_tenderOpening = ResourceFactory.createProperty(pc, "tenderOpeningDateTime");
    public static final Property pc_contact = ResourceFactory.createProperty(pc, "contact");
    public static final Property pc_contract = ResourceFactory.createProperty(pc, "contract");
    public static final Property pc_contractingAuthority = ResourceFactory.createProperty(pc, "contractingAuthority");
    public static final Property pc_awardCriteriaCombination = ResourceFactory.createProperty(pc, "awardCriteriaCombination");
    public static final Property pc_awardCriterion = ResourceFactory.createProperty(pc, "awardCriterion");
    public static final Property pc_weightedCriterion = ResourceFactory.createProperty(pc, "weightedCriterion");
    public static final Property pc_criterionWeight = ResourceFactory.createProperty(pc, "criterionWeight");
    public static final Property pc_supplier = ResourceFactory.createProperty(pc, "supplier");
    public static final Property pc_tender = ResourceFactory.createProperty(pc, "tender");
    public static final Property pc_awardedTender = ResourceFactory.createProperty(pc, "awardedTender");

    public static final Property gr_hasCurrencyValue = ResourceFactory.createProperty(gr, "hasCurrencyValue");
    public static final Property gr_hasCurrency = ResourceFactory.createProperty(gr, "hasCurrency");
    public static final Property gr_legalName = ResourceFactory.createProperty(gr, "legalName");

    public static final Property pcf_confidentialPrice = ResourceFactory.createProperty(pcf, "confidentialPrice");
    public static final Property pcf_created = ResourceFactory.createProperty(pcf, "created");
    public static final Property pcf_modified = ResourceFactory.createProperty(pcf, "modified");
    public static final Property pcf_projectID = ResourceFactory.createProperty(pcf, "projectID");
    public static final Property pcf_eventReference = ResourceFactory.createProperty(pcf, "eventReferenceField");
    public static final Property pcf_eventType = ResourceFactory.createProperty(pcf, "eventType");
    public static final Property pcf_fileGenTerms = ResourceFactory.createProperty(pcf, "documentGenTerms");
    public static final Property pcf_fileCallDoc = ResourceFactory.createProperty(pcf, "documentCallDoc");
    public static final Property pcf_document = ResourceFactory.createProperty(pcf, "document");
    public static final Property pcf_documentToken = ResourceFactory.createProperty(dc, "identifier");
    public static final Property pcf_documentType = ResourceFactory.createProperty(rdf, "type");
    public static final Property pcf_documentFileName = ResourceFactory.createProperty(s, "name");
    public static final Property pcf_documentGlobal = ResourceFactory.createProperty(pcf, "isGlobal");
    public static final Property pcf_submitted = ResourceFactory.createProperty(pcf, "submitted");
    public static final Property pcf_invitedSupplier = ResourceFactory.createProperty(pcf, "invitedSupplier");
    public static final Property pcf_withdrawn = ResourceFactory.createProperty(pcf, "withdrawn");
    public static final Property pcf_status = ResourceFactory.createProperty(pcf, "status");

    public static final Property pce_hasParentRegion = ResourceFactory.createProperty(pce, "hasParentRegion");

    public static final Property vc_fn = ResourceFactory.createProperty(vc, "fn");
    public static final Property vc_note = ResourceFactory.createProperty(vc, "note");
    public static final Property vc_email = ResourceFactory.createProperty(vc, "email");
    public static final Property vc_tel = ResourceFactory.createProperty(vc, "tel");

    public static final Property rdfs_label = ResourceFactory.createProperty(rdfs, "label");

    public static final Property rdfsns_type = ResourceFactory.createProperty(rdfsns, "type");
    public static final Property rdfsns_value = ResourceFactory.createProperty(rdfsns, "value");

    public static final Property s_email = ResourceFactory.createProperty(s, "email");

    protected ComponentConfiguration config;
    protected PCFappUtils utils;

    public PCFappModel(ComponentConfiguration config) {
        this.config = config;
        this.utils = new PCFappUtils(this.config);
    }

    /**
     * Returns private tender as JENA model
     *
     * @param documentToken
     * @param namedGraph
     * @return
     */
    public Document getDocument(String documentToken, String namedGraph) {
        return ExtendedDocument.fetchByIdAndGraph(documentToken, namedGraph);
    }

    /**
     * Returns private contract as JENA model
     *
     * @param supplierURI
     * @return
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
     * Returns private tender as JENA model
     *
     * @param tenderURI
     * @param namedGraph
     * @param awarded
     * @return
     */
    protected Model getTendersFromContract(String tenderURI, String namedGraph, boolean awarded) {
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
                + "WHERE "
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
                + "  } "
                + "VALUES ?tenderURI { <" + tenderURI + "> }");
        /* @formatter:on */
        Model tender = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

        // System.out.println(query.toString());
        // System.out.println("###################################################");
        // contract.write(System.out, "Turtle");
        return tender;
    }

    /**
     * Returns private tenders for contract as JENA model
     *
     * @param contractURI
     * @param namedGraph
     * @param awarded
     * @return
     */
    public Model getPrivateTendersForContract(String namedGraph, String contractURI, boolean awarded) {
        /* @formatter:off */
        Query query = QueryFactory.create(
                config.getPreference("prefixes")
                + "CONSTRUCT "
                + "  { ?contractURI " + ((awarded) ? "pc:awardedTender" : "pc:tender") + " ?tender . "
                + "	 ?tender ?p1 ?o1 . "
                + "	 ?price ?p2 ?o2 "
                + "} "
                + "FROM <" + namedGraph + "> "
                + "WHERE "
                + "{  ?contractURI " + ((awarded) ? "pc:awardedTender" : "pc:tender") + " ?tender . "
                + "	?tender ?p1 ?o1 . "
                + "	?tender pc:offeredPrice ?price . "
                + "	?price ?p2 ?o2 . "
                + "	?contractURI rdf:type pc:Contract ."
                + "} "
                + "VALUES ?contractURI { <" + contractURI + "> } ");
        /* @formatter:on */
        Model tenders = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execConstruct();

        // System.out.println(query.toString());
        // System.out.println("###################################################");
        // contract.write(System.out, "Turtle");
        return tenders;
    }

    /**
     * Returns private contract as JENA model
     *
     * @param contractURI
     * @return
     */
    public Model getPublicContract(String contractURI) {
        /* @formatter:off */
        Query query = QueryFactory.create(
                config.getPreference("prefixes")
                + "CONSTRUCT  "
                + "  { ?contractURI ?p1 ?o1 . "
                + "    ?o1 ?p2 ?o2 . "
                + "    ?o2 ?p3 ?o3 . "
                + "    ?o3 ?p4 ?o4 . "
                + "    ?o4 ?p5 ?o5 .} "
                + "FROM <" + config.getPreference("publicGraphName") + "> "
                + "WHERE "
                + "  { ?contractURI ?p1 ?o1 . "
                + "    ?contractURI a pc:Contract "
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
                + "VALUES ?contractURI { <" + contractURI + "> }");
        /* @formatter:on */
        Model contract = QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execConstruct();

        if (debug) {
            System.out.println("###################################################");
            contract.write(System.out, "Turtle");
        }

        return contract;
    }

    public ResultSet getPublicSupplierData(String entity) {
        /* @formatter:off */
        Query query = QueryFactory.create(config.getPreference("prefixes")
                + "SELECT DISTINCT ?contractURI ?title ?description ?cpv1URL ?cpvAdd ?currency ?price "
                + "WHERE "
                + "  { GRAPH <" + config.getPreference("publicGraphName") + "> "
                + "      {      "
                + "            ?contractURI rdf:type pc:Contract . "
                + "            ?contractURI dc:title ?title . "
                + "            ?contractURI pc:notice ?notice . "
                + "            ?notice pc:publicationDate ?publicationDate ."
                + "			 ?contractURI pc:awardedTender ?tender ."
                + "			 ?tender pc:supplier <" + entity + "> ."
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
                + "				GROUP BY ?contractURI "
                + "			 } "
                + "      } "
                + "  }");
        /* @formatter:n */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPublicQuery(), query).execSelect();
    }

    public List<ExtendedDocument> getUserDocuments(String namedGraph, boolean global) {
        List<ExtendedDocument> result = ExtendedDocument.fetchAllByGraph(namedGraph);
        for (Iterator<ExtendedDocument> it = result.iterator(); it.hasNext();) {
            ExtendedDocument extendedDocument = it.next();
            if (global && !extendedDocument.isGlobal()) {
                it.remove();
            }
        }
        return result;
    }

    public void unlinkDocument(UserContext uc, String contractURL, String token) {
        Document document = ExtendedDocument.fetchByIdAndGraph(token, uc.getNamedGraph());
        if (document != null) {
            UpdateRequest request;
            /* @formatter:off */
            if (deleteDocument(document) == 0) {
                return;
            }
            request = UpdateFactory.create(
                    config.getPreference("prefixes")
                    + "DELETE DATA"
                    + "{"
                    + "	GRAPH <" + uc.getNamedGraph() + "> { "
                    + "	<" + contractURL + ">	pcfapp:document		<" + document.getUri() + "> "
                    + "	} "
                    + "}");
            /* @formatter:on */
            UpdateProcessRemote upr = new UpdateProcessRemote(request, config.getSparqlPrivateUpdate(), Context.emptyContext);
            upr.execute();
        }
    }

    public int deleteDocument(Document document) {
        try {
            document.deleteContentFile();
            Map<String, Object> sparqlParams = new HashMap<>();
            sparqlParams.put("graph-uri", document.getOwner().getNamedGraph());
            sparqlParams.put("resource", document.getUri());
            Sparql.privateUpdate(Mustache.getInstance().getBySparqlPath("cascaded_delete.mustache", sparqlParams)).execute();
            return 1;
        } catch (IOException ex) {
            LogManager.getLogger("Documents").debug(ex, ex);
            return 0;
        }
    }

    public void addDocument(ExtendedDocument document) {
        Map<String, Object> sparqlParams = new HashMap<>();
        sparqlParams.put("graph-uri", document.getOwner().getNamedGraph());
        sparqlParams.put("document-uri", document.getUri());
        sparqlParams.put("id", document.getId());
        sparqlParams.put("name", document.getName());
        sparqlParams.put("doc-type", document.getDocType());
        sparqlParams.put("content-type", document.getContentType());
        sparqlParams.put("is-global", document.isGlobal());
        Sparql.privateUpdate(Mustache.getInstance().getBySparqlPath("create_document.mustache", sparqlParams)).execute();
    }

    public void addSupplierDocs(UserContext uc, HttpServletRequest httpRequest) {
        String fileToken;
        Document document;
        String[] docTypes = {"QualityCertificate", "CompanyProfile", "FinancialStatements"};
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
                    document = utils.processFileUpload(part, fileToken, uc);
                    if (document != null) {
                        addDocument(new ExtendedDocument(true, part.getName(), document));
                    }
                }
            }
        } catch (IOException | ServletException ex) {
            LogManager.getLogger("Documents").warn(ex, ex);
        }
    }

    public String getMailFromEntity(String entity) {
        throw new UnsupportedOperationException();
//        PreparedStatement pst;
//        try {
//            Connection con = connectDB();
//            pst = con.prepareStatement("SELECT * FROM user_preferences WHERE preference = 'businessEntity' AND value = ? ");
//            pst.setString(1, entity);
//            java.sql.ResultSet rs = pst.executeQuery();
//            if (rs.next()) {
//                return rs.getString("username");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return "";
    }

    public String getMailFromNS(String entity) {
        throw new UnsupportedOperationException();
//        PreparedStatement pst;
//        try {
//            Connection con = connectDB();
//            pst = con.prepareStatement("SELECT * FROM user_preferences WHERE preference = 'namedGraph' AND value = ? ");
//            pst.setString(1, entity);
//            java.sql.ResultSet rs = pst.executeQuery();
//            if (rs.next()) {
//                return rs.getString("username");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return "";
    }

    public ResultSet getBuyerActivityData(String namedGraph) {

        /* @formatter:off */
        Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
                config.getPreference("prefixes")
                + "SELECT ?subject ?subjectTitle ?object ?objectTitle ?entity ?entityName ?price ?currency ?date ?type "
                + "WHERE "
                + "{ "
                + "	GRAPH <" + namedGraph + "> "
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
                + "				?entity gr:legalName ?entityName "
                + "			} "
                + "		} "
                + "		UNION "
                + "		{ "
                + "			select ?subject ?subjectTitle ?object ?objectTitle ?entity ?entityName ?price ?currency ?date (\"tenderSubmitted\" as ?type) "
                + "			where "
                + "			{ "
                + "				?subject a pc:tender . "
                + "				?subject pc:supplier ?entity ."
                + "				?entity gr:legalName ?entityName . "
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
                + "				?entity gr:legalName ?entityName . "
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
                + "			where "
                + "			{ "
                + "				?subject a pc:tender . "
                + "				?subject pc:supplier ?entity ."
                + "				?entity gr:legalName ?entityName . "
                + "				?subject pc:offeredPrice ?offer ."
                + "				?offer gr:hasCurrency ?currency ."
                + "				?offer gr:hasCurrencyValue ?price ."
                + "				?subject pcfapp:rejected ?date . "
                + "				?subject pc:contract ?object . "
                + "				?object dc:title ?objectTitle "
                + "			} "
                + "		} "
                + "	} "
                + "} "
                + "ORDER BY DESC(?date) ");
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        return QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();
    }

    public JsonObject getBuyerStats(String namedGraph, boolean total) {

        /* @formatter:off */
        Query query = QueryFactory.create( // TODO rewrite using CONSTRUCT
                config.getPreference("prefixes")
                + "SELECT ?status (count(?a) as ?count) "
                + "WHERE "
                + "  { GRAPH " + ((total) ? " ?g " : " <" + namedGraph + "> ")
                + "      {  "
                + "			?a a 				pc:Contract ;"
                + "			   pcfapp:status 	?status "
                + "	  } "
                + "	}"
                + "GROUP BY ?status ");
        /* @formatter:on */

        if (debug) {
            System.out.println("###################################################");
            System.out.println(query);
        }

        ResultSet rs = QueryExecutionFactory.sparqlService(config.getSparqlPrivateQuery(), query).execSelect();

        JsonObject data = new JsonObject();

        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            if (qs.contains("status") && qs.contains("count")) {
                qs.get("status").asResource().getLocalName();
                qs.get("count").asLiteral().getInt();
                data.addProperty(qs.get("status").asResource().getLocalName(), qs.get("count").asLiteral().getInt());
            }
        }

        JsonObject ret = new JsonObject();
        ret.add("data", data);

        return ret;
    }

    public static JsonArray resultSetAsJson(ResultSet resultSet) {

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

}