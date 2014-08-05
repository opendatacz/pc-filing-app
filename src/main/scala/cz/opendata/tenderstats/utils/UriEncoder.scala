package cz.opendata.tenderstats.utils

import java.net.URLEncoder

class UriEncoder(uri: String, parts: List[String]) {
  def part(x: String) = new UriEncoder(uri, x :: parts)
  def encode = {
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

object UriEncoder {
  def apply(uri: String) = new UriEncoder(uri, Nil)
}