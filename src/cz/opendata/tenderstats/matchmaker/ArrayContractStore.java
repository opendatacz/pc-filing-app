package cz.opendata.tenderstats.matchmaker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Matej Snoha
 * 
 */
public class ArrayContractStore extends ArrayList<Contract> implements ContractStore {

	private static final long serialVersionUID = 5756058902127824909L;

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
			return get(0).getScores().keySet();
		} else {
			return new HashSet<>();
		}
	}
}
