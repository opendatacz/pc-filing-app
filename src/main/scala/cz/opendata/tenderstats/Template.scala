package cz.opendata.tenderstats

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.TemplateSource

object Template {

  val te = new TemplateEngine

  def unapply(rm: (java.net.URL, Map[String, Any])) = {
    import cz.opendata.tenderstats.utils.BasicExtractor._
    rm match {
      case (Resource(r), m) => Some(te.layout(TemplateSource.fromURL(r), m))
      case _ => None
    }
  }

}