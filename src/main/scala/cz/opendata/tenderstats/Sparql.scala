package cz.opendata.tenderstats

import com.hp.hpl.jena.query.QueryExecutionFactory

object Sparql {

  def privateUpdate(q: String) = QueryExecutionFactory.sparqlService(Config.cc.getSparqlPrivateUpdate, q)
  def privateQuery(q: String) = QueryExecutionFactory.sparqlService(Config.cc.getSparqlPrivateQuery, q)
  def publicUpdate(q: String) = QueryExecutionFactory.sparqlService(Config.cc.getSparqlPublicUpdate, q)
  def publicQuery(q: String) = QueryExecutionFactory.sparqlService(Config.cc.getSparqlPublicQuery, q)

}
