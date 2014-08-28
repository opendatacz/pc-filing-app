package cz.opendata.tenderstats.pcfapp;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

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
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;

import cz.opendata.tenderstats.ComponentConfiguration;
import cz.opendata.tenderstats.UserContext;
import cz.opendata.tenderstats.admin.ClassHierarchy;

public class PCFappModelOntology implements Serializable {

	private static final long serialVersionUID = 4317705214419986680L;

	public static final String pc = "http://purl.org/procurement/public-contracts#";
	public static final String pcf = "http://purl.org/procurement/pcfilingapp#";
	public static final String gr = "http://purl.org/goodrelations/v1#";
	public static final String dc = "http://purl.org/dc/terms/";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String rdfsns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	// NEW
	public static final String fs = "http://linked.opendata.cz/ontology/form-specification#";

	public static final Property pc_mainCPV = ResourceFactory.createProperty(
			pc, "mainObject");
	public static final Property pc_additionalCPV = ResourceFactory
			.createProperty(pc, "additionalObject");

	public static final Property rdfsns_type = ResourceFactory.createProperty(
			rdfsns, "type");

	private ComponentConfiguration config;

	public PCFappModelOntology(ComponentConfiguration config) {
		this.config = config;
	}

	/**
	 * Adds data from filled form as specification
	 * 
	 */
	public String addSpecification(UserContext uc, String contractURL,
			String cpv, String root, String ontologyURI) throws IOException {

		String offeringURI = contractURL + "/Offering/" + cpv + "/"
				+ UUID.randomUUID().toString();

		//following commented lines seems redundant and only cause permissions trouble
		//File file = new File("temp.xml");
		//file.createNewFile();
		//FileWriter fstream = new FileWriter("temp.xml");
		//BufferedWriter out = new BufferedWriter(fstream);
		
		root = root.replace("rdf:rdf", "rdf:RDF");
		root = root.replace("base:Offering", offeringURI + "/");
		root = root.replace("base:", offeringURI + "/");

		StringBuilder rdf = new StringBuilder(root);
		// find all occurrences forward
		for (int i = -1; (i = rdf.indexOf("*", i + 1)) != -1;) {
			rdf.replace(i + 1, i + 2, rdf.substring(i + 1, i + 2).toUpperCase());
			rdf.replace(i, i + 1, "");
		}
		//out.write(rdf.toString());
		//out.close();

		Model model = ModelFactory.createDefaultModel();

		//InputStream in = FileManager.get().open("temp.xml");
		//if (in == null) {
		//	throw new IllegalArgumentException("File: " + "temp.xml"
		//			+ " not found");
		//}

		// read the RDF/XML file
		// --changed to just reading the string as inputstream
		InputStream in = new ByteArrayInputStream(rdf.toString().getBytes());
		model.read(in, null);

		StringWriter sw = new StringWriter();
		model.write(sw, "Turtle");

		String temp = sw.getBuffer().toString();
		temp = temp
				.replace(
						"@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .",
						"");
		temp = temp.replace(
				"@prefix gr:      <http://purl.org/goodrelations/v1#> .", "");
		temp = temp.replace("@prefix co:      <" + ontologyURI + "#> .", "");

		System.out.println(temp);
		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "PREFIX co: <"
				+ ontologyURI
				+ "#>   "
				+ "INSERT DATA "
				+ "{ "
				+ "	GRAPH <"
				+ uc.getNamedGraph()
				+ "> {"
				+ "		<"
				+ contractURL
				+ ">	pc:item  <" + offeringURI + "> . " + temp + "} " + "}");

		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();

		return request.toString();
	}

	/**
	 * Returns ontologies as ResultSet
	 * 
	 */
	public ResultSet getOntologies(String namedGraph) {
		String q = config.getPreference("prefixes")
				+ "SELECT ?ontologyURI ?title ?description ?modified "
				+ "WHERE "
				+ "  { GRAPH <"
				+ namedGraph
				+ "> "
				+ "      {      "
				+ "          { "
				+ "            ?ontologyURI rdf:type owl:Ontology . "
				+ "            ?ontologyURI dcterms:title ?title . "
				+ "            ?ontologyURI pcfapp:modified ?modified . "
				+ "			 OPTIONAL "
				+ "	 	       { ?ontologyURI dcterms:description ?description } "
				+ "          } " + "      } " + "  }"
				+ "ORDER BY DESC(?modified)";
		Query query = QueryFactory.create(q);
		
		System.out.println(query);
		return QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();
	}

	/**
	 * Returns forms as ResultSet
	 * 
	 */
	public ResultSet getForms(String namedGraph) {
		String q = config.getPreference("prefixes")
				+ "SELECT DISTINCT ?formClass ?ontologyTitle ?cpv ?modified ?cpvAdd "
				+ "WHERE "
				+ "  { GRAPH <"
				+ namedGraph
				+ "> "
				+ "      {      "
				+ "            ?formClass a owl:Class . "
				+ "            ?formClass rdfs:isDefinedBy ?ontologyURI . "
				+ "			 ?ontologyURI dcterms:title ?ontologyTitle . "
				+ "            ?formClass fs:form ?form ."
				+ "            ?formClass pc:mainObject ?cpv . "
				+ "            ?formClass pcfapp:modified ?modified . "
				+ "            OPTIONAL {"
				+ "            SELECT ?formClass (group_concat( distinct ?cpvTemp) as ?cpvAdd)"
				+ "            	WHERE {?formClass pc:additionalObject ?cpvTemp .} "
				+ "               GROUP BY ?formClass }" + "      } " + "  }"
				+ "ORDER BY DESC(?modified)";
		Query query = QueryFactory.create(q);
		return QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();
	}

	/**
	 * Returns forms as JsonArray based on ontology
	 * 
	 */
	public JsonArray getFormsByOntologyAsJson(String ontologyURI,
			String namedGraph) {
		String q = config.getPreference("prefixes")
				+ "SELECT DISTINCT ?formClass " + "WHERE " + "  { GRAPH <"
				+ namedGraph + "> " + "      {      "
				+ "            ?formClass a owl:Class . "
				+ "            ?formClass rdfs:isDefinedBy ?ontologyURI . "
				+ "      } " + "" + " FILTER (?ontologyURI = <" + ontologyURI
				+ ">) }" + "ORDER BY DESC(?modified)";
		Query query = QueryFactory.create(q);

		return resultSetAsJson(QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect());
	}

	/**
	 * Stores information about ontology
	 * 
	 */
	public void addOntology(UserContext uc, HttpServletRequest httpRequest) {
		String ontologyURI = httpRequest.getParameter("ontoURI");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat2 = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();

		Model model = ModelFactory.createDefaultModel();

		Resource node = model.createResource(ontologyURI).addProperty(
				model.createProperty(rdfsns + "type"), OWL.Ontology);
		model.add(node, model.createProperty(dc + "title"),
				httpRequest.getParameter("ontoName"), XSDDatatype.XSDstring);
		if (!httpRequest.getParameter("ontoDescription").isEmpty())
			model.add(
					node,
					model.createProperty(dc + "description"),
					httpRequest.getParameter("ontoDescription").replaceAll(
							"(\r\n|[\r\n])+", " "), XSDDatatype.XSDstring);
		model.add(node, model.createProperty(pcf + "modified"),
				dateFormat1.format(date) + "T" + dateFormat2.format(date),
				XSDDatatype.XSDdateTime);

		StringWriter sw = new StringWriter();
		model.write(sw, "Turtle");

		String temp = sw.getBuffer().toString();

		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "INSERT DATA "
				+ "{ "
				+ "	GRAPH <"
				+ uc.getNamedGraph() + "> { " + temp + "	} " + "}");

		System.out.println(request);
		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Deletes, and stores new data about edited ontology, returns its URI
	 * 
	 */
	public String editOntology(UserContext uc, HttpServletRequest httpRequest) {
		String ontologyURI = httpRequest.getParameter("oldOntoURI");

		this.deleteOntology(uc, ontologyURI);
		this.addOntology(uc, httpRequest);
		return ontologyURI;
	}

	/**
	 * Stores product class and its form
	 * 
	 */
	public void addClass(UserContext uc, HttpServletRequest httpRequest) {
		String ontologyURI = httpRequest.getParameter("ontologyURI");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat2 = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();
		String classURI = "";
		String form = httpRequest.getParameter("form");
		String[] cpvs = httpRequest.getParameterValues("cpvValues[]");
		classURI = httpRequest.getParameter("selClass").substring(
					httpRequest.getParameter("selClass").indexOf("http"));
		String cpvMainURI = config.getPreference("cpvURL")
				+ (cpvs[0] + "-").substring(0, (cpvs[0] + "-").indexOf('-'));

		String[] opValues = httpRequest.getParameterValues("opValues[]");
		String[] dpValues = httpRequest.getParameterValues("dpValues[]");

		Model model = ModelFactory.createDefaultModel();
		OntModel m = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_RDFS_INF, null);
		m.read(ontologyURI);
		ClassHierarchy ch = new ClassHierarchy();
		ObjectProperty[] objectProperties = ch.getObjectPropertiesDefined(m,
				opValues);
		DatatypeProperty[] datatypeProperties = null;
		if (dpValues != null)
			datatypeProperties = ch.getDatatypePropertiesDefined(m, dpValues);

		Resource ontologyNode = model.createResource(ontologyURI);
		Resource classNode = model.createResource(classURI).addProperty(
				model.createProperty(rdfsns_type.toString()), OWL.Class);

		model.add(classNode, model.createProperty(rdfs + "isDefinedBy"),
				ontologyNode);
		model.add(classNode, model.createProperty(fs + "form"), form,
				XSDDatatype.XSDstring);
		model.add(classNode, model.createProperty(pc_mainCPV.toString()),
				model.createResource(cpvMainURI));
		for (int i = 1; i < cpvs.length; i++) {
			model.add(
					classNode,
					model.createProperty(pc_additionalCPV.toString()),
					model.createResource(config.getPreference("cpvURL")
							+ (cpvs[i] + "-").substring(0,
									(cpvs[i] + "-").indexOf('-'))));
		}
		model.add(classNode, model.createProperty(pcf + "modified"),
				dateFormat1.format(date) + "T" + dateFormat2.format(date),
				XSDDatatype.XSDdateTime);

		Resource propertyNode = null;
		if (opValues != null)
			for (int i = 0; i < opValues.length; i++) {
				propertyNode = model.createResource(objectProperties[i]
						.getURI());
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "isDefinedBy"), ontologyNode);
				 */
				model.add(propertyNode, model.createProperty(rdfs + "domain"),
						classNode);
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "subPropertyOf"),
				 * model.createResource(objectProperties[i].getRange
				 * ().getURI()));
				 */
				model.add(propertyNode,
						model.createProperty(rdfsns_type.toString()),
						OWL.ObjectProperty);
			}

		propertyNode = null;
		if (dpValues != null)
			for (int i = 0; i < dpValues.length; i++) {
				propertyNode = model.createResource(datatypeProperties[i]
						.getURI());
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "isDefinedBy"), ontologyNode);
				 */
				model.add(propertyNode, model.createProperty(rdfs + "domain"),
						classNode);
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "subPropertyOf"),
				 * model.createResource(datatypeProperties[i].getRange
				 * ().getURI()));
				 */
				model.add(propertyNode,
						model.createProperty(rdfsns_type.toString()),
						OWL.DatatypeProperty);
			}

		StringWriter sw = new StringWriter();
		model.write(sw, "Turtle");

		String temp = sw.getBuffer().toString();

		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "INSERT DATA "
				+ "{ "
				+ "	GRAPH <"
				+ uc.getNamedGraph() + "> { " + temp + "	} " + "}");
		System.out.println(request);
		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}
	
	/**
	 * Stores product class and copied form
	 * 
	 */
	public void addClassCopy(UserContext uc, HttpServletRequest httpRequest) {
		String ontologyURI = httpRequest.getParameter("ontologyURI");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat2 = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();		 
		String form = httpRequest.getParameter("form");
		String[] cpvs = httpRequest.getParameterValues("cpvValues[]");
		String classURI = httpRequest.getParameter("selClass").substring(
					httpRequest.getParameter("selClass").indexOf("http"));
		String classCopyURI = httpRequest.getParameter("selClassCopy").substring(
				httpRequest.getParameter("selClassCopy").indexOf("http"));
		String cpvMainURI = config.getPreference("cpvURL")
				+ (cpvs[0] + "-").substring(0, (cpvs[0] + "-").indexOf('-'));

		JsonArray propertiesToCopy = this.getClassProperties(classCopyURI, uc.getNamedGraph());
		
		String[] values = new String[propertiesToCopy.size()];
		for(int i = 0; i<propertiesToCopy.size(); i++)
			values[i] = propertiesToCopy.get(i).getAsJsonObject().get("property").getAsString();

		Model model = ModelFactory.createDefaultModel();
		OntModel m = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_RDFS_INF, null);
		m.read(ontologyURI);
		ClassHierarchy ch = new ClassHierarchy();
		ObjectProperty[] objectProperties = ch.getObjectPropertiesDefined(m,
				values);
		
		DatatypeProperty[] datatypeProperties = null;
		if (values != null)
			datatypeProperties = ch.getDatatypePropertiesDefined(m, values);

		Resource ontologyNode = model.createResource(ontologyURI);
		Resource classNode = model.createResource(classURI).addProperty(
				model.createProperty(rdfsns_type.toString()), OWL.Class);

		model.add(classNode, model.createProperty(rdfs + "isDefinedBy"),
				ontologyNode);
		model.add(classNode, model.createProperty(fs + "form"), form,
				XSDDatatype.XSDstring);
		model.add(classNode, model.createProperty(pc_mainCPV.toString()),
				model.createResource(cpvMainURI));
		for (int i = 1; i < cpvs.length; i++) {
			model.add(
					classNode,
					model.createProperty(pc_additionalCPV.toString()),
					model.createResource(config.getPreference("cpvURL")
							+ (cpvs[i] + "-").substring(0,
									(cpvs[i] + "-").indexOf('-'))));
		}
		model.add(classNode, model.createProperty(pcf + "modified"),
				dateFormat1.format(date) + "T" + dateFormat2.format(date),
				XSDDatatype.XSDdateTime);

		Resource propertyNode = null;
		if (objectProperties != null)
			for (int i = 0; i < objectProperties.length; i++) {
				if(objectProperties[i]!=null) {
					propertyNode = model.createResource(objectProperties[i]
						.getURI());
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "isDefinedBy"), ontologyNode);
				 */
				model.add(propertyNode, model.createProperty(rdfs + "domain"),
						classNode);
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "subPropertyOf"),
				 * model.createResource(objectProperties[i].getRange
				 * ().getURI()));
				 */
				model.add(propertyNode,
						model.createProperty(rdfsns_type.toString()),
						OWL.ObjectProperty);
				}
			}

		propertyNode = null;
		if (datatypeProperties != null)
			for (int i = 0; i < datatypeProperties.length; i++) {
				if(datatypeProperties[i]!=null) {
					propertyNode = model.createResource(datatypeProperties[i]
						.getURI());
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "isDefinedBy"), ontologyNode);
				 */
				model.add(propertyNode, model.createProperty(rdfs + "domain"),
						classNode);
				/*
				 * model.add(propertyNode, model.createProperty(rdfs +
				 * "subPropertyOf"),
				 * model.createResource(datatypeProperties[i].getRange
				 * ().getURI()));
				 */
				model.add(propertyNode,
						model.createProperty(rdfsns_type.toString()),
						OWL.DatatypeProperty);
				}
			}

		StringWriter sw = new StringWriter();
		model.write(sw, "Turtle");

		String temp = sw.getBuffer().toString();

		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "INSERT DATA "
				+ "{ "
				+ "	GRAPH <"
				+ uc.getNamedGraph() + "> { " + temp + "	} " + "}");
		System.out.println(request);
		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Deletes ontology with specified URI.
	 * 
	 */
	public void deleteOntology(UserContext uc, String ontologyURL) {

		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "WITH <"
				+ uc.getNamedGraph()
				+ "> "
				+ "DELETE "
				+ "{ ?s ?p ?o }"
				+ "WHERE"
				+ "{"
				+ "   ?s ?p ?o ."
				+ "   FILTER ( ?s = <"
				+ ontologyURL
				+ "> )"
				+ "}");

		System.out.println(request);
		
		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Deletes class with specified URI.
	 * 
	 */
	public void deleteClass(UserContext uc, String formClass) {

		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "WITH <"
				+ uc.getNamedGraph()
				+ "> "
				+ "DELETE "
				+ "{ ?s ?p ?o }"
				+ "WHERE"
				+ "{"
				+ "   ?s ?p ?o ."
				+ "   FILTER ( ?s = <"
				+ formClass
				+ "> )"
				+ "}");

		System.out.println(request);
		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Deletes specification with specified URI.
	 * 
	 */
	public void deleteSpecification(UserContext uc, String specURI) {

		UpdateRequest request = UpdateFactory.create(config
				.getPreference("prefixes")
				+ "WITH <"
				+ uc.getNamedGraph()
				+ "> "
				+ "DELETE "
				+ "{ ?s pc:item ?o . }"
				+ "WHERE"
				+ "{"
				+ "   ?s pc:item ?o ."
				+ "   FILTER ( ?o = <"
				+ specURI
				+ "> )"
				+ "}");

		UpdateProcessRemote upr = new UpdateProcessRemote(request,
				config.getSparqlPrivateUpdate(), Context.emptyContext);
		upr.execute();
	}

	/**
	 * Returns ontology as JSON object
	 * 
	 */
	public JsonObject getOntologyAsJson(String ontologyURL, String namedGraph) {

		Model ontology = getOntology(ontologyURL, namedGraph);
		Resource ontologyRes = ontology.getResource(ontologyURL);

		JsonObject json = new JsonObject();

		// title
		if (ontologyRes.hasProperty(PCFappModel.dc_title))
			json.addProperty("title",
					ontologyRes.getProperty(PCFappModel.dc_title).getObject()
							.asLiteral().getString());
		// desc
		if (ontologyRes.hasProperty(PCFappModel.dc_description))
			json.addProperty("description",
					ontologyRes.getProperty(PCFappModel.dc_description)
							.getObject().asLiteral().getString());

		// start - end date
		if (ontologyRes.hasProperty(PCFappModel.pcf_modified))
			json.addProperty("modified",
					ontologyRes.getProperty(PCFappModel.pcf_modified)
							.getString());

		return json;
	}

	/**
	 * Returns ontology as JENA model
	 * 
	 */
	public Model getOntology(String ontologyURL, String namedGraph) {

		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?ontologyURI ?p1 ?o1 .} " + "FROM <"
				+ namedGraph + "> " + "WHERE "
				+ "  { ?ontologyURI ?p1 ?o1 . } " + "VALUES ?ontologyURI { <"
				+ ontologyURL + "> }");

		return QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();
	};

	/**
	 * Returns form as JSON object
	 * 
	 */
	public JsonObject getFormAsJson(String formClass, String namedGraph) {

		Model classModel = getClass(formClass, namedGraph);

		Resource classRes = classModel.getResource(formClass.toString());

		JsonObject json = new JsonObject();

		// form body
		if (classRes.hasProperty(ResourceFactory.createProperty(fs, "form")))
			json.addProperty(
					"form",
					classRes.getProperty(
							ResourceFactory.createProperty(fs, "form"))
							.getString());
		// cpv main
		if (classRes.hasProperty(pc_mainCPV))
			json.addProperty("mainCPV", classRes.getProperty(pc_mainCPV)
					.getObject().toString());

		// cpv additional
		String additionalCpvs = "";
		if (classRes.hasProperty(pc_additionalCPV)) {
			StmtIterator iter = classRes.listProperties((pc_additionalCPV));

			while (iter.hasNext()) {
				additionalCpvs += iter.nextStatement().getObject().toString()
						+ " ";
			}
		}
		json.addProperty("cpvAdd", additionalCpvs);

		System.out.println(json);
		return json;
	}

	/**
	 * Internal method transforms ResultSet into JSON object
	 * 
	 */
	protected JsonObject resultSetAsJsonObject(ResultSet resultSet) {
		JsonObject json = new JsonObject();
		List<String> vars = resultSet.getResultVars();
		QuerySolution resultRow;

		if (resultSet.hasNext()) {
			resultRow = resultSet.next();
			Iterator<String> i = vars.iterator();

			while (i.hasNext()) {
				String var = i.next();
				RDFNode node = resultRow.get(var);

				if (node != null) {
					json.addProperty(var, (node.isLiteral()) ? node.asLiteral()
							.getValue().toString() : node.toString());
				}
			}
		} else
			json = null;

		return json;
	}

	/**
	 * Internal method transforms ResultSet into JSON array
	 * 
	 */
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

	/**
	 * Returns classes as JSON array specified by CPV codes
	 * 
	 */
	public JsonArray getClassesByCpvAsJson(String[] cpv, String namedGraph) {
		JsonArray json = new JsonArray();
		ArrayList<String> cpvs;
		JsonObject tempJson = new JsonObject();
		ResultSet classURIrs;
		Gson gson = new Gson();
		int i = 0;
		while (cpv[i] != null) {
			cpvs = new ArrayList<String>();
			classURIrs = getClassByCpv(cpv[i], namedGraph);
			tempJson = resultSetAsJsonObject(classURIrs);
			boolean matches = false;
			if (json.size() > 0) {
				for (int j = 0; j < json.size(); j++) {
					if (tempJson.get("classURI").equals(
							json.get(j).getAsJsonObject().get("classURI"))) {
						JsonArray tmp = json.get(j).getAsJsonObject()
								.get("cpvs").getAsJsonArray();
						for (int k = 0; k < tmp.size(); k++) {
							cpvs.add(tmp.get(k).getAsString());
						}
						cpvs.add(cpv[i]);
						json.get(j).getAsJsonObject().remove("cpvs");
						json.get(j).getAsJsonObject()
								.add("cpvs", gson.toJsonTree(cpvs));
						matches = true;
						break;
					}
				}
			}
			if (!matches) {
				cpvs.add(cpv[i]);
				tempJson.add("cpvs", gson.toJsonTree(cpvs));
				json.add(tempJson);
			}
			i++;
		}
		return json;
	}

	/**
	 * Returns class as JENA model
	 * 
	 */
	public Model getClass(String formClass, String namedGraph) {		
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?class ?p1 ?o1 .} " + "FROM <"
				+ namedGraph + "> " + "WHERE " + "  { ?class ?p1 ?o1 . } "
				+ "VALUES ?class { <" + formClass + "> }");
		Model result = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();
		
		System.out.println(query);
		
		return result;
	};

	/**
	 * Returns class as ResultSet by CPV code
	 * 
	 */
	public ResultSet getClassByCpv(String cpv, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "SELECT  ?classURI " + "WHERE { GRAPH <" + namedGraph + "> "
				+ "  { ?classURI rdf:type owl:Class ." + "    ?classURI ?p <"
				+ config.getPreference("cpvURL")
				+ (cpv + "-").substring(0, (cpv + "-").indexOf('-')) + "> . }"
				+ "  } ");

		return QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();
	};
	
	/**
	 * Returns class properties as JSON array
	 * 
	 */
	public JsonArray getClassProperties(String classURI, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "SELECT  ?property " + "WHERE { GRAPH <" + namedGraph + "> "
				+ "  { ?property rdfs:domain ?class . }"
				+ "VALUES ?class { <"+classURI+"> }"
				+ "  } ");

		return this.resultSetAsJson(QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect());
	};

	/**
	 * Returns form by CPV code as JSON object
	 * 
	 */
	public JsonObject getFormByCpv(String cpv, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "SELECT  ?form " + "WHERE { GRAPH <" + namedGraph + "> "
				+ "  { ?classURI rdf:type owl:Class ." + "    ?classURI ?p <"
				+ config.getPreference("cpvURL")
				+ (cpv + "-").substring(0, (cpv + "-").indexOf('-')) + "> . "
				+ "    ?classURI fs:form ?form}" + "  } ");
		ResultSet tempClass = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();
		return this.resultSetAsJsonObject(tempClass);
	};

	/**
	 * Returns ontology by CPV code as JSON object
	 * 
	 */
	public JsonObject getOntologyByCpv(String cpv, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "SELECT  DISTINCT ?ontologyURI ?classURI "
				+ "WHERE { GRAPH <" + namedGraph + "> "
				+ "  { ?classURI rdfs:isDefinedBy ?ontologyURI ."
				+ "    ?classURI ?p <" + config.getPreference("cpvURL") + cpv
				+ "> . " + "}" + "  } ");
		ResultSet tempOntology = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execSelect();

		return this.resultSetAsJsonObject(tempOntology);
	};

	/**
	 * Returns specification as JENA model
	 * 
	 */
	public Model getSpecification(String specURI, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?specURI a gr:Offering . "
				+ "?specURI rdf:type gr:Offering ; gr:includes ?item . "
				+ "?item gr:hasMakeAndModel ?model ; gr:hasInventoryLevel ?inv . "
				+ "?inv ?p3 ?o3 . "
				+ "?model gr:name ?o4 ; gr:description ?o5 ; ?p1 ?o1 . "
				+ "?o1 ?p2 ?o2 . } " + "FROM <"
				+ namedGraph + "> " + "WHERE " + "  { ?specURI a gr:Offering . "
				+ "?specURI rdf:type gr:Offering ; gr:includes ?item . "
				+ "?item gr:hasMakeAndModel ?model ; gr:hasInventoryLevel ?inv . "
				+ "?inv ?p3 ?o3 . "
				+ "?model gr:name ?o4 ; gr:description ?o5 ; ?p1 ?o1 . "
				+ "?o1 ?p2 ?o2 . "
				+ " MINUS { ?p2 a ?o2 }} " + "VALUES ?specURI { <" + specURI
				+ "/> }");
		Model result = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();

		System.out.println(query);
		return result;
	};

	/**
	 * Returns property as JENA model
	 * 
	 */
	public Model getProperty(String propertyUri, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "CONSTRUCT  " + "  { ?propertyUri a ?o . "
				+ "?propertyUri ?p2 ?o2 . } " + "FROM <" + namedGraph + "> "
				+ "WHERE " + "  { ?propertyUri a ?o ."
				+ " ?propertyUri ?p2 ?o2 . } " + "VALUES ?propertyUri { <"
				+ propertyUri + "> }");
		Model result = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execConstruct();
		return result;
	};

	/**
	 * Returns specification as JSON object
	 * 
	 */
	public JsonObject getSpecificationAsJson(String specURI, String namedGraph) {

		Model specModel = getSpecification(specURI, namedGraph);

		JsonObject json = new JsonObject();

		String cpv = specURI.substring(specURI.indexOf("Offering") + 9,
				specURI.indexOf("Offering") + 17);
		JsonObject tempOntology = getOntologyByCpv(cpv,
				"http://ld.opendata.cz/tenderstats/namedgraph/admin");

		String ontologyURI = tempOntology.get("ontologyURI").getAsString();
		String classURI = tempOntology.get("classURI").getAsString();

		try {
			URL url = new URL(ontologyURI);

			OntModel m = ModelFactory.createOntologyModel(
					OntModelSpec.OWL_MEM_RDFS_INF, null);
			m.read(url.toString());
			ClassHierarchy ch = new ClassHierarchy();

			ObjectProperty[] objectPropertiesAll = ch.getObjectPropertiesAll(
					classURI, m);
			DatatypeProperty[] datatypePropertiesAll = ch
					.getDatatypePropertiesAll(classURI, m);

			String tempProperty = "";
			ResIterator iter = specModel.listSubjects();
			Resource rs;
			Model propertyModel;
			Resource propertyRes;

			while (iter.hasNext()) {
				rs = iter.next().asResource();
				tempProperty = rs.getURI().substring(
						rs.getURI().lastIndexOf('/') + 1);

				// Inventory level
				if (rs.hasProperty(ResourceFactory.createProperty(gr
						+ "hasInventoryLevel"))){
					JsonObject tempPropertyJson = new JsonObject();
					
					tempPropertyJson.addProperty("hasValueInteger", rs.getProperty(ResourceFactory.createProperty(gr
							+ "hasInventoryLevel")).getResource().getProperty(ResourceFactory
					.createProperty(gr, "hasValueInteger")).getLiteral().getString());
					
					tempPropertyJson.addProperty("hasUnitOfMeasurement", rs.getProperty(ResourceFactory.createProperty(gr
							+ "hasInventoryLevel")).getResource().getProperty(ResourceFactory
					.createProperty(gr, "hasUnitOfMeasurement")).getLiteral().getString());
					
					json.add("Inventory level", tempPropertyJson);
				}
				//Name + description
				if (rs.hasProperty(ResourceFactory.createProperty(gr
						+ "name"))){
					JsonObject tempPropertyJson = new JsonObject();
					
					tempPropertyJson.addProperty("name", rs.getProperty(ResourceFactory.createProperty(gr
							+ "name")).getLiteral().getString());
					
					tempPropertyJson.addProperty("description", rs.getProperty(ResourceFactory.createProperty(gr
							+ "description")).getLiteral().getString());
					
					json.add("Name", tempPropertyJson);
				}
				//object quantitave properties
				for (int i = 0; i < objectPropertiesAll.length; i++)
					if (objectPropertiesAll[i] != null
							&& objectPropertiesAll[i].getLocalName().equals(
									tempProperty)) {

						if (objectPropertiesAll[i]
								.getSuperProperty()
								.toString()
								.equals("http://purl.org/goodrelations/v1#quantitativeProductOrServiceProperty")) {
							propertyModel = getProperty(rs.getURI(), namedGraph);
							propertyRes = propertyModel
									.getResource(rs.getURI());
							JsonObject tempPropertyJson = new JsonObject();

							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasMinValue"))) {
								tempPropertyJson
										.addProperty(
												"hasMinValue",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasMinValue"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasMaxValue"))) {
								tempPropertyJson
										.addProperty(
												"hasMaxValue",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasMaxValue"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasValue"))) {
								tempPropertyJson
										.addProperty(
												"hasValue",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasValue"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasMinValueInteger"))) {
								tempPropertyJson
										.addProperty(
												"hasMinValueInteger",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasMinValueInteger"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasMaxValueInteger"))) {
								tempPropertyJson
										.addProperty(
												"hasMaxValueInteger",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasMaxValueInteger"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasValueInteger"))) {
								tempPropertyJson
										.addProperty(
												"hasValueInteger",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasValueInteger"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasMinValueFloat"))) {
								tempPropertyJson
										.addProperty(
												"hasMinValueFloat",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasMinValueFloat"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasMaxValueFloat"))) {
								tempPropertyJson
										.addProperty(
												"hasMaxValueFloat",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasMaxValueFloat"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "hasValueFloat"))) {
								tempPropertyJson
										.addProperty(
												"hasValueFloat",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasValueFloat"))
														.getObject()
														.asLiteral()
														.getString());
							}
							if (propertyRes
									.hasProperty(ResourceFactory
											.createProperty(gr,
													"hasUnitOfMeasurement"))) {
								tempPropertyJson
										.addProperty(
												"hasUnitOfMeasurement",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"hasUnitOfMeasurement"))
														.getObject()
														.asLiteral()
														.getString());
							}
							json.add(objectPropertiesAll[i].getLabel(null),
									tempPropertyJson);
						}
						//object qualitative properties
						if (objectPropertiesAll[i]
								.getSuperProperty()
								.toString()
								.equals("http://purl.org/goodrelations/v1#qualitativeProductOrServiceProperty")) {
							propertyModel = getProperty(rs.getURI(), namedGraph);
							propertyRes = propertyModel
									.getResource(rs.getURI());
							JsonObject tempPropertyJson = new JsonObject();

							if (propertyRes.hasProperty(ResourceFactory
									.createProperty(gr, "valueReference"))) {
								tempPropertyJson
										.addProperty(
												"valueReference",
												propertyRes
														.getProperty(
																ResourceFactory
																		.createProperty(
																				gr,
																				"valueReference"))
														.getObject()
														.asLiteral()
														.getString());
							}
							json.add(objectPropertiesAll[i].getLabel(null),
									tempPropertyJson);
						}

						break;
					}
				//datatype properties
				for (int i = 0; i < datatypePropertiesAll.length; i++)
					if (datatypePropertiesAll[i] != null
							&& datatypePropertiesAll[i].getLocalName().equals(
									tempProperty)) {
						if (datatypePropertiesAll[i]
								.getSuperProperty()
								.toString()
								.equals("http://purl.org/goodrelations/v1#datatypeProductOrServiceProperty")) {
							propertyModel = getProperty(rs.getURI(), namedGraph);
							propertyRes = propertyModel
									.getResource(rs.getURI());
							JsonObject tempPropertyJson = new JsonObject();
							tempPropertyJson
									.addProperty(
											"datatypeValue",
											propertyRes
													.getProperty(
															ResourceFactory
																	.createProperty(
																			gr,
																			"datatypeProductOrServiceProperty"))
													.getObject().asLiteral()
													.getString());
							json.add(datatypePropertiesAll[i].getLabel(null),
									tempPropertyJson);

						}

					}

			}

		} catch (RiotException | IOException ex) {
			System.out.println(ex.getMessage());
		}

		System.out.println(json);
		return json;
	}

	/**
	 * Returns true or false if form with CPV code exists
	 * 
	 */
	public boolean checkCPV(String cpv, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "ASK  " + "  { GRAPH <" + namedGraph + "> "
				+ "  { ?class ?p <" + config.getPreference("cpvURL")
				+ (cpv + "-").substring(0, (cpv + "-").indexOf('-'))
				+ "> . }} ");
		boolean answer = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execAsk();

		return !answer;
	}

	/**
	 * Returns true or false if ontology with URI exists
	 * 
	 */
	public boolean checkOntology(String ontologyURI, String namedGraph) {
		Query query = QueryFactory.create(config.getPreference("prefixes")
				+ "ASK  " + "  { GRAPH <" + namedGraph + "> " + "  { <"
				+ ontologyURI + "> a owl:Ontology .}} ");
		boolean answer = QueryExecutionFactory.sparqlService(
				config.getSparqlPrivateQuery(), query).execAsk();
		return answer;
	}

}
