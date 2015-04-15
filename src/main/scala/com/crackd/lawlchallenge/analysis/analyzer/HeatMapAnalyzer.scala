package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.HeatMapModels._
import com.crackd.lawlchallenge.gametypes._
import com.crackd.lawlchallenge.helper.GameDataFinders._
import play.api.libs.json._

import scala.language.postfixOps

/**
*  Created by trent ahrens on 4/9/15.
*/
class HeatMapAnalyzer extends Analyzer[HeatMaps] {

  case class Atom(e: EventType, p: Point, c: Count)

  def apply(json: JsValue): HeatMaps =
    json.events.withPositions
      .map(toAtomSeq).groupBy(_.e)
      .foldRight(HeatMapsMonid.zero) {
      case ((e, s), hms) => hms += (e, s.foldRight(HeatMap.empty)((a, hm) => hm += (a.p, a.c)))
    }

  def toAtomSeq: (JsValue) => Atom = j => Atom(getEventType(j), getPoint(j), 1L)

  def getPoint(json: JsValue): Point = Point(downScale((json \ "position" \ "x").as[Int]), downScale((json \ "position" \ "y").as[Int]))

  def downScale(l: Int): Int = l / 20

  def getEventType(json: JsValue): String = (json \ "eventType").as[String]
}
