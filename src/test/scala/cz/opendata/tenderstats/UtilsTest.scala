package cz.opendata.tenderstats

import org.junit._
import cz.opendata.tenderstats.utils.UriEncoder
import java.net.URLEncoder
import org.junit.Assert._

class UtilsTest {

  @Test
  def testEncodeURI: Unit = {
    assertEquals(UriEncoder("http://localhost:8080/ščřž?m=ěščř&n=ěščř").encode, URLEncoder.encode("http://localhost:8080/ščřž?m=ěščř&n=ěščř", "UTF-8"))
    assertEquals(UriEncoder("http://localhost:8080/ščřž?m=ěščř&n=ěščř").part("t").encode, "http://localhost:8080/ščřž?m=ěščř&n=ěščř")
    assertEquals(UriEncoder("http://localhost:8080/ščřž?m=ěščř&n=ěščř").part("m").encode, "http://localhost:8080/ščřž?m=" + URLEncoder.encode("ěščř", "UTF-8") + "&n=ěščř")
    assertEquals(UriEncoder("http://localhost:8080/ščřž?m=ěščř&n=ěščř").part("m").part("n").encode, "http://localhost:8080/ščřž?m=" + URLEncoder.encode("ěščř", "UTF-8") + "&n=" + URLEncoder.encode("ěščř", "UTF-8") + "")
  }

}