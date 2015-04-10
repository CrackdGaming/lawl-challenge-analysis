package com.crackd.lawlchallenge.actor

import akka.actor.Actor
import com.crackd.lawlchallenge.actor.AnalysisEngine._
import com.crackd.lawlchallenge.analysis.aggregate.AnalyzerAggregate
import play.api.libs.json.JsValue

/**
 * Created by trent ahrens on 4/10/15.
 */

object AnalysisEngine {
  case class Analyze(json: JsValue)
}

class AnalysisEngine(aggregates: Seq[AnalyzerAggregate]) extends Actor {
  type AnalyzerName = String

  override def receive: Receive = {
    case Analyze(json) => aggregates.foreach(_(json))
  }
}
