package cz.opendata.tenderstats

import cz.opendata.tenderstats.utils.Resource
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.TemplateSource

object Template {

  val te = new TemplateEngine

  def apply(r: java.net.URL, m: Map[String, Any] = Map.empty) = {
    r match {
      case Resource(r) => te.layout(TemplateSource.fromURL(r), m).trim
      case _ => ""
    }
  }

}