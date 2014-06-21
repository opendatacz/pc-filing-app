package cz.opendata.tenderstats.matchmaker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Matej Snoha
 * 
 */
public class LinkedHashContractStore extends LinkedHashSet<Contract> implements ContractStore {

	private static final long serialVersionUID = 172569727517297314L;

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
		List<Contract> list = new ArrayList<>(this);
		java.util.Collections.sort(list);
		this.clear();
		this.addAll(list);
	}

	public void sortDescending() {
		List<Contract> list = new ArrayList<>(this);
		Collections.sort(list, Collections.reverseOrder());
		this.clear();
		this.addAll(list);
	}

	public Collection<Comparer> getComparers() {
		if (!isEmpty()) {
			return iterator().next().getScores().keySet();
		} else {
			return new HashSet<>();
		}
	}
}
