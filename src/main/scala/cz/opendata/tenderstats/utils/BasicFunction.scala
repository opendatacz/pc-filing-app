package cz.opendata.tenderstats.utils

object Match {
  def default: PartialFunction[Any, Unit] = { case _ => }
  def apply[T](x: T)(body: PartialFunction[T, Unit]) = (body orElse default)(x)
}

object Lift {
  def default[U]: PartialFunction[Any, Option[U]] = { case _ => None }
  def apply[T, U](x: T)(body: PartialFunction[T, Option[U]]) = (body orElse default)(x)
}
object AutoLift {
  def apply[T, U](x: T)(body: PartialFunction[T, U]) = body.lift(x)
}

class PcfaStringOpts(str: String) {
  def toClassFormat = str.foldLeft("")((r, s) => (if (r.endsWith("-")) r + s.toUpper else r + s)).replaceAllLiterally("-", "")
}
object PcfaStringOpts {
  import scala.language.implicitConversions
  implicit def stringToPcfaStringOpts(str: String) = new PcfaStringOpts(str)
}

class QuerySolutionOpts(q: com.hp.hpl.jena.query.QuerySolution) {
  import scala.collection.JavaConversions._
  def toMap = q.varNames.map(x => x -> (q.get(x) match {
    case x if x.isLiteral => x.asLiteral.getString match {
      case AnyToDouble(x) => x
      case x => x
    }
    case x => x.asNode.toString(false)
  })).toMap
}
object QuerySolutionOpts {
  import scala.language.implicitConversions
  implicit def querySolutionToQuerySolutionOpts(q: com.hp.hpl.jena.query.QuerySolution) = new QuerySolutionOpts(q)
}