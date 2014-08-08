package cz.opendata.tenderstats.utils

object NonEmptyString {
  def unapply(e: String) = e match {
    case x: String if !x.isEmpty => Some(x)
    case _ => None
  }
}

object AnyToDouble {
  def unapply(s: Any): Option[Double] = try {
    if (s == null)
      None
    else
      Some(s match {
        case x: Int => x.toDouble
        case x: Double => x
        case x: Float => x.toDouble
        case x: Long => x.toDouble
        case x: Short => x.toDouble
        case x: Byte => x.toDouble
        case x => x.toString.toDouble
      })
  } catch {
    case _: java.lang.NumberFormatException => None
  }
}

object File {
  def unapply(f: String) = {
    val file = new java.io.File(f)
    if (file.isFile && file.canRead)
      Some(file)
    else
      None
  }
}

object Resource {
  def unapply(f: java.net.URL) = f match {
    case null => None
    case x => Some(x)
  }
}

object Boolean {
  def unapply(s: String) = scala.util.Try(s.toBoolean).toOption
}