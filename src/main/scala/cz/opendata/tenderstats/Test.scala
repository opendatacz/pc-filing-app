package cz.opendata.tenderstats

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.TemplateSource
import org.fusesource.scalate.japi.TemplateEngineFacade
import scala.xml.Text
import scala.xml.XML

object Test {

  def main(args: Array[String]) {
    println(Config.cc.getRdbAddress)
    println(Config.cc.getRdbUsername)
    println(Config.cc.getRdbPassword)
    println(Config.cc.getRdbDatabase)
    println(Config.cc.getPreference("prefixes"))
    //XmlMerge.merge(XML.load(getClass.getResource("config/config.xml")), XML.load(getClass.getResource("config/config.xml")))
    //val engine = new TemplateEngine
    //println(engine.layout(TemplateSource.fromURL(getClass.getResource("sparql/resource_description.mustache")), Map("resource" -> "testest")))
  }
  
}
