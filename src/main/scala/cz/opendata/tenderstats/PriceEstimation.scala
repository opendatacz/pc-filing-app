package cz.opendata.tenderstats

import cz.opendata.tenderstats.dm.Model1
import cz.opendata.tenderstats.utils.AnyToDouble
import cz.opendata.tenderstats.utils.AutoLift
import cz.opendata.tenderstats.utils.Lift
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.log4j.LogManager
import scala.io.Source
import scala.util.Try

@WebServlet(Array("/PriceEstimation"))
class PriceEstimation extends AbstractComponent {

  val cpvs = {
    val xf = List.fill(7)(0)
    CpvMapper.loadFromIterator(Source.fromURL(getClass.getResource("/cz/opendata/tenderstats/config/cpv-2008-cs.csv")).getLines) map { case (_, y) => y.id -> PriceEstimation.mergeLists(xf, y.v)((_, y) => y + 1) }
  }

  override def doGetPost(request: HttpServletRequest, response: HttpServletResponse) = {
    response.setContentType("application/json; charset=UTF-8")
    Lift(request.getParameter("cpv"), request.getParameter("dur")) {
      case (AnyToDouble(cpv), AnyToDouble(dur)) => AutoLift(cpvs.get(cpv.toInt)) {
        case Some(List(c1, c2, c3, c4, c5, c6, c7)) => PriceEstimation.model.predictField9(c1.toString, c2.toString, c3.toString, c4.toString, c5.toString, c6.toString, c7.toString, dur)
      }
    } match {
      case Some(AnyToDouble(x)) => 
        val from = PriceEstimation.intervals.foldLeft(0)((r, s) => if (x > s || x == 0) s else r)
        val to = x.toInt
        response.getWriter.print(s"""{ "price": { "from": $from, "to": $to } }""")
      case _ => response.sendError(400)
    }
  }

}

object PriceEstimation {

  val logger = LogManager.getLogger("PriceEstimation")
  import logger._

  val model = new Model1
  val intervals = List(250000, 500000, 1000000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000, 4500000, 5000000, 6000000, 7000000, 8000000, 9000000, 10000000, 12500000, 15000000, 17500000, 20000000, 25000000, 30000000, 35000000, 40000000, 50000000, 75000000, 100000000, 250000000)

  def mergeLists[A](l: List[A], ln: List[A]*)(m: (A, A) => A) = {
    def mergeItemList(l: List[A], result: List[A], ln: Seq[List[A]]): List[A] = l match {
      case head :: tail => mergeItemList(
        tail,
        ln.foldLeft(head) {
          case (r, head :: tail) => m(r, head)
          case (r, _) => r
        } :: result,
        ln map {
          case _ :: tail => tail
          case x => x
        })
      case _ => result.reverse
    }
    mergeItemList(l, Nil, ln)
  }

}

trait CPV
case class MinCPV(id: Int, val v: List[Int]) extends CPV
case class ExtCPV(id: Int, val v: List[String]) extends CPV

object CPV {

  def unapply(str: String) = {
    def loadChar(rest: List[Char], current: String, result: List[String]): List[String] = rest match {
      case head :: tail => {
        if (head == ',' && current.matches("(\".+\")|([^\"].*)"))
          loadChar(tail, "", current.replaceAll("(^\")|(\"$)", "") :: result)
        else
          loadChar(tail, current + head, result)
      }
      case _ => current.replaceAll("(^\")|(\"$)", "") :: result
    }
    Lift(loadChar(str.toCharArray.toList, "", Nil).reverse) {
      case a @ head :: tail => Try(head.replaceFirst(".+/", "").toInt).toOption map (x => ExtCPV(x, tail))
    }
  }

}

object CpvMapper {

  def loadFromIterator(it: Iterator[String]) = {
    def loadCpv(it: Iterator[String], result: List[ExtCPV], minm: Map[String, Int]): List[MinCPV] = {
      if (it.hasNext)
        it.next match {
          case CPV(cpv @ ExtCPV(id, v)) => loadCpv(it, cpv :: result, v.foldLeft(minm)((r, x) => r + (x -> r.getOrElse(x, r.size))))
          case x => {
            loadCpv(it, result, minm)
          }
        }
      else
        result map (x => MinCPV(x.id, x.v map minm))
    }
    (loadCpv(it, Nil, Map.empty) map (x => x.id -> x)).toMap
  }

}