package cz.opendata.tenderstats

import cz.opendata.tenderstats.utils.AutoLift
import cz.opendata.tenderstats.utils.Lift
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.client.ClientBuilder
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONArray
import scala.util.parsing.json.JSONObject
import scala.util.parsing.json.JSONType

@WebServlet(Array("/Matchmaker"))
class Matchmaker extends AbstractComponent {

  override def doGetPost(request: HttpServletRequest, response: HttpServletResponse) = {
    import cz.opendata.tenderstats.utils.BasicExtractor._
    import Matchmaker._
    response.setContentType("application/json; charset=UTF-8")
    val endpointUri = AutoLift(request.getParameter("source"), request.getParameter("target")) {
      case (EntityType(s), EntityType(t)) => (s, t, AutoLift(s, t) {
        case (Contract, Contract) => (matchContractToContractUrl)
        case (Contract, BusinessEntity) => matchContractToBusinessEntityUrl
        case (BusinessEntity, Contract) => matchBusinessEntityToContractUrl
      })
    }
    (endpointUri, request.getParameter("uri"), request.getParameter("private")) match {
      case (Some((_, t, Some(e))), NonEmptyString(u), Boolean(p)) => response.getWriter.print(sendGet(e, u).extend(t).toJson)
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

  def sendGet(endpointUri: String, entityUri: String) = GetResponse(JSON.parseRaw(client.target(endpointUri).queryParam("uri", entityUri).queryParam("limit", "100").request("application/ld+json").get(classOf[String])))

  class GetResponse(val vals: List[Map[String, String]]) {
    def extend(t: EntityType) = {
      import cz.opendata.tenderstats.utils.QuerySolutionOpts._
      import scala.collection.JavaConversions._
      new GetResponse(
        (
          t match {
            case Contract => this.getClass.getResource("/cz/opendata/tenderstats/sparql/contract_results_enrichment.mustache")
            case BusinessEntity => this.getClass.getResource("/cz/opendata/tenderstats/sparql/business_entity_results_enrichment.mustache")
          },
          Map(
            "source-graph" -> Config.cc.getPreference("publicGraphName"),
            "results" -> vals)) match {
              case Template(q) => Sparql.publicQuery(q).execSelect.toList.map(x => x.toMap)
              case _ => vals
            })
    }
    def toJson = JSONArray(vals map JSONObject)
  }
  object GetResponse {
    def apply(j: Option[JSONType]) = {
      import cz.opendata.tenderstats.utils.JsonParser._
      new GetResponse(
        Lift(j) {
          case Some(x) => AutoLift(x \ "member") {
            case Some(JSONArray(x)) => x collect {
              case x: JSONObject => List(x \ "@id", x \ "vrank:hasValue", x \|\ ("dcterms:title", "gr:legalName"))
            } collect {
              case x @ List(Some(_: String), Some(_: Double), Some(_: String)) => List("uri", "score", "label").zip(x map (_.get.toString)).toMap
            }
          }
        } getOrElse Nil)
    }
  }

  sealed trait EntityType
  object Contract extends EntityType
  object BusinessEntity extends EntityType
  object EntityType {
    def unapply(s: String) = AutoLift(s) {
      case "contract" => Contract
      case "business-entity" => BusinessEntity
    }
  }

}