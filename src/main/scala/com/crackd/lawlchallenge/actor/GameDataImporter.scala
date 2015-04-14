package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.crackd.lawlchallenge.actor.Bus.GameDataAvailable
import com.crackd.lawlchallenge.actor.CommonMessages._
import com.crackd.lawlchallenge.actor.GameDataImporter._

import scala.collection.mutable

/**
 * Created by trent ahrens on 4/10/15.
 */
object GameDataImporter {
  case class FileCreated(p: Path)
  case class FileRemoved(p: Path)

  private case object Dequeue
}

class GameDataImporter(bus: ActorRef) extends Actor with ActorLogging {
  import context._

  val queue: mutable.Queue[Path] = mutable.Queue.empty

  override def receive: Receive = running orElse default

  def running: Receive = {
    case Suspend =>
      log.info("suspending importer")
      become(suspended orElse default)
    case Dequeue => bus ! GameDataAvailable(queue.dequeue())
  }

  def suspended: Receive = {
    case Resume =>
      log.info("resuming importer")
      become(running orElse default)
  }

  def default: Receive = {
    case FileCreated(p) =>
      log.info("file became available for processing {}", p.toString)
      queue.enqueue(p)
      self ! Dequeue
  }
}