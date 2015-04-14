package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.crackd.lawlchallenge.actor.AnalysisEngine._
import com.crackd.lawlchallenge.actor.Bus._
import com.crackd.lawlchallenge.actor.CommonMessages.{Resume, Suspend}
import com.crackd.lawlchallenge.actor.Journaler.Journal
import play.api.libs.json.{JsNull, JsValue}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
 * Created by trent ahrens on 4/10/15.
 */
object Bus {
  case class GameDataAvailable(p: Path)
  case object GetAnalysisSnapshot
  case class AnalysisSnapshot(json: JsValue)
}

class Bus(analysisEngine: ActorRef, journaler: ActorRef) extends Actor with ActorLogging {

  import context._

  var importer: ActorRef = null

  var snapshot: JsValue = JsNull

  system.scheduler.schedule(30 seconds, 30 seconds, analysisEngine, GetAnalysis)

  override def receive: Receive = {
    case GameDataAvailable(p) =>
      importer = sender()
      analysisEngine ! Analyze(p)
    case Suspend => importer ! Suspend
    case Resume => importer ! Resume
    case Analysis(json, paths) =>
      snapshot = json
      journaler ! Journal(paths, json)
    case GetAnalysisSnapshot => sender() ! AnalysisSnapshot(snapshot)
  }
}