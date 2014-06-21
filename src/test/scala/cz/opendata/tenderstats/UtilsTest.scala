package cz.opendata.tenderstats

import org.junit._
import cz.opendata.tenderstats.utils.ServletUtils
import java.net.URLEncoder
import org.junit.Assert._

class UtilsTest {

  @Test
  def testEncodeURI: Unit = {
    assertEquals(ServletUtils.encodeURI("http://localhost:8080/ščřž?m=ěščř&n=ěščř"), URLEncoder.encode("http://localhost:8080/ščřž?m=ěščř&n=ěščř", "UTF-8"))
    assertEquals(ServletUtils.encodeURI("http://localhost:8080/ščřž?m=ěščř&n=ěščř", "t"), "http://localhost:8080/ščřž?m=ěščř&n=ěščř")
    assertEquals(ServletUtils.encodeURI("http://localhost:8080/ščřž?m=ěščř&n=ěščř", "m"), "http://localhost:8080/ščřž?m=" + URLEncoder.encode("ěščř", "UTF-8") + "&n=ěščř")
    assertEquals(ServletUtils.encodeURI("http://localhost:8080/ščřž?m=ěščř&n=ěščř", "m", "n"), "http://localhost:8080/ščřž?m=" + URLEncoder.encode("ěščř", "UTF-8") + "&n=" + URLEncoder.encode("ěščř", "UTF-8") + "")
  }

}