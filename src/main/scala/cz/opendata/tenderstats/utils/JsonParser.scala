package cz.opendata.tenderstats.utils

import scala.util.parsing.json.JSONArray
import scala.util.parsing.json.JSONObject
import scala.util.parsing.json.JSONType

class JsonParser(jt: JSONType) {

  def \\(n: String): List[Any] = jt match {
    case JSONObject(o) => o.collect {
      case (`n`, o: JSONType) => o :: (new JsonParser(o) \\ n)
      case (`n`, o) => List(o)
      case (_, o: JSONType) => new JsonParser(o) \\ n
    }.fold(Nil)(_ ::: _)
    case JSONArray(a) => a.collect {
      case o: JSONType => new JsonParser(o) \\ n
    }.fold(Nil)(_ ::: _)
  }

  def \(n: String): Option[Any] = jt match {
    case JSONObject(o) => o.get(n) match {
      case x @ Some(_) => x
      case None => new JsonParser(JSONArray(o.values.toList)) \ n
    }
    case JSONArray((head: JSONType) :: tail) => (new JsonParser(head) \ n) match {
      case x @ Some(_) => x
      case None => new JsonParser(JSONArray(tail)) \ n
    }
    case JSONArray(_ :: tail) => new JsonParser(JSONArray(tail)) \ n
    case _ => None
  }

  def \|\(n: String*): Option[Any] = n.toList match {
    case head :: tail => \(head) orElse \|\(tail: _*)
    case _ => None
  }

}

object JsonParser {
  import scala.language.implicitConversions
  implicit def JSONTypeToJsonParser(jt: JSONType) = new JsonParser(jt)
}