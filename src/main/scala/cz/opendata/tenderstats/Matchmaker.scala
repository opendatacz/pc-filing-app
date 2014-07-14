package cz.opendata.tenderstats

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.client.ClientBuilder
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONArray
import scala.util.parsing.json.JSONObject

@WebServlet(Array("/Matchmaker"))
class Matchmaker extends AbstractComponent {

  override def doGetPost(request: HttpServletRequest, response: HttpServletResponse) = {
    ((request.getParameter("source"), request.getParameter("target")), request.getParameter("uri")) match {
      case (Matchmaker.EndpointUri(endpointUri), uri) if !uri.isEmpty => {
        import JsonParser._
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter.print(
          JSONArray(
            JSON.parseRaw(
              Matchmaker
                .client
                .target(endpointUri)
                .queryParam("uri", uri)
                .request("application/ld+json")
                .get(classOf[String])).getOrElse(JSONArray(Nil)) \ "member" match {
                case Some(JSONArray(x)) => x collect {
                  case x: JSONObject => List(x \ "@id", x \ "vrank:hasValue", x \|\ ("dcterms:title", "gr:legalName"))
                } collect {
                  case x @ List(Some(_: String), Some(_: Double), Some(_: String)) => JSONObject(List("URI", "score", "label").zip(x map (_.get)).toMap)
                }
                case _ => Nil
              }))
      }
      case _ => response.sendError(400)
    }
  }

}

object Matchmaker {

  val client = ClientBuilder.newClient

  val List(
    matchBusinessEntityToContractUrl,
    matchContractToBusinessEntityUrl,
    matchContractToContractUrl,
    loadContractUrl,
    loadBusinessEntityUrl
    ) = {
    val endpoint = (Config.matchmaker \\ "endpoint").text
    List(
      (Config.matchmaker \\ "business-entity-to-contract").text,
      (Config.matchmaker \\ "contract-to-business-entity").text,
      (Config.matchmaker \\ "contract-to-contract").text,
      (Config.matchmaker \\ "contract").text,
      (Config.matchmaker \\ "business-entity").text) map (endpoint + _)
  }

  object IsContract {
    def unapply(t: String) = t == "contract"
  }
  object IsBusinessEntity {
    def unapply(t: String) = t == "business-entity"
  }

  object EndpointUri {
    def unapply(t: (String, String)) = t match {
      case (Matchmaker.IsContract(), Matchmaker.IsContract()) => Some(Matchmaker.matchContractToContractUrl)
      case (Matchmaker.IsContract(), Matchmaker.IsBusinessEntity()) => Some(Matchmaker.matchContractToBusinessEntityUrl)
      case (Matchmaker.IsBusinessEntity(), Matchmaker.IsContract()) => Some(Matchmaker.matchBusinessEntityToContractUrl)
      case _ => None
    }
  }

}