package cz.opendata.tenderstats.utils

object Match {
  def default: PartialFunction[Any, Unit] = { case _ => }
  def apply[T](x: T)(body: PartialFunction[T, Unit]) = (body orElse default)(x)
}

class PcfaStringOpts(str : String) {
  def toClassFormat = str.foldLeft("")((r, s) => (if (r.endsWith("-")) r + s.toUpper else r + s)).replaceAllLiterally("-", "")
}
object PcfaStringOpts {
  import scala.language.implicitConversions
  implicit def stringToPcfaStringOpts(str : String) = new PcfaStringOpts(str)
}