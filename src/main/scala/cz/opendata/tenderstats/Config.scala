package cz.opendata.tenderstats

import scala.xml.XML

object Config {

  private val xml = XML.load(getClass.getResource("config/config.xml"))
  val matchmaker = xml \ "matchmaker"
  
}
