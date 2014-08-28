package cz.opendata.tenderstats.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.sparql.function.library.strlen;

public class RDFaBuilder {
	
	/*
	 * Returns pre-RDFa form for Buyer based on selected properties
	 * IN DEVELOPMENT
	 * 	 */
	public String generateRDFaBuyer(URL uri, OntModel m, String[] opValues, String[] dpValues) {

		ClassHierarchy ch = new ClassHierarchy();
		BufferedReader in;
		String RDFa = 
			"<div id=\"RDFcontent\"><div id=\"RDFroot\" name=\"RDFroot\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:gr=\"http://purl.org/goodrelations/v1#\">" +
				"<form action=\"#\" method=\"post\" class=\"form-horizontal\" id=\"addSpecificationForm\" enctype=\"multipart/form-data\" >" +
					"<input name=\"action\" type=\"hidden\" value=\"addSpecification\" />" +
					"<input name=\"forward\" type=\"hidden\" value=\"buyer-add-specification.html\" />" +
					"<input id=\"contractURI\" name=\"contractURI\" type=\"hidden\" value=\"\" />" +
						"<div about=\"base:Offering\" typeof=\"http://purl.org/goodrelations/v1#Offering\">" +
							"<input id=\"validFrom\" name=\"validFrom\" property=\"http://purl.org/goodrelations/v1#validFrom\" datatype=\"xsd:dateTime\" type=\"hidden\" value=\"\" />" +
							"<input id=\"validTo\" name=\"validTo\" property=\"http://purl.org/goodrelations/v1#validTo\" datatype=\"xsd:dateTime\" type=\"hidden\" value=\"\" class=\"hasDatepicker\" />" +
							"<div rel=\"http://purl.org/goodrelations/v1#includes\">" +
								"<div about=\"base:SomeItems\" typeof=\"http://purl.org/goodrelations/v1#SomeItems\">" +
									"<div rel=\"http://purl.org/goodrelations/v1#hasInventoryLevel\">" +
										"<div about=\"base:inventoryLevel\" typeof=\"http://purl.org/goodrelations/v1#QuantitativeValueInteger\">" +
											"<label for=\"inventoryLevel\">Inventory level</label>" +
											"<input property=\"http://purl.org/goodrelations/v1#hasValueInteger\" datatype=\"xsd:int\" type=\"text\" id=\"inventoryLevel\" name=\"inventoryLevel\" value=\"\" />" +
											"<input type=\"button\" class=\"btn\" id=\"inventoryLevelMinus\" value=\"-\" onclick=\"if(getElementById('inventoryLevel').value=='' || getElementById('inventoryLevel').value<=0) {getElementById('inventoryLevel').value = '1';}getElementById('inventoryLevel').value = (parseFloat(getElementById('inventoryLevel').value) - 1).toFixed(0);\" />"+
											"<input type=\"button\" class=\"btn\" id=\"inventoryLevelPlus\" value=\"+\" onclick=\"if(getElementById('inventoryLevel').value=='' || getElementById('inventoryLevel').value<0) {getElementById('inventoryLevel').value = '0';} getElementById('inventoryLevel').value = (parseFloat(getElementById('inventoryLevel').value) + 1).toFixed(0);\" />"+
											"<label class=\"small\">Minimum</label>" +
											"<input id=\"inventoryLevel-minValue\" name=\"inventoryLevel-minValue\" type=\"text\" value=\"\"" +
											"property=\"http://purl.org/goodrelations/v1#hasMinValueInteger\" datatype=\"xsd:int\"/>"+
											"<label class=\"small\">Maximum</label>" +
											"<input id=\"inventoryLevel-maxValue\" name=\"inventoryLevel-maxValue\" type=\"text\" value=\"\"" +
											"property=\"http://purl.org/goodrelations/v1#hasMaxValueInteger\" datatype=\"xsd:int\"/>"+
											"<label for=\"inventoryLevelUnitOfMeasurement\">Unit of measurement</label>" +
											"<input property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\" type=\"text\" id=\"inventoryLevelUnitOfMeasurement\" name=\"inventoryLevelUnitOfMeasurement\" value=\"\" />" +
											"<br /><br />" +
										"</div>" +
									"</div>" +
									"<h4>Product</h4>" +
									"<div rel=\"http://purl.org/goodrelations/v1#hasMakeAndModel\">" +
										"<div about=\"base:makeAndModel\" typeof=\"http://purl.org/goodrelations/v1#ProductOrServiceModel\">" +
											"<label for=\"productName\">Name</label>" +
											"<input class=\"big\" property=\"http://purl.org/goodrelations/v1#name\" datatype=\"rdfs:Literal\" type=\"text\" id=\"productName\" name=\"productName\" value=\"\" />" +
											"<label for=\"productDescription\">Description</label>" +
											"<textarea property=\"http://purl.org/goodrelations/v1#description\" datatype=\"rdfs:Literal\" id=\"productDescription\" name=\"productDescription\" />" +
											"<br /><br /><br /><br />";
		try {
			URLConnection con = uri.openConnection();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			ObjectProperty[] objectProperties = ch.getObjectPropertiesDefined(m, opValues);
			for (int i = 0; i < objectProperties.length; i++){
				RDFa +="<div rel=\""+objectProperties[i].getSuperProperty().toString()+"\">";
				RDFa +="<div about=\"base:"+objectProperties[i].getLocalName().toString()+"\" typeof=\""+objectProperties[i].getURI().toString()+"\">";
				RDFa +="<label for=\""+objectProperties[i].getLocalName().toString()+"\" data-original-title=\""+objectProperties[i].getComment(null)+"\">"+objectProperties[i].getLabel(null).toString()+"</label>";				
				RDFa +="<input id=\""+objectProperties[i].getLocalName().toString()+"\" name=\""+objectProperties[i].getLocalName().toString()+"\" type=\"text\" value=\"\" ";
				if(objectProperties[i].getSuperProperty().toString().equals("http://purl.org/goodrelations/v1#qualitativeProductOrServiceProperty")){
					RDFa += "property=\"http://purl.org/goodrelations/v1#valueReference\" datatype=\"http://purl.org/goodrelations/v1#QualitativeValue\"/>";
				}
				if(objectProperties[i].getRange().toString().equals("http://purl.org/goodrelations/v1#QuantitativeValue")){
					RDFa += "property=\"http://purl.org/goodrelations/v1#hasValue\" datatype=\"rdfs:Literal\"/>";
					RDFa +=	"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Minus\" value=\"-\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<=0) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '1';}getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) - 1).toFixed(0);\" />"+
							"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Plus\" value=\"+\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<0) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0';} getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) + 1).toFixed(0);\" />";
					RDFa += "<label class=\"small\">Minimum</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-minValue\" name=\""+objectProperties[i].getLocalName().toString()+"-minValue\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasMinValue\" datatype=\"rdfs:Literal\"/>";
					RDFa += "<label class=\"small\">Maximum</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" name=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasMaxValue\" datatype=\"rdfs:Literal\"/>";
					RDFa += "<label>Unit of measurement</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-unit\" name=\""+objectProperties[i].getLocalName().toString()+"-unit\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\"/>";
				}
				if(objectProperties[i].getRange().toString().equals("http://purl.org/goodrelations/v1#QuantitativeValueInteger")){
					RDFa += "property=\"http://purl.org/goodrelations/v1#hasValueInteger\" datatype=\"xsd:int\"/>";
					RDFa +=	"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Minus\" value=\"-\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<=0) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '1';}getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) - 1).toFixed(0);\" />"+
							"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Plus\" value=\"+\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<0) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0';} getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) + 1).toFixed(0);\" />";
					RDFa += "<label class=\"small\">Minimum</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-minValue\" name=\""+objectProperties[i].getLocalName().toString()+"-minValue\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasMinValueInteger\" datatype=\"xsd:int\"/>";
					RDFa += "<label class=\"small\">Maximum</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" name=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasMaxValueInteger\" datatype=\"xsd:int\"/>";
					RDFa += "<label>Unit of measurement</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-unit\" name=\""+objectProperties[i].getLocalName().toString()+"-unit\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\"/>";
				}
				if(objectProperties[i].getRange().toString().equals("http://purl.org/goodrelations/v1#QuantitativeValueFloat")){
					RDFa += "property=\"http://purl.org/goodrelations/v1#hasValueFloat\" datatype=\"xsd:float\"/>";
					RDFa +=	"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Minus\" value=\"-\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<=0) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0.1';}getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) - 0.1).toFixed(1);\" />"+
							"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Plus\" value=\"+\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<0) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0.0';} getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) + 0.1).toFixed(1);\" />";
					RDFa += "<label class=\"small\">Minimum</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-minValue\" name=\""+objectProperties[i].getLocalName().toString()+"-minValue\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasMinValueFloat\" datatype=\"xsd:float\"/>";
					RDFa += "<label class=\"small\">Maximum</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" name=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasMaxValueInteger\" datatype=\"xsd:float\"/>";
					RDFa += "<label>Unit of measurement</label>" +
							"<input id=\""+objectProperties[i].getLocalName().toString()+"-unit\" name=\""+objectProperties[i].getLocalName().toString()+"-unit\" type=\"text\" value=\"\"" +
									"property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\"/>";
				}
				RDFa +=	"</div></div><br /><br />";
			}
			
			RDFa += 				"</div>" +
								"</div>" +									
							"</div>" +
						"</div>" +
					"</div>";
			RDFa += "<input class=\"btn\" id=\"saveRDF\" type=\"input\" value=\"Send\" style=\"width: 150px; float: left;\" onclick=\"generateRDF();\"/>" +
				"</form>" +
			"</div></div>";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RDFa;
	}

	/*
	 * Returns pre-RDFa form for Seller based on selected properties
	 * 
	 * 	 */
	public String generateRDFaSeller(URL uri, OntModel m, String[] opValues, String[] dpValues, String[] opRequired, String[] dpRequired) {

		ClassHierarchy ch = new ClassHierarchy();
		BufferedReader in;
		String RDFa = 
			"<div id=\"RDFcontent\"><div id=\"RDFroot\" name=\"RDFroot\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:gr=\"http://purl.org/goodrelations/v1#\">" +
				"<form action=\"#\" method=\"post\" class=\"form-horizontal\" id=\"addSpecificationForm\" enctype=\"multipart/form-data\" >" +
					"<input id=\"contractURI\" name=\"contractURI\" type=\"hidden\" value=\"\" />" +
						"<div about=\"base:Offering\" typeof=\"http://purl.org/goodrelations/v1#Offering\">" +
							"<input id=\"validFrom\" name=\"validFrom\" property=\"http://purl.org/goodrelations/v1#validFrom\" datatype=\"xsd:dateTime\" type=\"hidden\" value=\"\" />" +
							"<input id=\"validTo\" name=\"validTo\" property=\"http://purl.org/goodrelations/v1#validTo\" datatype=\"xsd:dateTime\" type=\"hidden\" value=\"\" class=\"hasDatepicker\" />" +
							"<div rel=\"http://purl.org/goodrelations/v1#includes\">" +
								"<div about=\"base:SomeItems\" typeof=\"http://purl.org/goodrelations/v1#SomeItems\">" +
									"<div rel=\"http://purl.org/goodrelations/v1#hasInventoryLevel\">" +
										"<div about=\"base:inventoryLevel\" typeof=\"http://purl.org/goodrelations/v1#QuantitativeValueInteger\">" +
											"<p class=\"btn-group\" style=\"float: left\" data-toggle=\"buttons-radio\">" +
												"<button id=\"inventoryLevelSpecific\" type=\"button\" class=\"btn active\" title=\"Specific value\" " +
													"onclick=\"$('#inventoryLevel').prop('type','text');" +
														"$('#inventoryLevelMinus').prop('type','button');" +
															"$('#inventoryLevelPlus').prop('type','button');" +
																"$('#inventoryLevel-minValue').prop('type','hidden');" +
																	"$('#inventoryLevel-minLabel').addClass('hidden');" +
																		"$('#inventoryLevel-maxValue').prop('type','hidden');" +
																			"$('#inventoryLevel-maxLabel').addClass('hidden');" +
																				"if($('#inventoryLevel-minValue').prop('required')) {$('#inventoryLevel-minValue').removeProp('required');$('#inventoryLevel-maxValue').removeProp('required');$('#inventoryLevel').attr('required','');}" +
																					"\">S</button>" +
												"<button id=\"inventoryLevelRange\" type=\"button\" class=\"btn\" title=\"Range value\" " +
													"onclick=\"$('#inventoryLevel').prop('type','hidden');" +
														"$('#inventoryLevelMinus').prop('type','hidden');" +
															"$('#inventoryLevelPlus').prop('type','hidden');" +
																"$('#inventoryLevel-minValue').prop('type','text');" +
																	"$('#inventoryLevel-minLabel').removeClass('hidden');" +
																		"$('#inventoryLevel-maxValue').prop('type','text');" +
																			"$('#inventoryLevel-maxLabel').removeClass('hidden');" +
																				"if($('#inventoryLevel').prop('required')) {$('#inventoryLevel').removeProp('required');$('#inventoryLevel-minValue').attr('required','');$('#inventoryLevel-maxValue').attr('required','');}" +
																					"\">R</button>" +
											"</p>" +
											"<label for=\"inventoryLevel\">Inventory level<font color=\"red\">*</font></label>" +
											"<input required property=\"http://purl.org/goodrelations/v1#hasValueInteger\" datatype=\"xsd:int\" type=\"text\" id=\"inventoryLevel\" name=\"inventoryLevel\" value=\"\" />" +
											"<input type=\"button\" class=\"btn\" id=\"inventoryLevelMinus\" value=\"-\" onclick=\"if(getElementById('inventoryLevel').value=='' || getElementById('inventoryLevel').value<=0 || isNaN(parseFloat(getElementById('inventoryLevel').value))) {getElementById('inventoryLevel').value = '1';} getElementById('inventoryLevel').value = (parseFloat(getElementById('inventoryLevel').value) - 1).toFixed(0);\" />"+
											"<input type=\"button\" class=\"btn\" id=\"inventoryLevelPlus\" value=\"+\" onclick=\"if(getElementById('inventoryLevel').value=='' || getElementById('inventoryLevel').value<0 || isNaN(parseFloat(getElementById('inventoryLevel').value))) {getElementById('inventoryLevel').value = '0';} getElementById('inventoryLevel').value = (parseFloat(getElementById('inventoryLevel').value) + 1).toFixed(0);\" />"+
											"<label class=\"small hidden\" id=\"inventoryLevel-minLabel\">Min</label>" +
											"<input id=\"inventoryLevel-minValue\" name=\"inventoryLevel-minValue\" type=\"hidden\" value=\"\"" +
											"property=\"http://purl.org/goodrelations/v1#hasMinValueInteger\" datatype=\"xsd:int\"/>"+
											"<label class=\"small hidden\" id=\"inventoryLevel-maxLabel\">Max</label>" +
											"<input id=\"inventoryLevel-maxValue\" name=\"inventoryLevel-maxValue\" type=\"hidden\" value=\"\"" +
											"property=\"http://purl.org/goodrelations/v1#hasMaxValueInteger\" datatype=\"xsd:int\"/>"+
											"<label for=\"inventoryLevelUnitOfMeasurement\">Unit of measurement<font color=\"red\">*</font></label>" +
											"<input required property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\" type=\"text\" id=\"inventoryLevelUnitOfMeasurement\" name=\"inventoryLevelUnitOfMeasurement\" value=\"\" />" +
											"<br /><br />" +
										"</div>" +
									"</div>" +
									"<h4>Product</h4>" +
									"<div rel=\"http://purl.org/goodrelations/v1#hasMakeAndModel\">" +
										"<div about=\"base:makeAndModel\" typeof=\"http://purl.org/goodrelations/v1#ProductOrServiceModel\">" +
											"<label for=\"productName\">Name<font color=\"red\">*</font></label>" +
											"<input required class=\"big\" property=\"http://purl.org/goodrelations/v1#name\" datatype=\"rdfs:Literal\" type=\"text\" id=\"productName\" name=\"productName\" value=\"\" />" +
											"<label for=\"productDescription\">Description</label>" +
											"<textarea property=\"http://purl.org/goodrelations/v1#description\" datatype=\"rdfs:Literal\" id=\"productDescription\" name=\"productDescription\" />" +
											"<br /><br /><br /><br />";
		try {
			URLConnection con = uri.openConnection();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String disabled = "";
			//ObjectProperties
			ObjectProperty[] objectProperties = new ObjectProperty[100];
			if(opValues != null && opValues.length > 0) {
				objectProperties = ch.getObjectPropertiesDefined(m, opValues);
				for (int i = 0; i < objectProperties.length; i++){
				    String required = "";
					if(opRequired != null && Arrays.asList(opRequired).contains(String.valueOf(i))) required = "required";					
					RDFa +="<div rel=\""+objectProperties[i].getSuperProperty().toString()+"\">";
					RDFa +="<div style=\"width: 100%;\" about=\"base:"+objectProperties[i].getLocalName().toString()+"\" typeof=\""+objectProperties[i].getURI().toString()+"\">";
					if(!objectProperties[i].getRange().toString().contains("http://purl.org/goodrelations/v1#QuantitativeValue")) disabled = "disabled"; else disabled = "";
					RDFa +=	"<p class=\"btn-group\" style=\"float: left\" data-toggle=\"buttons-radio\">" +
								"<button id=\"inventoryLevelSpecific\" type=\"button\" class=\"btn active\" "+disabled+" title=\"Specific value\" " +
									"onclick=\"$('#"+objectProperties[i].getLocalName().toString()+"').prop('type','text');" +
											"$('#"+objectProperties[i].getLocalName().toString()+"Minus').prop('type','button');" +
													"$('#"+objectProperties[i].getLocalName().toString()+"Plus').prop('type','button');" +
															"$('#"+objectProperties[i].getLocalName().toString()+"Value').prop('type','hidden');" +
																	"$('#"+objectProperties[i].getLocalName().toString()+"Label').addClass('hidden');" +
																			"$('#"+objectProperties[i].getLocalName().toString()+"-minValue').prop('type','hidden');" +
																					"$('#"+objectProperties[i].getLocalName().toString()+"-minLabel').addClass('hidden');" +
																							"$('#"+objectProperties[i].getLocalName().toString()+"-maxValue').prop('type','hidden');" +
																									"$('#"+objectProperties[i].getLocalName().toString()+"-maxLabel').addClass('hidden');" +
																											"if($('#"+objectProperties[i].getLocalName().toString()+"-minValue').prop('required')) {$('#"+objectProperties[i].getLocalName().toString()+"-minValue').removeProp('required');$('#"+objectProperties[i].getLocalName().toString()+"-maxValue').removeProp('required');$('#"+objectProperties[i].getLocalName().toString()+"').attr('required','');}" +
																													"\">S</button>" +
								"<button id=\"inventoryLevelRange\" type=\"button\" class=\"btn\" "+disabled+" title=\"Range value\" " +
									"onclick=\"$('#"+objectProperties[i].getLocalName().toString()+"').prop('type','hidden');" +
											"$('#"+objectProperties[i].getLocalName().toString()+"Minus').prop('type','hidden');" +
													"$('#"+objectProperties[i].getLocalName().toString()+"Plus').prop('type','hidden');" +
															"$('#"+objectProperties[i].getLocalName().toString()+"Value').prop('type','text');" +
																	"$('#"+objectProperties[i].getLocalName().toString()+"Label').removeClass('hidden');" +
																			"$('#"+objectProperties[i].getLocalName().toString()+"-minValue').prop('type','text');" +
																					"$('#"+objectProperties[i].getLocalName().toString()+"-minLabel').removeClass('hidden');" +
																							"$('#"+objectProperties[i].getLocalName().toString()+"-maxValue').prop('type','text');" +
																									"$('#"+objectProperties[i].getLocalName().toString()+"-maxLabel').removeClass('hidden');" +
																											"if($('#"+objectProperties[i].getLocalName().toString()+"').prop('required')) {$('#"+objectProperties[i].getLocalName().toString()+"').removeProp('required');$('#"+objectProperties[i].getLocalName().toString()+"-minValue').attr('required','');$('#"+objectProperties[i].getLocalName().toString()+"-maxValue').attr('required','');}" +
																													"\">R</button>" +
							"</p>";
					RDFa +="<label for=\""+objectProperties[i].getLocalName().toString()+"\" title=\""+objectProperties[i].getComment(null)+"\">"+objectProperties[i].getLabel(null).toString();
					if(required != "") RDFa +="<font color=\"red\">*</font></label>"; else RDFa +="</label>";			
					RDFa +="<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"\" name=\""+objectProperties[i].getLocalName().toString()+"\" type=\"text\" value=\"\" ";
					if(objectProperties[i].getSuperProperty().toString().equals("http://purl.org/goodrelations/v1#qualitativeProductOrServiceProperty")){
						RDFa += "property=\"http://purl.org/goodrelations/v1#valueReference\" datatype=\"http://purl.org/goodrelations/v1#QualitativeValue\" class=\"big\"/>";
					}
					if(objectProperties[i].getRange().toString().equals("http://purl.org/goodrelations/v1#QuantitativeValue")){
						RDFa += "property=\"http://purl.org/goodrelations/v1#hasValue\" datatype=\"rdfs:Literal\"/>";
						RDFa +=	"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Minus\" value=\"-\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<=0 || isNaN(parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value))) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '1';}getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) - 1).toFixed(0);\" />"+
								"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Plus\" value=\"+\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<0 || isNaN(parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value))) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0';} getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) + 1).toFixed(0);\" />";
						RDFa += "<label class=\"small hidden\" id=\""+objectProperties[i].getLocalName().toString()+"-minLabel\">Min</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-minValue\" name=\""+objectProperties[i].getLocalName().toString()+"-minValue\" type=\"hidden\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasMinValue\" datatype=\"rdfs:Literal\"/>";
						RDFa += "<label class=\"small hidden\" id=\""+objectProperties[i].getLocalName().toString()+"-maxLabel\">Max</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" name=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" type=\"hidden\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasMaxValue\" datatype=\"rdfs:Literal\"/>";
						RDFa += "<label>Unit of measurement</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-unit\" name=\""+objectProperties[i].getLocalName().toString()+"-unit\" type=\"text\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\"/>";
					}
					if(objectProperties[i].getRange().toString().equals("http://purl.org/goodrelations/v1#QuantitativeValueInteger")){
						RDFa += "property=\"http://purl.org/goodrelations/v1#hasValueInteger\" datatype=\"xsd:int\"/>";
						RDFa +=	"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Minus\" value=\"-\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<=0 || isNaN(parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value))) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '1';}getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) - 1).toFixed(0);\" />"+
								"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Plus\" value=\"+\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<0 || isNaN(parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value))) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0';} getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) + 1).toFixed(0);\" />";
						RDFa += "<label class=\"small hidden\" id=\""+objectProperties[i].getLocalName().toString()+"-minLabel\">Min</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-minValue\" name=\""+objectProperties[i].getLocalName().toString()+"-minValue\" type=\"hidden\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasMinValueInteger\" datatype=\"xsd:int\"/>";
						RDFa += "<label class=\"small hidden\" id=\""+objectProperties[i].getLocalName().toString()+"-maxLabel\">Max</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" name=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" type=\"hidden\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasMaxValueInteger\" datatype=\"xsd:int\"/>";
						RDFa += "<label>Unit of measurement</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-unit\" name=\""+objectProperties[i].getLocalName().toString()+"-unit\" type=\"text\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\"/>";
					}
					if(objectProperties[i].getRange().toString().equals("http://purl.org/goodrelations/v1#QuantitativeValueFloat")){
						RDFa += "property=\"http://purl.org/goodrelations/v1#hasValueFloat\" datatype=\"xsd:float\"/>";
						RDFa +=	"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Minus\" value=\"-\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<=0 || isNaN(parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value))) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0.1';}getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) - 0.1).toFixed(1);\" />"+
								"<input type=\"button\" class=\"btn\" id=\""+objectProperties[i].getLocalName().toString()+"Plus\" value=\"+\" onclick=\"if(getElementById('"+objectProperties[i].getLocalName().toString()+"').value=='' || getElementById('"+objectProperties[i].getLocalName().toString()+"').value<0 || isNaN(parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value))) {getElementById('"+objectProperties[i].getLocalName().toString()+"').value = '0.0';} getElementById('"+objectProperties[i].getLocalName().toString()+"').value = (parseFloat(getElementById('"+objectProperties[i].getLocalName().toString()+"').value) + 0.1).toFixed(1);\" />";
						RDFa += "<label class=\"small hidden\" id=\""+objectProperties[i].getLocalName().toString()+"-minLabel\">Min</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-minValue\" name=\""+objectProperties[i].getLocalName().toString()+"-minValue\" type=\"hidden\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasMinValueFloat\" datatype=\"xsd:float\"/>";
						RDFa += "<label class=\"small hidden\" id=\""+objectProperties[i].getLocalName().toString()+"-maxLabel\">Max</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" name=\""+objectProperties[i].getLocalName().toString()+"-maxValue\" type=\"hidden\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasMaxValueFloat\" datatype=\"xsd:float\"/>";
						RDFa += "<label>Unit of measurement</label>" +
								"<input " + required + " id=\""+objectProperties[i].getLocalName().toString()+"-unit\" name=\""+objectProperties[i].getLocalName().toString()+"-unit\" type=\"text\" value=\"\"" +
										"property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\"/>";
					}
					RDFa +=	"</div></div><br /><br />";
				}
			}
			//DatatypeProperties
			DatatypeProperty[] datatypeProperties = new DatatypeProperty[100];
				if(dpValues != null && dpValues.length > 0) {
				datatypeProperties = ch.getDatatypePropertiesDefined(m, dpValues);
				for (int i = 0; i < datatypeProperties.length; i++){
					RDFa +="<div rel=\""+datatypeProperties[i].getSuperProperty().toString()+"\">";
					RDFa +="<div style=\"width: 100%;\" about=\"base:"+datatypeProperties[i].getLocalName().toString()+"\" typeof=\""+datatypeProperties[i].getURI().toString()+"\">";
					disabled = "disabled";
					RDFa +=	"<p class=\"btn-group\" style=\"float: left\" data-toggle=\"buttons-radio\">" +
								"<button id=\"inventoryLevelSpecific\" type=\"button\" class=\"btn active\" "+disabled+" title=\"Specific value\" " +
									"onclick=\"$('#"+datatypeProperties[i].getLocalName().toString()+"').prop('type','text');$('#"+datatypeProperties[i].getLocalName().toString()+"Minus').prop('type','button');$('#"+datatypeProperties[i].getLocalName().toString()+"Plus').prop('type','button');$('#"+datatypeProperties[i].getLocalName().toString()+"Value').prop('type','hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"Label').addClass('hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"-minValue').prop('type','hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"-minLabel').addClass('hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"-maxValue').prop('type','hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"-maxLabel').addClass('hidden');\">S</button>" +
								"<button id=\"inventoryLevelRange\" type=\"button\" class=\"btn\" "+disabled+" title=\"Range value\" " +
									"onclick=\"$('#"+datatypeProperties[i].getLocalName().toString()+"').prop('type','hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"Minus').prop('type','hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"Plus').prop('type','hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"Value').prop('type','text');$('#"+datatypeProperties[i].getLocalName().toString()+"Label').removeClass('hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"-minValue').prop('type','text');$('#"+datatypeProperties[i].getLocalName().toString()+"-minLabel').removeClass('hidden');$('#"+datatypeProperties[i].getLocalName().toString()+"-maxValue').prop('type','text');$('#"+datatypeProperties[i].getLocalName().toString()+"-maxLabel').removeClass('hidden');\">R</button>" +
							"</p>";
					RDFa +="<label for=\""+datatypeProperties[i].getLocalName().toString()+"\" title=\""+datatypeProperties[i].getComment(null)+"\">"+datatypeProperties[i].getLabel(null).toString()+"</label>";				
					RDFa +="<input id=\""+datatypeProperties[i].getLocalName().toString()+"\" name=\""+datatypeProperties[i].getLocalName().toString()+"\" type=\"text\" value=\"\" ";
					RDFa += "property=\"http://purl.org/goodrelations/v1#datatypeProductOrServiceProperty\" datatype=\""+ datatypeProperties[i].getRange().toString() + "\" class=\"big\" />";
					
					RDFa +=	"</div></div><br /><br />";
				}
			}
			
			
			
			RDFa += 				"</div>" +
								"</div>" +									
							"</div>" +
						"</div>" +
					"</div>";
			RDFa += "<input class=\"btn\" id=\"saveRDF\" type=\"input\" value=\"Send\" style=\"width: 150px; float: left;\" onclick=\"generateRDF();\"/>" +
				"</form>" +
			"</div></div>";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Service for specific URL unavailable...<br /> "+e.getMessage();
		}
		
		return RDFa;
	}
	
	/*
	 * Returns pre-RDFa form based on selected properties
	 * DEPRECATED
	 * 	 */	
	private void parseXmlFile(String inputXML) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(inputXML));

		Document doc = null;
		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			// Create file
			FileWriter fstream = new FileWriter("out.html");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<div id=\"RDFroot\" name=\"RDFroot\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:foo=\"http://example.org/#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:gr=\"http://purl.org/goodrelations/v1#\" xmlns:dbpedia=\"http://dbpedia.org/resource/\" xmlns:vso=\"http://purl.org/vso/ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"
					+ "<h1>Add Specification</h1>"
					+ "<form action=\"#\" method=\"post\">"
					+ "<h3>Business Entity</h3>"
					+ "<div about=\"base:company1\" typeof=\"http://purl.org/goodrelations/v1#BusinessEntity\">"
					+ "<label for=\"legalName\">Legal Name</label>"
					+ "<input property=\"http://purl.org/goodrelations/v1#legalName\" id=\"legalName\" name=\"legalName\" value=\"Legal Name\" type=\"text\" datatype=\"rdfs:Literal\"/><br /><br />"
					+ "<h3>Offer</h3>"
					+ "<div rel=\"http://purl.org/goodrelations/v1#offers\">"
					+ "<div about=\"base:offer1\" typeof=\"http://purl.org/goodrelations/v1#Offering\">"
					+ "<label for=\"validFrom\">Valid from</label>"
					+ "<input id=\"validFrom\" name=\"validFrom\" property=\"http://purl.org/goodrelations/v1#validFrom\" datatype=\"xsd:dateTime\" type=\"text\" value=\"28.12.2012\" />"
					+ "<label for=\"validTo\">Valid to</label>"
					+ "<input id=\"validTo\" name=\"validTo\" property=\"http://purl.org/goodrelations/v1#validTo\" datatype=\"xsd:dateTime\" type=\"text\" value=\"15.2.2013\" /><br /><br />"
					+ "<div rel=\"http://purl.org/goodrelations/v1#includes\">"
					+ "<div about=\"base:cars\" typeof=\"http://purl.org/goodrelations/v1#SomeItems\">"
					+ "<div rel=\"http://purl.org/goodrelations/v1#hasInvetoryLevel\">"
					+ "<div about=\"base:pieces\" typeof=\"http://purl.org/goodrelations/v1#QuantitativeValueInteger\">"
					+ "<label for=\"inventoryLevel\">Inventory level</label>"
					+ "<input property=\"http://purl.org/goodrelations/v1#hasValueInteger\" datatype=\"xsd:int\" type=\"text\" id=\"inventoryLevel\" name=\"inventoryLevel\" value=\"20\"/>"
					+ "<label for=\"inventoryLevelUnitOfMeasurement\">Unit of measurement</label>"
					+ "<input property=\"http://purl.org/goodrelations/v1#UnitOfMeasurement\" datatype=\"xsd:string\" type=\"text\" id=\"inventoryLevelUnitOfMeasurement\" name=\"inventoryLevelUnitOfMeasurement\" value=\"pc\"/><br /><br />"
					+ "</div>" + "</div>" + "<h3>Product</h3>");

			// Product class
			Element product = (Element) doc.getElementsByTagName("owl:Class")
					.item(0);
			// gr:hasMakeAndModel
			// open div
			out.write("<div rel=\"http://purl.org/goodrelations/v1#hasMakeAndModel\">");
			// gr:ProductOrServiceModel
			// open div
			out.write("<div about=\"base:model\" typeof=\""
					+ product.getAttribute("rdf:about")
					+ " http://purl.org/goodrelations/v1#ProductOrServiceModel\">");
			// gr:name
			out.write("<label for=\"productOrServiceModelName\">Name</label>\n"
					+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#name\" id=\"productOrServiceModelName\" name=\"productOrServiceModelName\" datatype=\"xsd:string\"/><br /><br />");
			// gr:description
			out.write("<label for=\"productOrServiceModelDescription\">Description</label>\n"
					+ "<textarea ows=\"4\" cols=\"50\" property=\"http://purl.org/goodrelations/v1#description\" id=\"productOrServiceModelDescription\" name=\"productOrServiceModelDescription\" datatype=\"xsd:string\"></textarea><br /><br /><br /><br />");
			// gr:hasManufacturer
			// open div
			out.write("<div rel=\"http://purl.org/goodrelations/v1#hasManufacturer\">");
			// gr:BusinessEntity
			// open div
			out.write("<div about=\"base:businessEntity\" typeof=\"http://purl.org/goodrelations/v1#BusinessEntity\">");
			// gr:legalName
			// close div
			// close div
			out.write("<label for=\"manufacturerLegalName\">Manufacturer Legal Name</label>\n"
					+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#legalName\" id=\"manufacturerLegalName\" name=\"manufacturerLegalName\" datatype=\"xsd:string\"/><br /><br /></div></div>");
			// gr:hasBrand
			// open div
			out.write("<div rel=\"http://purl.org/goodrelations/v1#hasBrand\">");
			// gr:BusinessEntity
			// open div
			out.write("<div about=\"base:brand\" typeof=\"http://purl.org/goodrelations/v1#Brand\">");
			// gr:legalName
			// close div
			// close div
			out.write("<label for=\"brand\">Brand Name</label>\n"
					+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#name\" id=\"brand\" name=\"brand\" datatype=\"xsd:string\"/><br /><br /></div></div>");

			// objectProperties
			NodeList nodes = doc.getElementsByTagName("owl:ObjectProperty");

			for (int i = 0; i < nodes.getLength(); i++) {
				if ((nodes.item(i).getParentNode().getNodeName())
						.equals("rdf:RDF")) {
					Element property = (Element) nodes.item(i);
					String label1 = "", label2 = "";
					label1 = ((Element) property.getElementsByTagName(
							"rdfs:label").item(0)).getTextContent();
					// parse label names
					String[] labelParts = label1.split(" ");
					label1 = labelParts[0];
					labelParts = label1.split("(?=[A-Z][^A-Z])");// regex for
																	// splitting
					if (labelParts.length > 0)
						for (int j = 0; j < labelParts.length; j++) {
							if (j == 0)
								labelParts[0] = Character
										.toUpperCase(labelParts[0].charAt(0))
										+ labelParts[0].substring(1);
							label2 += labelParts[j] + " ";
						}
					// gr:objectProperty
					// open div
					out.write("<div rel=\""
							+ ((Element) property.getElementsByTagName(
									"rdfs:subPropertyOf").item(0))
									.getAttribute("rdf:resource") + "\">");
					// gr:BusinessEntity
					// open div
					out.write("<div about=\"base:" + label1 + "\" typeof=\""
							+ property.getAttribute("rdf:about") + "\">");
					// gr:legalName
					// close div
					// close div
					if (((Element) property.getElementsByTagName(
							"rdfs:subPropertyOf").item(0))
							.getAttribute("rdf:resource").toLowerCase()
							.contains("quantitative".toLowerCase())) {

						if (((Element) property.getElementsByTagName(
								"rdfs:range").item(0))
								.getAttribute("rdf:resource").toLowerCase()
								.contains("float".toLowerCase())) {
							out.write("<label for=\""
									+ label1
									+ "\">"
									+ label2
									+ "</label>\n"
									+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#hasValueFloat\" id=\""
									+ label1
									+ "\" name=\""
									+ label1
									+ "\" datatype=\"xsd:string\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Plus\" value=\"+\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0.0';} getElementById('"
									+ label1
									+ "').value = (parseFloat(getElementById('"
									+ label1
									+ "').value) + 0.1).toFixed(1);\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Minus\" value=\"-\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0.0';}getElementById('"
									+ label1
									+ "').value = (parseFloat(getElementById('"
									+ label1
									+ "').value) - 0.1).toFixed(1);\"/>"
									+ "<br /><br /></div></div>");

						} else
							out.write("<label for=\""
									+ label1
									+ "\">"
									+ label2
									+ "</label>\n"
									+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#hasValueInteger\" id=\""
									+ label1
									+ "\" name=\""
									+ label1
									+ "\" datatype=\"xsd:string\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Plus\" value=\"+\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0';}getElementById('"
									+ label1
									+ "').value = parseInt(getElementById('"
									+ label1
									+ "').value) + 1;\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Minus\" value=\"-\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0';}getElementById('"
									+ label1
									+ "').value = parseInt(getElementById('"
									+ label1 + "').value) - 1;\"/>"
									+ "<br /><br /></div></div>");

					} else
						// if(((Element)
						// property.getElementsByTagName("rdfs:subPropertyOf").item(0)).getAttribute("rdf:resource").toLowerCase().contains("qualitative".toLowerCase())){
						// gr:legalName
						// close div
						// close div
						out.write("<label for=\""
								+ label1
								+ "\">"
								+ label2
								+ "</label>\n"
								+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#hasValue\" id=\""
								+ label1
								+ "\" name=\""
								+ label1
								+ "\" datatype=\"xsd:string\"/><br /><br /></div></div>");
					// }
				}
			}
			// datatypeProperties
			nodes = doc.getElementsByTagName("owl:DatatypeProperty");
			for (int i = 0; i < nodes.getLength(); i++) {
				if ((nodes.item(i).getParentNode().getNodeName())
						.equals("rdf:RDF")) {
					Element property = (Element) nodes.item(i);
					String label1 = "", label2 = "";
					label1 = ((Element) property.getElementsByTagName(
							"rdfs:label").item(0)).getTextContent();
					// parse label names
					String[] labelParts = label1.split(" ");
					label1 = labelParts[0];
					labelParts = label1.split("(?=[A-Z][^A-Z])");// regex for
																	// splitting
					if (labelParts.length > 0)
						for (int j = 0; j < labelParts.length; j++) {
							if (j == 0)
								labelParts[0] = Character
										.toUpperCase(labelParts[0].charAt(0))
										+ labelParts[0].substring(1);
							label2 += labelParts[j] + " ";
						}
					System.out.println();
					// gr:objectProperty
					// open div
					out.write("<div rel=\""
							+ ((Element) property.getElementsByTagName(
									"rdfs:subPropertyOf").item(0))
									.getAttribute("rdf:resource") + "\">");
					// gr:BusinessEntity
					// open div
					out.write("<div about=\"base:" + label1 + "\" typeof=\""
							+ property.getAttribute("rdf:about") + "\">");
					if (((Element) property.getElementsByTagName(
							"rdfs:subPropertyOf").item(0))
							.getAttribute("rdf:resource").toLowerCase()
							.contains("quantitative".toLowerCase())) {

						if (((Element) property.getElementsByTagName(
								"rdfs:range").item(0))
								.getAttribute("rdf:resource").toLowerCase()
								.contains("float".toLowerCase())) {
							out.write("<label for=\""
									+ label1
									+ "\">"
									+ label2
									+ "</label>\n"
									+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#hasValueFloat\" id=\""
									+ label1
									+ "\" name=\""
									+ label1
									+ "\" datatype=\"xsd:string\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Plus\" value=\"+\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0.0';}getElementById('"
									+ label1
									+ "').value = (parseFloat(getElementById('"
									+ label1
									+ "').value) + 0.1).toFixed(1);\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Minus\" value=\"-\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0.0';}getElementById('"
									+ label1
									+ "').value = (parseFloat(getElementById('"
									+ label1
									+ "').value) - 0.1).toFixed(1);\"/>"
									+ "<br /><br /></div></div>");

						} else
							out.write("<label for=\""
									+ label1
									+ "\">"
									+ label2
									+ "</label>\n"
									+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#hasValueInteger\" id=\""
									+ label1
									+ "\" name=\""
									+ label1
									+ "\" datatype=\"xsd:string\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Plus\" value=\"+\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0';}getElementById('"
									+ label1
									+ "').value = parseInt(getElementById('"
									+ label1
									+ "').value) + 1;\"/>"
									+ "<input type=\"button\" class=\"btn\" id=\""
									+ label1
									+ "Minus\" value=\"-\" onclick=\"if(getElementById('"
									+ label1
									+ "').value=='') {getElementById('"
									+ label1
									+ "').value = '0';}getElementById('"
									+ label1
									+ "').value = parseInt(getElementById('"
									+ label1 + "').value) - 1;\"/>"
									+ "<br /><br /></div></div>");

					} else
						// if(((Element)
						// property.getElementsByTagName("rdfs:subPropertyOf").item(0)).getAttribute("rdf:resource").toLowerCase().contains("qualitative".toLowerCase())){
						// gr:legalName
						// close div
						// close div
						out.write("<label for=\""
								+ label1
								+ "\">"
								+ label2
								+ "</label>\n"
								+ "<input type=\"text\" property=\"http://purl.org/goodrelations/v1#hasValue\" id=\""
								+ label1
								+ "\" name=\""
								+ label1
								+ "\" datatype=\"xsd:string\"/><br /><br /></div></div>");
					// }
				}
			}

			// close div
			out.write("</div></div></div></div></div>"
					+ "<h3>Price specification</h3>"
					+ "<div rel=\"http://purl.org/goodrelations/v1#hasPriceSpecification\">"
					+ "<div about=\"base:priceSpecification1\" typeof=\"http://purl.org/goodrelations/v1#UnitPriceSpecification\">"
					+ "<label for=\"hasCurrencyValue\">Price</label>"
					+ "<input id=\"hasCurrencyValue\" name=\"hasCurrencyValue\" property=\"http://purl.org/goodrelations/v1#hasCurrencyValue\" datatype=\"xsd:float\" type=\"text\" />"
					+ "<label for=\"hasCurrency\">Currency</label>"
					+ "<input id=\"hasCurrency\" name=\"hasCurrency\" property=\"http://purl.org/goodrelations/v1#hasCurrency\" datatype=\"xsd:string\" type=\"text\" /><br /><br />"
					+ "<label for=\"hasUnitOfMeasurement\">Unit</label>"
					+ "<input id=\"hasUnitOfMeasurement\" name=\"hasUnitOfMeasurement\" property=\"http://purl.org/goodrelations/v1#hasUnitOfMeasurement\" datatype=\"xsd:string\" type=\"text\" /><br /><br />"
					+ "</div></div></div></div>"
					+ "<input class=\"btn\" type=\"submit\"  value=\"Send\" style=\"width: 150px; float: left;\"/>"
					+ "</form></div><br /><br />");
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}

	}

}
