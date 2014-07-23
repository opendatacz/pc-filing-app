package cz.opendata.tenderstats

import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote
import com.hp.hpl.jena.update.UpdateFactory
import com.hp.hpl.jena.sparql.util.Context
import org.apache.log4j.LogManager

object Sparql {

  private val logger = LogManager.getLogger("Sparql")
  import logger._

  def privateUpdate(q: String) = {
    debug("Sparql private update:\n" + q)
    new UpdateProcessRemote(UpdateFactory.create(q), Config.cc.getSparqlPrivateUpdate, Context.emptyContext)
  }
  def privateQuery(q: String) = {
    debug("Sparql private query:\n" + q)
    QueryExecutionFactory.sparqlService(Config.cc.getSparqlPrivateQuery, q)
  }
  def publicUpdate(q: String) = {
    debug("Sparql public update:\n" + q)
    new UpdateProcessRemote(UpdateFactory.create(q), Config.cc.getSparqlPublicUpdate, Context.emptyContext)
  }
  def publicQuery(q: String) = {
    debug("Sparql public query:\n" + q)
    QueryExecutionFactory.sparqlService(Config.cc.getSparqlPublicQuery, q)
  }

  def template(name: String, m: Map[String, Any] = Map.empty) = {
    Template(this.getClass.getResource(s"/cz/opendata/tenderstats/sparql/${name}"), m + ("prefixes" -> Config.prefixes))
  }

}
