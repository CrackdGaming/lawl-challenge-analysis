package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.{Actor, ActorRef}
import com.crackd.lawlchallenge.actor.AnalysisEngine.{Analysis, Analyze}
import com.crackd.lawlchallenge.actor.Bus._
import com.crackd.lawlchallenge.actor.Journaler.Journal
import play.api.libs.json.JsValue

import scala.collection.mutable

/**
 * Created by trent ahrens on 4/10/15.
 */
object Bus {
  case class GameDataAvailable(json: JsValue, p: Path)
}

class Bus(analysisEngine: ActorRef, journaler: ActorRef) extends Actor {
  import context._

  val queue: mutable.Queue[GameDataAvailable] = mutable.Queue.empty

  override def receive: Receive = idle
  
  def idle: Receive = {
    case GameDataAvailable(json, p) =>
      analysisEngine ! Analyze(json)
      become(waitingForAnalysis(p))
  }
  
  def waitingForAnalysis(p: Path): Receive = {
    case Analysis(json) =>
      journaler ! Journal(p, json)
      if (queue.nonEmpty) {
        val e = queue.dequeue()
        analysisEngine ! Analyze(e.json)
        become(waitingForAnalysis(e.p))
      }
      else become(idle)
    case e @ GameDataAvailable(_,_) =>
      queue.enqueue(e)
  }
}