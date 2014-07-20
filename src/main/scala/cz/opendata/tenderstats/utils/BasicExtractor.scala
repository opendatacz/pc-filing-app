package cz.opendata.tenderstats.utils

import java.io.File
import scala.util.Try

object BasicExtractor {

  object NonEmptyString {
    def unapply(e: String) = e match {
      case x: String if !x.isEmpty => Some(x)
      case _ => None
    }
  }

  object File {
    def unapply(f: String) = {
      val file = new File(f)
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
    def unapply(s: String) = Try(s.toBoolean).toOption
  }

}