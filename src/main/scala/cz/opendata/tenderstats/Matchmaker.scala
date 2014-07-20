package cz.opendata.tenderstats

import cz.opendata.tenderstats.utils.AutoLift
import cz.opendata.tenderstats.utils.Lift
import cz.opendata.tenderstats.utils.NonEmptyString
import cz.opendata.tenderstats.utils.Boolean
import java.io.ByteArrayOutputStream
import java.net.URLDecoder
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import scala.util.Try
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONArray
import scala.util.parsing.json.JSONObject
import scala.util.parsing.json.JSONType

@WebServlet(Array("/Matchmaker"))
class Matchmaker extends AbstractComponent {

  override def doGetPost(request: HttpServletRequest, response: HttpServletResponse) = {
    import Matchmaker._
    //DELETE IN PROD VERSION
    val testUser = new UserContext()
    testUser.setNamedGraph("http://ld.opendata.cz/tenderstats/namedgraph/demo")
    //END
    response.setContentType("application/json; charset=UTF-8")
    val endpointUri = AutoLift(request.getParameter("source"), request.getParameter("target")) {
      case (EntityType(s), EntityType(t)) => (s, t, AutoLift(s, t) {
        case (Contract, Contract) => (matchContractToContractUrl)
        case (Contract, BusinessEntity) => matchContractToBusinessEntityUrl
        case (BusinessEntity, Contract) => matchBusinessEntityToContractUrl
      })
    }
    val isPrivate = request.getParameter("private") match {
      case Boolean(p) => p
      case _ => false
    }
    (endpointUri, request.getParameter("uri"), testUser /*getUserContext(request)*/ ) match {
      case (Some((s, t, Some(e))), NonEmptyString(u), uc: UserContext) => {
        val g = if (isPrivate) sendPut(s, u, uc.getNamedGraph) else None
        response.getWriter.print(sendGet(e, u, g).extend(t).toJson)
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

  def sendGet(endpointUri: String, entityUri: String, graph: Option[String]) = GetResponse(JSON.parseRaw {
    val r = client.target(endpointUri).queryParam("uri", entityUri).queryParam("limit", "100")
    (graph match {
      case Some(g) => r.queryParam(PutResponse.graphUriKey, g)
      case None => r
    }).request("application/ld+json").get(classOf[String])
  })

  def sendPut(source: EntityType, entityUri: String, graph: String) = {
    val endpointUri = source match {
      case Contract => loadContractUrl
      case BusinessEntity => loadBusinessEntityUrl
    }
    val baos = new ByteArrayOutputStream
    try {
      Sparql
        .privateQuery(
          Template(
            this.getClass.getResource("/cz/opendata/tenderstats/sparql/resource_description.mustache"),
            Map(
              "source-graph" -> graph,
              "resource" -> entityUri)))
        .execConstruct
        .write(baos, "TURTLE")
      val x = baos.toString("UTF-8").replaceAll("http://purl.org/weso/cpv/2008/", "http://linked.opendata.cz/resource/cpv-2008/concept/")
      PutResponse(JSON.parseRaw(client.target(endpointUri).request.put(Entity.entity(x, "text/turtle"), classOf[String])))
    } finally {
      baos.close
    }
  }

  object PutResponse {
    val graphUriKey = "graph_uri"
    def apply(j: Option[JSONType]) = {
      import cz.opendata.tenderstats.utils.JsonParser._
      val GraphUri = (".+?" + graphUriKey + "=(.+?)(?:&.*|$)").r
      Lift(j) {
        case Some(x) => Lift((x \\ ("@id") collect { case x: String => x } find (_.contains(graphUriKey)))) {
          case Some(GraphUri(x)) => Try(URLDecoder.decode(x, "UTF-8")).toOption
        }
      }
    }
  }

  class GetResponse(val vals: List[Map[String, Any]]) {
    def extend(t: EntityType) = {
      import cz.opendata.tenderstats.utils.QuerySolutionOpts._
      import scala.collection.JavaConversions._
      new GetResponse(
        Sparql.publicQuery(
          Template(
            t match {
              case Contract => this.getClass.getResource("/cz/opendata/tenderstats/sparql/contract_results_enrichment.mustache")
              case BusinessEntity => this.getClass.getResource("/cz/opendata/tenderstats/sparql/business_entity_results_enrichment.mustache")
            },
            Map(
              "source-graph" -> Config.cc.getPreference("publicGraphName"),
              "results" -> vals.map(_.map { case (k, v) => k -> v.toString })))).execSelect.toList map (_.toMap))
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
              case x @ List(Some(_: String), Some(_: Double), Some(_: String)) => List("uri", "score", "label").zip(x map (_.get)).toMap
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