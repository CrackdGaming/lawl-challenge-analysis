package com.crackd.lawlchallenge

import java.nio.file.{FileSystems, Files}

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import com.crackd.lawlchallenge.abstraction.DefaultFileService
import com.crackd.lawlchallenge.actor.AnalysisAccumulator.Deserialize
import com.crackd.lawlchallenge.actor._
import com.crackd.lawlchallenge.analysis.analyzer._
import com.crackd.lawlchallenge.analysis.model.ChampionKillsModels.{ChampionKills, ChampionKillsMonoid, championKillsFormat}
import com.crackd.lawlchallenge.analysis.model.ChampionsModels.{Champions, ChampionsMonoid, championsFormat}
import com.crackd.lawlchallenge.analysis.model.GameModels.{Game, GameMonoid, gameFormat}
import com.crackd.lawlchallenge.analysis.model.HeatMapModels.{HeatMaps, HeatMapsMonid, heatMapsFormat}
import com.crackd.lawlchallenge.analysis.model.StatModels.{Stats, StatsMonoid, statsFormat}
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

  val gameAccumulator = system.actorOf(Props(classOf[AnalysisAccumulator[Game]], GameMonoid, gameFormat, "game"))
  val heatmapAccumulator = system.actorOf(Props(classOf[AnalysisAccumulator[HeatMaps]], HeatMapsMonid, heatMapsFormat, "heatmap"))
  val championKillsAccumulator = system.actorOf(Props(classOf[AnalysisAccumulator[ChampionKills]], ChampionKillsMonoid, championKillsFormat, "championkills"))
  val participantAccumulator = system.actorOf(Props(classOf[AnalysisAccumulator[Stats]], StatsMonoid, statsFormat, "participant"))
  val championAccumulator = system.actorOf(Props(classOf[AnalysisAccumulator[Champions]], ChampionsMonoid, championsFormat, "champions"))
  val accumulators = Vector(gameAccumulator,heatmapAccumulator,championKillsAccumulator,participantAccumulator,championAccumulator)

  val analyzers = Vector(
    (new GameAnalyzer, "game", gameAccumulator),
    (new HeatMapAnalyzer, "heatmap", heatmapAccumulator),
    (new ChampionKillsAnalyzer, "championkills", championKillsAccumulator),
    (new ParticipantAnalyzer, "participant", participantAccumulator),
    (new ChampionsAnalyzer, "champions", championAccumulator)
  )

  if (Files.exists(snapshot)) {
    log.info("restoring snapshot {}", snapshot.toString)
    val snapshotData = Json.parse(FileUtils.readFileToString(snapshot.toFile))
    analyzers.foreach {
      case (_, name, accumulator) => accumulator ! Deserialize(snapshotData \ name)
    }
  }

  val analysisEngine = system.actorOf(Props(classOf[AnalysisEngine], accumulators), "AnalysisEngine")
  val journaler = system.actorOf(Props(classOf[Journaler], fileService, snapshot, archive))
  val bus: ActorRef = system.actorOf(Props(classOf[Bus], analysisEngine, journaler))
  val importer = system.actorOf(Props(classOf[GameDataImporter], bus))
  val fileWatcher = new GameDataWatcher(imports, importer)
  fileWatcher.start()

  for (i <- 1 to 8) {
    system.actorOf(Props(classOf[AnalysisWorker], analyzers, fileService, failedImports, analysisEngine))
  }

  val apiService = system.actorOf(Props(classOf[RestApi], bus))
  IO(Http) ? Http.Bind(apiService, interface = "::0", port = Integer.valueOf(Option(System.getenv("PORT")).getOrElse("6437")))
}
