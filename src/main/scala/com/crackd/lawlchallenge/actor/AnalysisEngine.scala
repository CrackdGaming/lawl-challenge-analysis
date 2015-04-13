package com.crackd.lawlchallenge.actor

import akka.actor.Actor
import com.crackd.lawlchallenge.actor.AnalysisEngine._
import com.crackd.lawlchallenge.analysis.aggregate.AnalyzerAggregate
import play.api.libs.json.{Json, JsValue}

/**
 * Created by trent ahrens on 4/10/15.
 */

object AnalysisEngine {
  case class Analyze(json: JsValue)
  case class Analysis(json: JsValue)
  case object GetAnalysis
}

class AnalysisEngine(aggregates: Seq[AnalyzerAggregate]) extends Actor {
  override def receive: Receive = {
    case Analyze(json) =>
      aggregates.foreach(_(json))
      sender ! Analysis(Json.toJson(getAnalysis))
    case GetAnalysis => sender() ! Analysis(getAnalysis)
  }

  def getAnalysis = Json.toJson(aggregates.map(a => (a.name,a.capture)).toMap)
}
