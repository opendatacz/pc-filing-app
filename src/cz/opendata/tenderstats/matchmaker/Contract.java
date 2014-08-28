package cz.opendata.tenderstats.matchmaker;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

/**
 * @author Matej Snoha
 * 
 */
public class Contract implements Comparable<Contract> {

	protected String url;

	protected Model rdf;

	protected Map<Comparer, Double> scores;
	protected Map<Comparer, String> messages;

	public double getTotalScore() {
		double result = 0;
		for (Comparer comparer : scores.keySet()) {
			result += getScore(comparer) * comparer.getWeight();
		}
		return result;
	};

	@Override
	public int compareTo(Contract other) {
		return ((Double) getTotalScore()).compareTo(other.getTotalScore());
	}

	public Contract(String url) {
		this(url, new ModelCom(GraphFactory.createPlainGraph()));
	}

	public Contract(String url, Model rdf) {
		this.url = url;
		this.rdf = rdf;
		this.scores = new HashMap<>();
		this.messages = new HashMap<>();
	}

	@Override
	public String toString() {
		return url;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(url).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Contract other = (Contract) obj;
		return new EqualsBuilder().append(url, other.url).isEquals();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Model getRdf() {
		return rdf;
	}

	public void setRdf(Model rdf) {
		this.rdf = rdf;
	}

	public Map<Comparer, Double> getScores() {
		return scores;
	}

	public Map<Comparer, String> getMessages() {
		return messages;
	}

	public boolean containsScore(Comparer comparer) {
		return scores.containsKey(comparer.getName());
	}

	public boolean containsMessage(Comparer comparer) {
		return messages.containsKey(comparer.getName());
	}

	public double getScore(Comparer comparer) {
		return scores.get(comparer);
	}

	public String getMessage(Comparer comparer) {
		return messages.get(comparer);
	}

	public void setScore(Comparer comparer, double score) {
		scores.put(comparer, score);
	}

	public void setMessage(Comparer comparer, String message) {
		messages.put(comparer, message);
	}

	public void appendMessage(Comparer comparer, String message) {
		appendMessage(comparer, message, " ");
	}

	public void appendMessage(Comparer comparer, String message, String delimiter) {
		if (containsMessage(comparer)) {
			setMessage(comparer, getMessage(comparer) + delimiter + message);
		} else {
			setMessage(comparer, message);
		}
	}
}
