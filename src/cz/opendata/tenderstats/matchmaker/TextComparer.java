package cz.opendata.tenderstats.matchmaker;

import org.apache.commons.configuration.Configuration;

import com.hp.hpl.jena.rdf.model.ResourceFactory;

import com.wcohen.ss.JaroWinklerTFIDF;
import com.wcohen.ss.SoftTFIDF;
import com.wcohen.ss.api.StringDistance;

import static cz.opendata.tenderstats.Prefixes.*;

/**
 * @author Matej Snoha
 * 
 */
public class TextComparer extends AbstractComparer {

	protected StringDistance stringDistance = new JaroWinklerTFIDF();

	public TextComparer() {
		((SoftTFIDF) stringDistance).setTokenMatchThreshold(getConfiguration().getDouble("wordSimilarityThreshold", 0.8));
	}

	public TextComparer(Configuration config) {
		super(config);
		((SoftTFIDF) stringDistance).setTokenMatchThreshold(getConfiguration().getDouble("wordSimilarityThreshold", 0.8));
	}

	static double max(double... values) {
		double max = Float.MIN_VALUE;
		for (double value : values) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	static double avg(double... values) {
		double sum = 0;
		for (double value : values) {
			sum += value;
		}
		return sum / values.length;
	}

	@Override
	public VerboseResult compareVerbose(Contract base, Contract other) {
		try {
			String baseTitle =
					base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(DC, "title")).getString();
			String otherTitle =
					other.rdf.getProperty(ResourceFactory.createResource(other.getUrl()),
							ResourceFactory.createProperty(DC, "title")).getString();
			// if (baseTitle.length() >= 4 && baseTitle.equals(otherTitle)) {
			// return 1; // if titles match, it is ideal
			// }

			String baseDescription =
					base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(DC, "description")).getString();
			String otherDescription =
					other.rdf.getProperty(ResourceFactory.createResource(other.getUrl()),
							ResourceFactory.createProperty(DC, "description")).getString();

			// if (stringDistance.score(baseTitle, otherTitle) > 0) {
			// System.out.println(// ((SoftTFIDF) stringDistance).getTokenMatchThreshold() + " :: "
			// /* + */stringDistance.explainScore(baseTitle, otherTitle));
			// }

			double rawScoreMultiplier = getConfiguration().getDouble("rawScoreMultiplier", 4);
			double score =
					rawScoreMultiplier
							* stringDistance.score(baseTitle + " " + baseDescription, otherTitle + " " + otherDescription);
			String message =
					stringDistance.explainScore(baseTitle + " " + baseDescription, otherTitle + " " + otherDescription)
							.replaceAll("Common tokens: ", "").replaceAll("score.*", "").trim();
			if (message.isEmpty()) {
				message = "no similar words";
			}

			return new VerboseResult(Math.min(1, score), message);
		} catch (Exception e) {
			return new VerboseResult(0, "missing or corrupted data");
		}
	}
}
