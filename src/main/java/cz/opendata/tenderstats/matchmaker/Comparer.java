package cz.opendata.tenderstats.matchmaker;

import java.util.Collection;

import org.apache.commons.configuration.Configuration;

/**
 * @author Matej Snoha
 * 
 */
public interface Comparer {

	public class VerboseResult {

		public final double score;
		public final String message;

		public VerboseResult(double score, String message) {
			this.score = score;
			this.message = message;
		}
	}

	public double getWeight();

	public void setWeight(double weight);

	public String getName();

	public Comparer addConfiguration(Configuration config);

	public Comparer addConfiguration(Configuration config, boolean filterRelevantItems);

	public void run(Contract base, Contract other);

	public void runVerbose(Contract base, Contract other);

	public void run(Contract base, Collection<Contract> others);

	public void runVerbose(Contract base, Collection<Contract> others);

	public double compare(Contract base, Contract other);

	public VerboseResult compareVerbose(Contract base, Contract other);

}
