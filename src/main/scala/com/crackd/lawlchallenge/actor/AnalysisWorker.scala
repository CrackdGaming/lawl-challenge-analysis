package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern._
import akka.util.Timeout
import com.crackd.lawlchallenge.abstraction.FileService
import com.crackd.lawlchallenge.actor.AnalysisAccumulator.Add
import com.crackd.lawlchallenge.actor.AnalysisWorker.{DoWork, _}
import com.crackd.lawlchallenge.analysis.analyzer.Analyzer
import play.api.libs.json.Json

import scala.concurrent.duration.{DurationInt, Duration}
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * Created by trent ahrens on 4/14/15.
 */

object AnalysisWorker {
  case object WorkAvailable
  case class DoWork(p: Path)
  case class GetWork(worker: ActorRef)
  case class FinishedWork(worker: ActorRef, p: Path)
  case class FailedWork(worker: ActorRef, p: Path)
  case class RegisterWorker(worker: ActorRef)

  private type Accumulator = ActorRef
}

class AnalysisWorker(l: Seq[(Analyzer[_],String,Accumulator)], fileService: FileService, failedFilesPath: Path, master: ActorRef) extends Actor with ActorLogging {
  import context._

  override def receive: Receive = idle

  override def preStart(): Unit = master ! RegisterWorker(self)

  def idle: Receive = {
    case WorkAvailable => master ! GetWork(self)
    case DoWork(p) =>
      become(working)
      Future {
        try {
          implicit val timeout = Timeout(10 minutes)
          val json = Json.parse(fileService.readAllText(p))
          val future = Future.sequence(l.map {
            case (analyzer, _, accumulator) => accumulator ? Add(analyzer(json))
          })
          Await.ready(future, Duration.Inf)
          log.info("analyzed {}", p.toString)
          FinishedWork(self, p)
        } catch {
          case e: Throwable =>
            try {
              log.error(e, "failed to process {}", p.toString)
              fileService.move(p, failedFilesPath.resolve(p.getFileName))
              FailedWork(self, p)
            } catch {
              case e: Throwable => log.error(e, "unable to move file {}", p.toString)
                FailedWork(self, p)
            }
        }
      } pipeTo self
  }

  def working: Receive = {
    case FinishedWork(worker, p) =>
      master ! FinishedWork(worker, p)
      master ! GetWork(self)
      become(idle)
    case FailedWork(worker, p) =>
      master ! FailedWork(worker, p)
      master ! GetWork(self)
      become(idle)
  }
}
