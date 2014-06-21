package cz.opendata.tenderstats.utils

import java.net.URLEncoder

object ServletUtilsImpl {

  def encodeURI(uri: String, parts: Array[String]) = {
    if (parts.isEmpty)
      URLEncoder.encode(uri, "UTF-8")
    else
      uri.split("""\?""", 2) match {
        case Array(path, qs) => path + "?" + qs.split('&').map(_.split("=", 2)).collect({
          case Array(x, y) => x + "=" + (if (parts.contains(x)) URLEncoder.encode(y, "UTF-8") else y)
        }).mkString("&")
        case _ => uri
      }
  }

}
