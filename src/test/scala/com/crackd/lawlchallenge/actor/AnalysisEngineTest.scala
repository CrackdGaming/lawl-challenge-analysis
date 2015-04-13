package com.crackd.lawlchallenge.actor

import akka.testkit.TestActorRef
import com.crackd.lawlchallenge.actor.AnalysisEngine.{Analysis, Analyze}
import com.crackd.lawlchallenge.analysis.aggregate.AnalyzerAggregate
import com.crackd.test.AkkaUnitSpec
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/10/15.
 */
class AnalysisEngineTest extends AkkaUnitSpec {
  val aggregate1 = mock[AnalyzerAggregate]
  val aggregate2 = mock[AnalyzerAggregate]
  val sut = TestActorRef(new AnalysisEngine(Vector(aggregate1, aggregate2)))

  "an analysis" should "respond with aggregated result" in {
    (aggregate1.apply _).expects(JsNull)
    (aggregate2.apply _).expects(JsNull)
    (aggregate1.name _).expects().returns("aggregate1")
    (aggregate1.capture _).expects().returns(JsNull)
    (aggregate2.name _).expects().returns("aggregate2")
    (aggregate2.capture _).expects().returns(JsNull)

    sut ! Analyze(JsNull)
    val expected = Json.obj(
      "aggregate1" -> JsNull,
      "aggregate2" -> JsNull
    )
    expectMsg(Analysis(expected))
  }
}