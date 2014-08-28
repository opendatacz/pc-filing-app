package cz.opendata.tenderstats.admin;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.Filter;
import java.util.*;

public class ClassHierarchy {

	protected OntModel m_model;
	private String[] classes = new String[100000];
	private String[] properties = new String[100000];
	private ObjectProperty[] objectProperties;
	private DatatypeProperty[] datatypeProperties;
	private int j;

	/** Show the sub-class hierarchy encoded by the given model */
	public String[] generateHierarchy(OntModel m) {
		// create an iterator over the root classes that are not anonymous class
		// expressions
		Iterator<OntClass> i = m.listHierarchyRootClasses().filterDrop(
				new Filter<OntClass>() {
					@Override
					public boolean accept(OntClass r) {
						return r.isAnon();
					}
				});
		j = 0;
		while (i.hasNext()) {
			try {
				OntClass temp = i.next();
				if (temp.getURI()
						.toString()
						.equals("http://purl.org/goodrelations/v1#ProductOrService")) {
					showClass(temp, new ArrayList<OntClass>(), 0);
					j++;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String[] shortClasses = new String[j];
		for (int tem = 0; tem < j; tem++)
			shortClasses[tem] = classes[tem];
		return shortClasses;
	}

	/** Return all datatypeProperties from ontology as String array */
	public String[] getDatatypeProperties(OntModel m) {

		Iterator<DatatypeProperty> i = m.listDatatypeProperties();
		j = 0;
		while (i.hasNext()) {
			try {
				DatatypeProperty temp = i.next();

				if (temp.getSuperProperty() != null
						&& (temp.getSuperProperty().getURI().toString()
								.contains("http://purl.org/goodrelations/v1#datatypeProductOrServiceProperty"))
						&& !temp.getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")
						&& temp.getDomain() != null
				/* && temp.getDomain().getURI().equals(className) */) {
					properties[j] = temp.getURI();
					j++;
				}
				/*
				 * if ((temp.getSuperProperty() != null &&
				 * temp.getSuperProperty() .getURI().toString()
				 * .contains("http://purl.org/goodrelations/v1#")) ||
				 * temp.getURI().toString()
				 * .contains("http://purl.org/goodrelations/v1#")) { classes[j]
				 * = (temp.getURI().toString().substring(temp
				 * .getURI().toString().indexOf("#") + 1, temp
				 * .getURI().toString().length())); j++; }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String[] results = new String[j];
		for (int tem = 0; tem < j; tem++) {
			results[tem] = properties[tem];
		}
		Arrays.sort(results, java.text.Collator.getInstance());
		return results;
	}

	/** Return specified datatypeProperties from ontology */
	public DatatypeProperty[] getDatatypePropertiesDefined(OntModel m,
			String[] values) {
		datatypeProperties = new DatatypeProperty[values.length];
		Iterator<DatatypeProperty> i = m.listDatatypeProperties();
		j = 0;
		while (i.hasNext()) {
			try {
				DatatypeProperty temp = i.next();
				if (temp.getSuperProperty() != null
						&& temp.getSuperProperty().getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")
						&& !temp.getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")) {
					if (Arrays.asList(values).contains(temp.getURI())) {
						datatypeProperties[j] = temp;
						j++;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return datatypeProperties;
	}

	/** Return all datatypeProperties from ontology as DatatypeProperty array*/
	public DatatypeProperty[] getDatatypePropertiesAll(String className, OntModel m) {

		Iterator<DatatypeProperty> i = m.listDatatypeProperties();
		datatypeProperties = new DatatypeProperty[100];
		j = 0;
		while (i.hasNext()) {
			try {
				DatatypeProperty temp = i.next();

				if (temp.getSuperProperty() != null
						&& (temp.getSuperProperty().getURI().toString()
								.contains("http://purl.org/goodrelations/v1#datatypeProductOrServiceProperty"))
						&& !temp.getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")
						&& temp.getDomain() != null
				/* && temp.getDomain().getURI().equals(className) */) {
					datatypeProperties[j] = temp;
					j++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return datatypeProperties;
	}

	/** Return all objectProperties from ontology as String array */	
	public String[] getObjectProperties(String className, OntModel m) {
		Iterator<ObjectProperty> i = m.listObjectProperties();

		j = 0;
		while (i.hasNext()) {
			try {
				ObjectProperty temp = i.next();
				if (temp.getSuperProperty() != null
						&& (temp.getSuperProperty()
								.getURI()
								.toString()
								.contains(
										"http://purl.org/goodrelations/v1#quantitativeProductOrServiceProperty") || temp
								.getSuperProperty()
								.getURI()
								.toString()
								.contains(
										"http://purl.org/goodrelations/v1#qualitativeProductOrServiceProperty"))
						&& !temp.getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")
						&& temp.getDomain() != null
				/* && temp.getDomain().getURI().equals(className) */) {
					properties[j] = temp.getURI();
					j++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String[] results = new String[j];
		for (int tem = 0; tem < j; tem++) {
			results[tem] = properties[tem];
		}
		Arrays.sort(results, java.text.Collator.getInstance());
		return results;
	}

	/** Return specified objectProperties from ontology */
	public ObjectProperty[] getObjectPropertiesDefined(OntModel m,
			String[] values) {
		objectProperties = new ObjectProperty[values.length];
		Iterator<ObjectProperty> i = m.listObjectProperties();

		j = 0;
		while (i.hasNext()) {
			try {
				ObjectProperty temp = i.next();
				if (temp.getSuperProperty() != null
						&& temp.getSuperProperty().getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")
						&& !temp.getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")) {
					if (Arrays.asList(values).contains(temp.getURI())) {
						objectProperties[j] = temp;
						j++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return objectProperties;
	}

	/** Return all datatypeProperties from ontology as ObjectProperty array*/
	public ObjectProperty[] getObjectPropertiesAll(String className, OntModel m) {

		Iterator<ObjectProperty> i = m.listObjectProperties();
		objectProperties = new ObjectProperty[100];
		j = 0;
		while (i.hasNext()) {
			try {
				ObjectProperty temp = i.next();
				if (temp.getSuperProperty() != null
						&& (temp.getSuperProperty()
								.getURI()
								.toString()
								.contains(
										"http://purl.org/goodrelations/v1#quantitativeProductOrServiceProperty") || temp
								.getSuperProperty()
								.getURI()
								.toString()
								.contains(
										"http://purl.org/goodrelations/v1#qualitativeProductOrServiceProperty"))
						&& !temp.getURI().toString()
								.contains("http://purl.org/goodrelations/v1#")
						&& temp.getDomain() != null
				/* && temp.getDomain().getURI().equals(className) */) {
					objectProperties[j] = temp;
					j++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return objectProperties;
	}

	/**
	 * Present a class, then recurse down to the sub-classes. Use occurs check
	 * to prevent getting stuck in a loop
	 */
	private void showClass(OntClass cls, List<OntClass> occurs, int depth) {
		classes[j] = renderClassDescription(classes[j], cls, depth);
		// classes[j] = (cls.getURI());
		j++;
		// recurse to the next level down
		if (cls.canAs(OntClass.class) && !occurs.contains(cls)) {
			for (Iterator<OntClass> i = cls.listSubClasses(true); i.hasNext();) {
				OntClass sub = i.next();
				// we push this expression on the occurs list before we recurse
				occurs.add(cls);
				if (!sub.getURI().toString().contains("goodrelations"))
					showClass(sub, occurs, depth + 1);
				occurs.remove(cls);
			}
		}
	}

	public String renderClassDescription(String className, OntClass c, int depth) {
		className = indent(depth);
		// className = renderURI(className, c.getModel(), c.getURI());
		className += c.getURI();
		return className;
	}

	public String renderPropertyDescription(String propertyName,
			ObjectProperty op, int depth) {
		propertyName = indent(depth);
		propertyName = renderURI(propertyName, op.getModel(), op.getURI());

		return propertyName;
	}

	/** Render a URI */
	protected String renderURI(String name, PrefixMapping prefixes, String uri) {
		name += prefixes.shortForm(uri);
		return name;
	}

	/** Generate the indentation */
	protected String indent(int depth) {

		String prefix = "";

		for (int i = 1; i < depth; i++) {
			prefix += " -- ";
		}
		return prefix;
	}

}