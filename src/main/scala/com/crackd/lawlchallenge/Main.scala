package com.crackd.lawlchallenge

import java.nio.file.{Files, FileSystems}

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import com.crackd.lawlchallenge.abstraction.DefaultFileService
import com.crackd.lawlchallenge.actor.{GameDataImporter, Bus, Journaler, AnalysisEngine}
import com.crackd.lawlchallenge.analysis.aggregate.DefaultAnalyzerAggregate
import com.crackd.lawlchallenge.analysis.analyzer._
import com.crackd.lawlchallenge.io.GameDataWatcher
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import play.api.libs.json._
import spray.can.Http

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
*  Created by trent ahrens on 4/7/15.
*/
object Main extends App {
  implicit val system = ActorSystem()
  implicit val timeout = Timeout(6 seconds)

  val log = LoggerFactory.getLogger(getClass)
  val config = ConfigFactory.load()
  val fileSystem = FileSystems.getDefault
  val fileService = new DefaultFileService
  val snapshot = fileSystem.getPath(config.getString("folders.snapshot"))
  val archive = fileSystem.getPath(config.getString("folders.archive"))
  val failedImports = fileSystem.getPath(config.getString("folders.failedImports"))
  val imports = fileSystem.getPath(config.getString("folders.import"))

  val aggregates = Vector(
    new DefaultAnalyzerAggregate(new GameAnalyzer, "game"),
    new DefaultAnalyzerAggregate(new HeatMapAnalyzer, "heatmap"),
    new DefaultAnalyzerAggregate(new ChampionKillsAnalyzer, "championkills"),
    new DefaultAnalyzerAggregate(new ParticipantAnalyzer, "participant"),
    new DefaultAnalyzerAggregate(new ChampionsAnalyzer, "champions")
  )

  if (Files.exists(snapshot)) {
    log.info("restoring snapshot {}", snapshot.toString)
    val snapshotData = Json.parse(FileUtils.readFileToString(snapshot.toFile))
    aggregates.foreach(a => a.restore(snapshotData \ a.name))
  }

  val analysisEngine = system.actorOf(Props(classOf[AnalysisEngine], aggregates), "AnalysisEngine")
  val journaler = system.actorOf(Props(classOf[Journaler], fileService, snapshot, archive))
  var bus = system.actorOf(Props(classOf[Bus], analysisEngine, journaler))
  val importer = system.actorOf(Props(classOf[GameDataImporter], fileService, bus, failedImports))
  val fileWatcher = new GameDataWatcher(imports, importer)
  fileWatcher.start()

  val apiService = system.actorOf(Props(classOf[RestApi], analysisEngine))
  IO(Http) ? Http.Bind(apiService, interface = "::0", port = Integer.valueOf(Option(System.getenv("PORT")).getOrElse("6437")))
}
