package com.crackd.lawlchallenge.actor

import akka.actor.Actor
import com.crackd.lawlchallenge.actor.AnalysisEngine._

/**
 * Created by trent ahrens on 4/10/15.
 */

object AnalysisEngine {
  case object Analyze
  case object AnalysisComplete
}

class AnalysisEngine extends Actor {

  var counter = 0

  override def receive: Receive = {
    case Analyze =>
      counter += 1
      sender() ! AnalysisComplete
  }
}
