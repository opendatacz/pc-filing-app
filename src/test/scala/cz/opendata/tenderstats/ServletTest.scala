package cz.opendata.tenderstats

import org.junit._
import java.io.PrintWriter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.junit.Assert._
import org.mockito.Mockito._

class ServletTest {

//  @Test
//  def testMatchmaker: Unit = {
//    val request = mock(classOf[HttpServletRequest])
//    val response = mock(classOf[HttpServletResponse])
//    when(request.getParameter("source")).thenReturn("contract")
//    when(request.getParameter("target")).thenReturn("business-entity")
//    when(request.getParameter("uri")).thenReturn("http://linked.opendata.cz/resource/vestnikverejnychzakazek.cz/public-contract/367933-7402031067933")
//    val writer = new PrintWriter(System.out, true)
//    when(response.getWriter()).thenReturn(writer)
//    val servlet = new Matchmaker
//    servlet.doGetPost(request, response)
//    writer.flush
//  }

}