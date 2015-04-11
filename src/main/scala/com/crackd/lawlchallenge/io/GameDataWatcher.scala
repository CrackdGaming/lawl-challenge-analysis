package com.crackd.lawlchallenge.io

import java.nio.file.StandardWatchEventKinds._
import java.nio.file.{FileSystems, Path, WatchEvent}
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import com.crackd.lawlchallenge.actor.GameDataImporter.{FileCreated, FileRemoved}
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import scala.util.control.Breaks._

/**
 * Created by trent ahrens on 4/10/15.
 */
class GameDataWatcher(path: Path, handler: ActorRef) {
  val watcher = FileSystems.getDefault.newWatchService()
  path.register(watcher, ENTRY_CREATE, ENTRY_DELETE)

  var thread: Thread = null
  @volatile var shutdown: Boolean = false

  def start() = {
    thread = new Thread {
      override def run(): Unit = {
        FileUtils.iterateFiles(path.toFile, List("json").toArray, false).asScala.foreach(f => handler ! FileCreated(f.toPath))
        breakable {
          while (!shutdown) {
            val key = watcher.poll(5, TimeUnit.SECONDS)

            if (key != null) {
              key.pollEvents().asScala.foreach(e => e.kind() match {
                case ENTRY_CREATE | ENTRY_DELETE =>
                  val p = path.resolve(e.asInstanceOf[WatchEvent[Path]].context())
                  if (p.getFileName.toString.endsWith("json")) {
                    e.kind() match {
                      case ENTRY_CREATE => handler ! FileCreated(p)
                      case ENTRY_DELETE => handler ! FileRemoved(p)
                    }
                  }
                case _ =>
              })

              if (!key.reset()) break()
            }
          }
        }
      }
    }
    thread.start()
  }

  def stop(): Unit = shutdown = true
}
