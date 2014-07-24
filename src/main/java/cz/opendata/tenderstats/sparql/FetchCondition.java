package cz.opendata.tenderstats.sparql;

import com.hp.hpl.jena.query.QuerySolution;

/**
 *
 * @author venca
 */
public interface FetchCondition {

    boolean isValid(QuerySolution qs);

}
