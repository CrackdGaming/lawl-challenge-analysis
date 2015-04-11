package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.testkit.TestActorRef
import com.crackd.lawlchallenge.abstraction.FileService
import com.crackd.lawlchallenge.actor.Bus.GameDataAvailable
import com.crackd.lawlchallenge.actor.GameDataImporter.FileCreated
import com.crackd.test.AkkaUnitSpec
import play.api.libs.json.JsNull

/**
 * Created by trent ahrens on 4/10/15.
 */
class GameDataImporterTest extends AkkaUnitSpec {
  val fileService = mock[FileService]
  val path = mock[Path]
  val failedPath = mock[Path]
  val sut = TestActorRef(new GameDataImporter(fileService, testActor, failedPath))

  it should "send created files data to the bus" in {
    (fileService.readAllText _).expects(path).returns(JsNull.toString())

    sut ! FileCreated(path)

    expectMsg(GameDataAvailable(JsNull,path))
  }

  "when parsing failed it" must "delete the file" in {
    (fileService.readAllText _).expects(path).returning("badjson")
    (path.getFileName _).expects().returning(path)
    (failedPath.resolve(_:Path)).expects(path).returning(failedPath)
    (fileService.move _).expects(path, failedPath)

    sut ! FileCreated(path)
  }
}
