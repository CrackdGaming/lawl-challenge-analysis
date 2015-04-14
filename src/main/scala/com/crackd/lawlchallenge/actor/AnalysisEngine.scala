package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern._
import akka.util.Timeout
import com.crackd.lawlchallenge.actor.AnalysisAccumulator.{Serialize, Serialized}
import com.crackd.lawlchallenge.actor.AnalysisEngine._
import com.crackd.lawlchallenge.actor.AnalysisWorker.{RegisterWorker, _}
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * Created by trent ahrens on 4/10/15.
 */

object AnalysisEngine {
  case class Analyze(p: Path)
  case class Analysis(json: JsValue, l: Seq[Path])
  case object GetAnalysis

  private case object SendAnalysisIfIdle
  private case class Snapshot(json: JsValue)
}

class AnalysisEngine(accumulators: Seq[ActorRef]) extends Actor with ActorLogging {
  import context._

  val maxQueue = 100

  implicit val timeout = Timeout(20 seconds)

  val queue: mutable.Queue[Path] = mutable.Queue.empty

  val workers: mutable.Map[ActorRef,Boolean] = mutable.Map.empty

  val finishedPaths: mutable.ListBuffer[Path] = mutable.ListBuffer.empty

  var bus: ActorRef = null

  override def receive: Receive = running orElse default

  def running: Receive = {
    case RegisterWorker(worker) => workers += worker -> false
    case GetWork(worker) =>
      if (queue.nonEmpty && !workers(worker)) {
        workers += worker -> true
        worker ! DoWork(queue.dequeue())
      }
    case Analyze(p) =>
      queue.enqueue(p)
      bus = sender()
      workers.keys.foreach(_ ! WorkAvailable)
    case GetAnalysis =>
      become(waitForIdle orElse default)
      if (workers.values.forall(_ == false)) self ! SendAnalysisIfIdle
  }

  def waitForIdle: Receive = {
    case Analyze(p) =>
      queue.enqueue(p)
    case SendAnalysisIfIdle =>
      Future {
        log.info("getting snapshot")
        val r = Await.result(Future.traverse(accumulators)(a => a ? Serialize).mapTo[Seq[Serialized]], Duration.Inf)
        Snapshot(Json.toJson(r.map(a => (a.name,a.json)).toMap))
      } pipeTo self
    case Snapshot(json) =>
      log.info("forward snapshot {}", queue.size)
      val files = finishedPaths.toList
      finishedPaths.clear()
      bus ! Analysis(json, files)
      become(receive orElse default)
      workers.keys.foreach(_ ! WorkAvailable)
  }

  def default: Receive = {
    case FinishedWork(worker,p) =>
      workers += worker -> false
      finishedPaths += p
      self ! SendAnalysisIfIdle
    case FailedWork(worker,p) =>
      workers += worker -> false
      finishedPaths += p
      self ! SendAnalysisIfIdle
  }
}
