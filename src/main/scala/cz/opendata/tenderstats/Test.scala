package cz.opendata.tenderstats

object Test {

  def main(args: Array[String]) {
    List("aa", "bb", "") match {
      case rl @ List(source, target, uri) if rl.forall(!_.isEmpty) => println(source + target + uri)
      case _ => println("nic")
    }
  }
  
}
