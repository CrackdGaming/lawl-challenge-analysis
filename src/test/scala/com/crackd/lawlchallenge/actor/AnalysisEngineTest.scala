package com.crackd.lawlchallenge.actor

import akka.testkit.TestActorRef
import com.crackd.lawlchallenge.actor.AnalysisEngine.Analyze
import com.crackd.lawlchallenge.analysis.aggregate.AnalyzerAggregate
import com.crackd.test.AkkaUnitSpec
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/10/15.
 */
class AnalysisEngineTest extends AkkaUnitSpec {
  "an analysis" should "apply the result to all aggregates" in {
    val aggregate1 = mock[AnalyzerAggregate]
    val aggregate2 = mock[AnalyzerAggregate]
    (aggregate1.apply _).expects(JsNull).twice()
    (aggregate2.apply _).expects(JsNull).twice()

    val sut = TestActorRef(new AnalysisEngine(Vector(aggregate1, aggregate2)))
    sut ! Analyze(JsNull)
    sut ! Analyze(JsNull)
  }
}