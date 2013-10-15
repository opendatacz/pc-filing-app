package cz.opendata.tenderstats.matchmaker;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Matej Snoha
 * 
 */
public interface ContractStore extends Collection<Contract>, Serializable {

	public void retain(double minTotalScore, double maxTotalScore);

	public void remove(double minTotalScore, double maxTotalScore);

	public void retain(Comparer comparer, double minScore, double maxScore);

	public void remove(Comparer comparer, double minScore, double maxScore);

	public void retain(Collection<Comparer> comparers, double minScore, double maxScore);

	public void remove(Collection<Comparer> comparers, double minScore, double maxScore);

	public void sort();

	public void sortDescending();

	public Collection<Comparer> getComparers();

}
