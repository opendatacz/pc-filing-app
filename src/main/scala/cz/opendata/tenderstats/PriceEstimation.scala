package cz.opendata.tenderstats

import cz.opendata.tenderstats.dm.Model1
import cz.opendata.tenderstats.dm.Model10
import cz.opendata.tenderstats.dm.Model2
import cz.opendata.tenderstats.dm.Model3
import cz.opendata.tenderstats.dm.Model4
import cz.opendata.tenderstats.dm.Model5
import cz.opendata.tenderstats.dm.Model6
import cz.opendata.tenderstats.dm.Model7
import cz.opendata.tenderstats.dm.Model8
import cz.opendata.tenderstats.dm.Model9
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
        case Some(List(c1, c2, c3, c4, c5, c6, c7)) => PriceEstimation.models map (_.predictField9(c1.toString, c2.toString, c3.toString, c4.toString, c5.toString, c6.toString, c7.toString, dur))
      }
    } match {
      case Some(x) => response.getWriter.print("{ \"price\": " + (x.foldLeft(0D)(_ + _) / x.length) + " }")
      case _ => response.sendError(400)
    }
  }

}

object PriceEstimation {

  val logger = LogManager.getLogger("PriceEstimation")
  import logger._

  val models = List(
    new Model1,
    new Model2,
    new Model3,
    new Model4,
    new Model5,
    new Model6,
    new Model7,
    new Model8,
    new Model9,
    new Model10)

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