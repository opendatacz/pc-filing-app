package cz.opendata.tenderstats

import com.github.jsonldjava.core.JsonLdOptions
import com.github.jsonldjava.core.JsonLdProcessor
import com.github.jsonldjava.utils.JsonUtils
import cz.opendata.tenderstats.utils.Lift
import cz.opendata.tenderstats.utils.NonEmptyString
import java.io.ByteArrayOutputStream
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import org.apache.log4j.LogManager

@WebServlet(Array("/NumberOfBidders"))
class NumberOfBidders extends AbstractComponent {

  override def doGetPost(request: HttpServletRequest, response: HttpServletResponse) = {
    response.setContentType("application/json; charset=UTF-8")
    Lift(request.getParameter("uri"), getUserContext(request)) {
      case (NonEmptyString(contractURI), uc: UserContext) => NumberOfBidders.sendPost(contractURI, uc.getNamedGraph)
    } match {
      case Some(x) => response.getWriter.print(x)
      case None => response.sendError(400)
    }
  }

}

object NumberOfBidders {

  val client = ClientBuilder.newClient
  val logger = LogManager.getLogger("NumberOfBidders")
  val jsonLdFrame = JsonUtils.fromURL(getClass.getResource("/cz/opendata/tenderstats/config/contract-frame.json"))
  val jsonLdOptions = {
    val options = new JsonLdOptions
    options.setUseNativeTypes(true)
    options
  }

  import logger._

  val endpoint = (Config.numberOfBidders \\ "endpoint").text

  def sendPost(uri: String, graph: String) = {
    val baos = new ByteArrayOutputStream
    debug(s"Sending POST to: $endpoint with entity $uri from graph $graph")
    try {
      Sparql
        .privateQuery(Sparql.template("resource_description_extended.mustache", Map("source-graph" -> graph, "resource" -> uri)))
        .execConstruct
        .write(baos, "JSONLD")
      val x = JsonUtils.toPrettyString(JsonLdProcessor.frame(JsonUtils.fromString(baos.toString("UTF-8")), jsonLdFrame, jsonLdOptions))
      debug(s"POST body content is:\n$x")
      val rs = client.target(endpoint).request.post(Entity.entity(x, "application/ld+json"), classOf[String])
      debug(s"POST response is:\n$rs")
      Some(rs)
    } catch {
      case e: Throwable => {
        warn(e, e)
        None
      }
    } finally {
      baos.close
    }
  }

}