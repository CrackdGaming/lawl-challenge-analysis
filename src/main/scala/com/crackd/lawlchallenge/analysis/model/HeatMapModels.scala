package com.crackd.lawlchallenge.analysis.model

import com.crackd.lawlchallenge.MutableMonoid
import com.crackd.lawlchallenge.gametypes._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.collection.mutable
import scala.language.postfixOps

/**
 * Created by trent ahrens on 4/10/15.
 */
object HeatMapModels {
  type Count = Long

  case object HeatMap {
    def empty = HeatMap(mutable.Map.empty)
  }

  case class HeatMap(m: mutable.Map[Point,Count]) {
    def +(p: Point, c: Count): HeatMap = HeatMap(m + (p -> (m.getOrElse(p, 0L) + c)))
    def +(hm: HeatMap): HeatMap = {
      var foldOnto = hm
      var map = m
      if (m.size > hm.m.size) {
        foldOnto = this
        map = hm.m
      }
      map.foldLeft(foldOnto) { case (a,(p,c)) => a + (p,c) }
    }
    def +=(p: Point, c: Count): HeatMap = {
      m += (p -> (m.getOrElse(p, 0L) + c))
      this
    }
    def +=(hm: HeatMap): HeatMap = hm.m.foldLeft(this) { case (a,(p,c)) => a += (p,c) }
  }

  case class HeatMaps(m: mutable.Map[EventType,HeatMap]) {
    def +(e: EventType, hm: HeatMap): HeatMaps = HeatMaps(m + (e -> (m.getOrElse(e, HeatMap.empty) + hm)))
    def +(o: HeatMaps): HeatMaps = m.foldLeft(o) { case (a,(e,hm)) => a + (e, hm)}
    def +=(e: EventType, hm: HeatMap): HeatMaps = {
      m += (e -> (m.getOrElse(e, HeatMap.empty) += hm))
      this
    }
    def +=(o: HeatMaps): HeatMaps = o.m.foldLeft(this) { case (a,(e,hm)) => a += (e, hm) }
  }

  implicit object HeatMapsMonid extends MutableMonoid[HeatMaps] {
    override def zero: HeatMaps = new HeatMaps(mutable.Map.empty)

    override def append(f1: HeatMaps, f2: => HeatMaps): HeatMaps = f1 + f2

    override def +=(left: HeatMaps, right: HeatMaps): HeatMaps = left += right
  }

  implicit val heatMapPairFormat = new Format[(Point,Count)] {
    override def writes(o: (Point, Count)): JsValue = o match {
      case (p, c) => Json.obj(
        "x" -> p.x,
        "y" -> p.y,
        "c" -> c
      )
    }

    override def reads(json: JsValue): JsResult[(Point, Count)] = JsSuccess((
      Point((json \ "x").as[Int], (json \ "y").as[Int]),
      (json \ "c").as[Long]
      ))
  }

  implicit val heatMapFormat = new Format[HeatMap] {
    override def writes(o: HeatMap): JsValue = Json.toJson(o.m.toSeq.sortBy(t => (t._1.x,t._1.y)))

    override def reads(json: JsValue): JsResult[HeatMap] = JsSuccess(HeatMap(mutable.Map(json.as[Seq[(Point,Count)]]: _*)))
  }

  implicit val heatMapsPairFormat = new Format[(EventType,HeatMap)] {
    override def writes(o: (EventType, HeatMap)): JsValue = Json.obj(
      "eventType" -> o._1,
      "data" -> Json.toJson(o._2)
    )

    override def reads(json: JsValue): JsResult[(EventType, HeatMap)] =
      JsSuccess(((json \ "eventType").as[String],(json \ "data").as[HeatMap]))
  }

  implicit val heatMapsFormat = new Format[HeatMaps] {
    override def writes(hms: HeatMaps): JsValue = Json.toJson(hms.m.toSeq.sortBy(_._1))

    override def reads(json: JsValue): JsResult[HeatMaps] =
      JsSuccess(HeatMaps(mutable.Map(json.as[Seq[(EventType,HeatMap)]]: _*)))
  }
}
