package com.crackd.lawlchallenge

import com.crackd.lawlchallenge.gametypes._
import com.crackd.lawlchallenge.helpers.GameDataFinders._

import play.api.libs.json._

import scala.language.postfixOps

/**
*  Created by trent ahrens on 4/9/15.
*/
object HeatMapAnalysis {
  def apply() = new HeatMapAnalysis()

  type Count = Long

  object HeatMap {
    def empty = new HeatMap(Map.empty)
  }

  case class HeatMap(m: Map[Point,Count]) {
    def +(p: Point, c: Count): HeatMap = HeatMap(m + (p -> (m.getOrElse(p, 0L) + c)))
    def +(hm: HeatMap): HeatMap = m.foldRight(hm) { case ((p,c),`hm`) => hm + (p,c) }
  }

  object HeatMaps {
    def empty = new HeatMaps(Map.empty)
  }

  case class HeatMaps(m: Map[EventType,HeatMap]) extends Statistic[HeatMaps] {
    def +(e: EventType, hm: HeatMap): HeatMaps = HeatMaps(m + (e -> (m.getOrElse(e, HeatMap.empty) + hm)))
    override def + (hms: HeatMaps): HeatMaps = m.foldRight(hms) { case ((e,hm),`hms`) => hms + (e, hm)}
  }

  implicit val heatMapPairWrites = new Writes[(Point,Count)] {
    override def writes(o: (Point, Count)): JsValue = o match {
      case (p, c) => Json.obj(
        "x" -> p.x,
        "y" -> p.y,
        "c" -> c
      )
    }
  }

  implicit val heatMapWrites = new Writes[HeatMap] {
    override def writes(o: HeatMap): JsValue = Json.toJson(o.m.toSeq.sortBy(t => (t._1.x,t._1.y)))
  }

  implicit val heatMapsPairWrites = new Writes[(EventType,HeatMap)] {
    override def writes(o: (EventType, HeatMap)): JsValue = Json.obj(
      "eventType" -> o._1,
      "data" -> Json.toJson(o._2)
    )
  }

  implicit val heatMapsWrites = new Writes[HeatMaps] {
    override def writes(hms: HeatMaps): JsValue = Json.toJson(hms.m.toSeq.sortBy(_._1))
  }
}

class HeatMapAnalysis {
  import HeatMapAnalysis._

  case class Atom(e: EventType, p: Point, c: Count)

  def apply(json: JsValue): JsValue =
    Json.toJson(
      json.events.withPositions
        .map(toAtomSeq).groupBy(_.e)
        .foldRight(HeatMaps.empty) {
          case ((e,s),hms) => hms + (e, s.foldRight(HeatMap.empty)((a,hm) => hm + (a.p, a.c)))
        }
    )

  def toAtomSeq: (JsValue) => Atom = j => Atom(getEventType(j),getPoint(j),1L)

  def getPoint(json: JsValue): Point = Point((json \ "position" \ "x").as[Int], (json \ "position" \ "y").as[Int])

  def getEventType(json: JsValue): String = (json \ "eventType").as[String]
}
