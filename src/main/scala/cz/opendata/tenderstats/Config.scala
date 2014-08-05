package cz.opendata.tenderstats

import cz.opendata.tenderstats.utils.File
import cz.opendata.tenderstats.utils.Match
import cz.opendata.tenderstats.utils.NonEmptyString
import cz.opendata.tenderstats.utils.Resource
import scala.xml.Elem
import scala.xml.Node
import scala.xml.Text
import scala.xml.XML

object Config {

  val (cc, matchmaker, numberOfBidders, prefixes) = {
    import cz.opendata.tenderstats.utils.PcfaStringOpts._
    val xml = {
      scala.xml.Utility.trim(
        (this.getClass.getResource("/cz/opendata/tenderstats/config/config.xml"), System.getenv("PCFA_CONFIG")) match {
          case (Resource(c1), NonEmptyString(File(c2))) => merge(XML.load(c1), XML.loadFile(c2))
          case (Resource(c1), _) => XML.load(c1)
          case _ => throw new ConfigException("Config was not loaded.")
        })
    }
    val prefixes = this.getClass.getResource("/cz/opendata/tenderstats/config/prefixes.xml") match {
      case (Resource(r)) => XML.load(r)
      case _ => throw new ConfigException("Prefixes was not loaded.")
    }
    val cc = new ComponentConfiguration
    Match((xml \ ("sparql")).headOption) {
      case Some(<sparql><public-graph-name>{ Text(pgn) }</public-graph-name><endpoints><private-query>{ Text(prq) }</private-query><private-update>{ Text(pru) }</private-update><public-query>{ Text(puq) }</public-query><public-update>{ Text(puu) }</public-update></endpoints></sparql>) => {
        cc.setSparqlPrivateQuery(prq)
        cc.setSparqlPrivateUpdate(pru)
        cc.setSparqlPublicQuery(puq)
        cc.setSparqlPublicUpdate(puu)
        cc.setPreference("publicGraphName", pgn)
      }
    }
    Match((xml \ ("emails")).headOption) {
      case Some(<emails><info>{ Text(e1) }</info><invitation>{ Text(e2) }</invitation></emails>) => {
        cc.setPreference("infoMail", e1)
        cc.setPreference("invitationEmail", e2)
      }
    }
    Match((xml \ ("system")).headOption) {
      case Some(Elem(_, _, _, _, ch @ _*)) => ch foreach (x => cc.setPreference(x.label.toClassFormat, x.text))
    }
    val pf = (prefixes \\ ("prefix") map (x => x.attribute("id") -> x.text) collect { case (Some(Seq(x)), NonEmptyString(y)) if !x.text.isEmpty => Map("id" -> x.text, "uri" -> y) })
    cc.setPreference("prefixes", Template(this.getClass.getResource("/cz/opendata/tenderstats/sparql/prefixes.mustache"), Map("prefixes" -> pf)))
    pf foreach (x => cc.setPrefix(x("id"), x("uri")))
    (cc, xml \ "matchmaker", xml \ "number-of-bidders", pf)
  }

  private def merge(xml1: Node, xml2: Node): Node = {
    (xml1, xml2) match {
      case (Text(t1), Text(t2)) if !t2.isEmpty => Text(t2)
      case (Elem(p, l1, a, s, ch1 @ _*), Elem(_, l2, _, _, ch2 @ _*)) =>
        Elem(p, l1, a, s, false, (if (ch1.isEmpty) List(Text("")) else ch1) map (x => (ch2 find (_.label == x.label)) match {
          case Some(y) => merge(x, y)
          case None => x
        }): _*)
      case (x, _) => x
    }
  }

}