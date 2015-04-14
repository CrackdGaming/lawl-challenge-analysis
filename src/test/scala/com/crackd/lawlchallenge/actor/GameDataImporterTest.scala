package com.crackd.lawlchallenge.actor

import java.nio.file.Path

import akka.testkit.TestActorRef
import com.crackd.lawlchallenge.abstraction.FileService
import com.crackd.test.AkkaUnitSpec

/**
 * Created by trent ahrens on 4/10/15.
 */
class GameDataImporterTest extends AkkaUnitSpec {
  val fileService = mock[FileService]
  val path = mock[Path]
  val failedPath = mock[Path]
  val sut = TestActorRef(new GameDataImporter(testActor))
  /*
  it should "send created files data to the bus" in {
    (fileService.readAllText _).expects(path).returns(JsNull.toString())

    sut ! FileCreated(path)

    expectMsg(GameDataAvailable(JsNull, path))
  }

  it should "suspend when requested" in {
    sut ! Suspend

    sut ! FileCreated(path)
    sut ! FileCreated(path)

    expectNoMsg()
  }

  it should "resume when requested" in {
    (fileService.readAllText _).expects(path).returns(JsNull.toString()).twice()

    sut ! Suspend

    sut ! FileCreated(path)
    sut ! FileCreated(path)

    sut ! Resume

    expectMsg(GameDataAvailable(JsNull, path))
    expectMsg(GameDataAvailable(JsNull, path))
  }
  "when parsing failed it" must "move the file to failed imports" in {
    (fileService.readAllText _).expects(path).returning("badjson")
    (path.getFileName _).expects().returning(path)
    (failedPath.resolve(_: Path)).expects(path).returning(failedPath)
    (fileService.move _).expects(path, failedPath)

    sut ! FileCreated(path)

    expectNoMsg()
  }
  */
}
