package cz.opendata.tenderstats.pcfapp;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import cz.opendata.tenderstats.Config;
import cz.opendata.tenderstats.Mustache;
import cz.opendata.tenderstats.Sparql;
import cz.opendata.tenderstats.UserContext;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author venca
 */
public class ExtendedDocument extends Document {

    private final boolean global;
    private final String docType;

    public ExtendedDocument(boolean global, String docType, Document document) {
        super(document.getFile(), document.getName(), document.getContentType(), document.getOwner());
        this.global = global;
        this.docType = docType;
    }

    public static List<ExtendedDocument> fetchAllByGraph(String graph) {
        Map<String, Object> sparqlParams = new HashMap<>();
        sparqlParams.put("graph-uri", graph);
        ResultSet execSelect = Sparql.privateQuery(Mustache.getInstance().getBySparqlPath("select_documents.mustache", sparqlParams)).execSelect();
        UserContext user = UserContext.fetchUserByNamedGraph(graph);
        LinkedList<ExtendedDocument> result = new LinkedList<>();
        while (execSelect.hasNext()) {
            ExtendedDocument document = fetchByUserAndQuerySolution(user, execSelect.next());
            if (document != null) {
                result.add(document);
            }
        }
        return result;
    }

    public static List<ExtendedDocument> fetchAllByGraphAndTender(String graph, String tender) {
        Map<String, Object> sparqlParams = new HashMap<>();
        sparqlParams.put("graph-uri", graph);
        sparqlParams.put("tender-uri", tender);
        ResultSet execSelect = Sparql.privateQuery(Mustache.getInstance().getBySparqlPath("select_documents_by_tender.mustache", sparqlParams)).execSelect();
        UserContext user = UserContext.fetchUserByNamedGraph(graph);
        LinkedList<ExtendedDocument> result = new LinkedList<>();
        while (execSelect.hasNext()) {
            ExtendedDocument document = fetchByUserAndQuerySolution(user, execSelect.next());
            if (document != null) {
                result.add(document);
            }
        }
        return result;
    }

    public static ExtendedDocument fetchByUserAndQuerySolution(UserContext user, QuerySolution qs) {
        if (user != null && qs != null && qs.varNames().hasNext()) {
            return new ExtendedDocument(
                    qs.getLiteral("isGlobal").getBoolean(),
                    qs.getResource("docType").getURI().replaceAll(".+/", ""),
                    new Document(
                            new File(Config.cc().getPreference("documentsDir"), qs.getLiteral("id").getString()),
                            qs.getLiteral("name").getString(),
                            qs.getLiteral("contentType").getString(),
                            user)
            );
        }
        return null;
    }

    public static ExtendedDocument fetchByIdAndGraph(String id, String graph) {
        Map<String, Object> sparqlParams = new HashMap<>();
        sparqlParams.put("graph-uri", graph);
        sparqlParams.put("document-uri", DOCUMENT_PREFIX + id);
        ResultSet execSelect = Sparql.privateQuery(Mustache.getInstance().getBySparqlPath("select_document.mustache", sparqlParams)).execSelect();
        UserContext user = UserContext.fetchUserByNamedGraph(graph);
        if (execSelect.hasNext()) {
            QuerySolution result = execSelect.next();
            return fetchByUserAndQuerySolution(user, result);
        }
        return null;
    }

    public boolean isGlobal() {
        return global;
    }

    public String getDocType() {
        return docType;
    }

}
