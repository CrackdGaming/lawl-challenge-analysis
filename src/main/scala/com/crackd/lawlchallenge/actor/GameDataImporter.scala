package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.actor.{ActorLogging, Actor, ActorRef}
import com.crackd.lawlchallenge.abstraction.FileService
import com.crackd.lawlchallenge.actor.Bus.GameDataAvailable
import com.crackd.lawlchallenge.actor.GameDataImporter.FileCreated
import play.api.libs.json._

import scala.concurrent.Future

/**
 * Created by trent ahrens on 4/10/15.
 */
object GameDataImporter {
  case class FileCreated(p: Path)
  case class FileRemoved(p: Path)
}

class GameDataImporter(fileService: FileService, bus: ActorRef, failedFilesPath: Path) extends Actor with ActorLogging {
  import context._
  override def receive: Receive = {
    case FileCreated(p) =>
      log.info("file became available for processing {}", p.toString)
      Future {
        try {
          bus ! GameDataAvailable(Json.parse(fileService.readAllText(p)), p)
        } catch {
          case _: Throwable => fileService.move(p, failedFilesPath.resolve(p.getFileName))
        }
      }
  }
}