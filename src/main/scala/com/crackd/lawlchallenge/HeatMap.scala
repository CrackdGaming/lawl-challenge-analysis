package com.crackd.lawlchallenge

import play.api.libs.json._

import scala.language.postfixOps

/**
 * Created by tahrens on 4/9/15.
 */
object HeatMap {
  def apply() = new HeatMap()
}

class HeatMap {
  def apply(json: JsValue): JsValue = layerGroupsToJson(toEventSeq(json).map(toAtomSeq).groupBy(_.e).map(toLayerGroups).toSeq)

  def toEventSeq(j: JsValue) = (j \ "timeline" \ "frames" \\ "events").asInstanceOf[Seq[JsArray]].flatMap(_.value).filter(j => (j \ "position") != JsNull)

  def toAtomSeq: (JsValue) => Atom = j => Atom(getEventType(j),getPoint(j),1L)
  
  def toLayerGroups: ((EventType, Seq[Atom])) => LayerGroup = ea => LayerGroup(ea._1,toLayer(ea._2))

  def toLayer(s: Seq[Atom]): Layer = s.foldLeft(Layer(Map.empty))((l,a) => l + a.p)

  def getPoint(json: JsValue): Point = Point((json \ "position" \ "x").as[Int], (json \ "position" \ "y").as[Int])

  def getEventType(json: JsValue): String = (json \ "eventType").as[String]

  def layerGroupsToJson(s: Seq[LayerGroup]): JsValue = Json.toJson(s.sortBy(_.e))

  type Count = Long
  type EventType = String

  case class Point(x: Int, y: Int)

  case class Atom(e: EventType, p: Point, c: Count)

  case class Layer(m: Map[Point,Count]) {
    def +(p: Point): Layer = this + (p,1)
    def +(p: Point, c: Count) = Layer(m + (p -> (m.getOrElse(p, 0L) + c)))
  }

  case class LayerGroup(e: EventType, l: Layer)

  implicit val statWrites = new Writes[(Point,Count)] {
    override def writes(o: (Point, Count)): JsValue = o match {
      case (p, c) => Json.obj(
        "x" -> p.x,
        "y" -> p.y,
        "c" -> c
      )
    }
  }

  implicit val layerGroupWrites = new Writes[LayerGroup] {
    override def writes(lg: LayerGroup): JsValue = Json.obj(
      "eventType" -> lg.e,
      "data" -> Json.toJson(lg.l.m.toSeq)
    )
  }
}
