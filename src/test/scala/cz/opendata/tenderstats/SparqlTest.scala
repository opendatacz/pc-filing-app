package cz.opendata.tenderstats

import com.hp.hpl.jena.query.ARQ
import com.hp.hpl.jena.sparql.mgt.Explain
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote
import com.hp.hpl.jena.sparql.util.Context
import com.hp.hpl.jena.update.UpdateFactory
import org.junit._

class SparqlTest {

  @Test
  def testSlow: Unit = {
    println("unit test works")
//    ARQ.setExecutionLogging(Explain.InfoLevel.ALL)
//    val request = UpdateFactory.create(
//      """
//      INSERT DATA
//      {
//        GRAPH <http://localhost/test> {
//          <http://example.com/subject> <http://example.com/predicate> <http://example.com/object> .
//        }
//      }
//      """)
//    val upr = new UpdateProcessRemote(request, "http://192.168.116.130:8890/sparql", Context.emptyContext)
//    upr.execute
  }

}