package cz.opendata.tenderstats.matchmaker;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.configuration.Configuration;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import static cz.opendata.tenderstats.Prefixes.*;

/**
 * @author Matej Snoha
 * 
 */
public class PublicationDateComparer extends AbstractComparer {

	public PublicationDateComparer() {
	}

	public PublicationDateComparer(Configuration config) {
		super(config);
	}

	@Override
	public VerboseResult compareVerbose(Contract base, Contract other) {
		try {
			String baseDate = null;
			if (base.rdf.contains(ResourceFactory.createResource(base.getUrl()), ResourceFactory.createProperty(PC, "notice"))) {
				Resource baseDateResource =
						base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
								ResourceFactory.createProperty(PC, "notice")).getResource();
				// System.out.println(baseDateResource.getURI());
				if (base.rdf.contains(baseDateResource, ResourceFactory.createProperty(PC, "publicationDate"))) {
					baseDate =
							base.rdf.getProperty(baseDateResource, ResourceFactory.createProperty(PC, "publicationDate"))
									.getString();
				}
			}
			long baseTime;
			if (baseDate != null) {
				try {
					baseTime = DatatypeConverter.parseDate(baseDate).getTimeInMillis() / 1000;
				} catch (Exception unused) {
					// System.err.println("no publication date for base contract, using current");
					baseTime = System.currentTimeMillis() / 1000; // current time for not yet published
				}
			} else {
				// System.err.println("no publication date for base contract, using current");
				baseTime = System.currentTimeMillis() / 1000; // current time for not yet published
				// return 0; // not parseable date ?
			}

			String otherDate = null;
			if (other.rdf.contains(ResourceFactory.createResource(other.getUrl()), ResourceFactory.createProperty(PC, "notice"))) {
				Resource otherDateResource =
						other.rdf.getProperty(ResourceFactory.createResource(other.getUrl()),
								ResourceFactory.createProperty(PC, "notice")).getResource();
				if (other.rdf.contains(otherDateResource, ResourceFactory.createProperty(PC, "publicationDate"))) {
					otherDate =
							other.rdf.getProperty(otherDateResource, ResourceFactory.createProperty(PC, "publicationDate"))
									.getString();
				}
			}
			long otherTime;
			if (otherDate != null) {
				try {
					otherTime = DatatypeConverter.parseDate(otherDate).getTimeInMillis() / 1000;
				} catch (Exception e) {
					return new VerboseResult(0, "no publication date");
				}
			} else {
				return new VerboseResult(0, "no publication date");
			}

			double secondsInYear = 31556926; // average year
			double yearsBetween = Math.abs((otherTime - baseTime) / secondsInYear);
			double maxDifference = getConfiguration().getDouble("maxDifference", 10);
			return new VerboseResult(1 - Math.min(1, yearsBetween / maxDifference), "difference of "
					+ (int) (yearsBetween * 1000) / 1000f + " years");
		} catch (Exception e) {
			return new VerboseResult(0, "missing or corrupted data");
		}
	}
}
