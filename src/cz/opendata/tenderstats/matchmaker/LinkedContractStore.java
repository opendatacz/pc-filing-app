package cz.opendata.tenderstats.matchmaker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Matej Snoha
 * 
 */
public class LinkedContractStore extends LinkedList<Contract> implements ContractStore {

	private static final long serialVersionUID = -3390922848420034067L;

	public void retain(double minTotalScore, double maxTotalScore) {
		Iterator<Contract> it = iterator();
		while (it.hasNext()) {
			Contract c = it.next();
			double score = c.getTotalScore();
			if (score < minTotalScore || score > maxTotalScore) {
				it.remove();
			}
		}
	}

	public void remove(double minTotalScore, double maxTotalScore) {
		Iterator<Contract> it = iterator();
		while (it.hasNext()) {
			Contract c = it.next();
			double score = c.getTotalScore();
			if (!(score < minTotalScore || score > maxTotalScore)) {
				it.remove();
			}
		}
	}

	public void retain(Comparer comparer, double minScore, double maxScore) {
		Iterator<Contract> it = iterator();
		while (it.hasNext()) {
			Contract c = it.next();
			double score = c.getScore(comparer);
			if (score < minScore || score > maxScore) {
				it.remove();
			}
		}
	}

	public void remove(Comparer comparer, double minScore, double maxScore) {
		Iterator<Contract> it = iterator();
		while (it.hasNext()) {
			Contract c = it.next();
			double score = c.getScore(comparer);
			if (!(score < minScore || score > maxScore)) {
				it.remove();
			}
		}
	}

	public void retain(Collection<Comparer> comparers, double minScore, double maxScore) {
		Iterator<Contract> it = iterator();
		while (it.hasNext()) {
			Contract c = it.next();
			for (Comparer comparer : comparers) {
				double score = c.getScore(comparer);
				if (score < minScore || score > maxScore) {
					it.remove();
					break;
				}
			}
		}
	}

	public void remove(Collection<Comparer> comparers, double minScore, double maxScore) {
		Iterator<Contract> it = iterator();
		while (it.hasNext()) {
			Contract c = it.next();
			for (Comparer comparer : comparers) {
				double score = c.getScore(comparer);
				if (!(score < minScore || score > maxScore)) {
					it.remove();
					break;
				}
			}
		}
	}

	public void sort() {
		Collections.sort(this);
	}

	public void sortDescending() {
		Collections.sort(this, Collections.reverseOrder());
	}

	public Collection<Comparer> getComparers() {
		if (!isEmpty()) {
			return getFirst().getScores().keySet();
		} else {
			return new HashSet<>();
		}
	}
}
