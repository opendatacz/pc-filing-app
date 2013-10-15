package cz.opendata.tenderstats.matchmaker;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import static cz.opendata.tenderstats.Prefixes.*;

/**
 * @author Matej Snoha
 * 
 */
public class CPVComparer extends AbstractComparer {

	public CPVComparer() {
	}

	public CPVComparer(Configuration config) {
		super(config);
	}

	@Override
	public VerboseResult compareVerbose(Contract base, Contract other) {
		try {
			Set<String> baseCpvs = new HashSet<>();
			Set<String> otherCpvs = new HashSet<>();

			String baseMainCpv =
					base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(PC, "mainObject")).getResource().toString();
			baseMainCpv = baseMainCpv.substring(baseMainCpv.lastIndexOf('/') + 1);

			String otherMainCpv =
					other.rdf
							.getProperty(ResourceFactory.createResource(other.getUrl()),
									ResourceFactory.createProperty(PC, "mainObject")).getResource().toString();
			otherMainCpv = otherMainCpv.substring(otherMainCpv.lastIndexOf('/') + 1);

			if (baseMainCpv.equals(otherMainCpv) && getConfiguration().getBoolean("maxScoreForSameMainCPVs", false)) {
				return new VerboseResult(1, "main cpvs exact match");
			}

			int cpvLength = getConfiguration().getInt("cpvLength", 4);
			baseCpvs.add(baseMainCpv.substring(0, cpvLength));
			otherCpvs.add(otherMainCpv.substring(0, cpvLength));

			NodeIterator it =
					base.rdf.listObjectsOfProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(PC, "additionalObject"));
			while (it.hasNext()) {
				for (RDFNode additionalCpvNode : it.toSet()) {
					String additionalCpv = additionalCpvNode.asResource().toString();
					baseCpvs.add(additionalCpv.substring(additionalCpv.lastIndexOf('/') + 1, additionalCpv.lastIndexOf('/')
							+ cpvLength + 1));
				}
			}

			it =
					other.rdf.listObjectsOfProperty(ResourceFactory.createResource(other.getUrl()),
							ResourceFactory.createProperty(PC, "additionalObject"));
			while (it.hasNext()) {
				for (RDFNode additionalCpvNode : it.toSet()) {
					String additionalCpv = additionalCpvNode.asResource().toString();
					otherCpvs.add(additionalCpv.substring(additionalCpv.lastIndexOf('/') + 1, additionalCpv.lastIndexOf('/')
							+ cpvLength + 1));
				}
			}

			StringBuilder message = new StringBuilder();

			message.append("base: ");
			for (String cpv : baseCpvs) {
				message.append(cpv);
				message.append(" ");
			}

			message.append("other: ");
			for (String cpv : otherCpvs) {
				message.append(cpv);
				message.append(" ");
			}

			baseCpvs.retainAll(otherCpvs); // baseCpvs now contains set intersection of cpvs
			double score1Match = getConfiguration().getDouble("score1Match", 0.7);
			double scoreAdditionalMatch = getConfiguration().getDouble("scoreAdditionalMatch", 0.2);
			double score = Math.min(1, (baseCpvs.size() == 0 ? 0 : score1Match + scoreAdditionalMatch * (baseCpvs.size() - 1)));

			message.append("# of similar cpvs : ");
			message.append(baseCpvs.size());
			message.append(" ( ");
			for (String cpv : baseCpvs) {
				message.append(cpv);
				message.append(" ");
			}
			message.append(")");
			return new VerboseResult(score, message.toString());
		} catch (Exception e) {
			return new VerboseResult(0, "missing or corrupted data");
		}
	}
}
