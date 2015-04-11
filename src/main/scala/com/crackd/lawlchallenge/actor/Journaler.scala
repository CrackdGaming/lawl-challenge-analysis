package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.Status.Failure
import akka.actor.{ActorLogging, Props, Actor}
import akka.pattern.pipe
import com.crackd.lawlchallenge.abstraction.FileService
import com.crackd.lawlchallenge.actor.JournalIo.JournalUpdateComplete
import com.crackd.lawlchallenge.actor.Journaler.Journal
import play.api.libs.json.JsValue

import scala.collection.mutable
import scala.concurrent.Future

/**
 * Created by trent ahrens on 4/10/15.
 */
object Journaler {
  case class Journal(p: Path, analysis: JsValue)
}

class Journaler(fileService: FileService, snapshot: Path, archive: Path) extends Actor with ActorLogging {
  import context._

  val io = context.actorOf(Props(classOf[JournalIo], fileService, snapshot, archive))
  val queue: mutable.Queue[Journal] = mutable.Queue.empty

  override def receive: Receive = idle

  def idle: Receive = {
    case e @ Journal(_,_) =>
      io ! e
      become(working)
  }

  def working: Receive = {
    case JournalUpdateComplete =>
      if (queue.nonEmpty) io ! queue.dequeue()
      else become(idle)
    case e @ Journal(_,_) => queue.enqueue(e)
    case Failure(e) => println(e.toString)
  }
}

object JournalIo {
  case object JournalUpdateComplete
}

class JournalIo(fileService: FileService, snapshot: Path, archive: Path) extends Actor with ActorLogging {
  import context._
  override def receive: Actor.Receive = {
    case Journal(p, analysis) =>
      Future {
        try {
          fileService.writeAllText(snapshot, analysis.toString())
          fileService.move(p, archive.resolve(p.getFileName))
          log.info("processed file {}", p.toString)
          JournalUpdateComplete
        } catch {
          case e: Throwable => Failure(e)
        }
      } pipeTo parent
  }
}