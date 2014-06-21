package cz.opendata.tenderstats.matchmaker;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.configuration.Configuration;

import com.hp.hpl.jena.rdf.model.ResourceFactory;

import static cz.opendata.tenderstats.Prefixes.*;

/**
 * @author Matej Snoha
 * 
 */
public class TenderDeadlineComparer extends AbstractComparer {

	public TenderDeadlineComparer() {
	}

	public TenderDeadlineComparer(Configuration config) {
		super(config);
	}

	@Override
	public VerboseResult compareVerbose(Contract base, Contract other) {
		try {
			String baseDateTime = null;
			if (base.rdf.contains(ResourceFactory.createResource(base.getUrl()),
					ResourceFactory.createProperty(PC, "tenderDeadline"))) {
				baseDateTime =
						base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
								ResourceFactory.createProperty(PC, "tenderDeadline")).getString();
			}
			long baseTime;
			if (baseDateTime != null) {
				try {
					baseTime = DatatypeConverter.parseDateTime(baseDateTime).getTimeInMillis() / 1000;
				} catch (Exception e) {
					return new VerboseResult(0, "no tender deadline for base contract");
				}
			} else {
				return new VerboseResult(0, "no tender deadline for base contract");
			}

			String otherDateTime = null;
			if (other.rdf.contains(ResourceFactory.createResource(other.getUrl()),
					ResourceFactory.createProperty(PC, "tenderDeadline"))) {
				otherDateTime =
						other.rdf.getProperty(ResourceFactory.createResource(other.getUrl()),
								ResourceFactory.createProperty(PC, "tenderDeadline")).getString();
				// System.out.println(otherDateTime);
			}
			long otherTime;
			if (otherDateTime != null) {
				try {
					otherTime = DatatypeConverter.parseDateTime(otherDateTime).getTimeInMillis() / 1000;
				} catch (Exception e) {
					return new VerboseResult(0, "missing or corrupted data");
				}
			} else {
				return new VerboseResult(0, "missing or corrupted data");
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
